package Coconut;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Engine extends Canvas {
    private final JFrame frame;
    private final Renderer renderer;
    private final String title, entities, boxes, lights, cameraAttach;
    private final double tps;
    private boolean running = false;
    private final Physics physics;
    private static final ArrayList<Entity> entityList = new ArrayList<>();
    public static final ArrayList<Light> lightList = new ArrayList<>();
    private static final ArrayList<Entity> removeList = new ArrayList<>();
    private static final ArrayList<Entity> addList = new ArrayList<>();
    private BufferedImage overlay = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    private Entity cameraEntity = new BasicEntity();
    private final int numLayers, baseLightLevel;
    public static int width, height;
    public static double scale;
    public final Input input = new Input();
    public static Vec2 cameraPos = new Vec2();
    public static boolean lightsEnabled;

    public static void main(String[] args) {
        // Create a non-static instance of the Engine class
        new Engine();
    }

    public Engine() {
        Properties properties = new Properties();																		// Load properties from the default location of game/launch.properties
        try {
            properties.load(new FileInputStream("game/launch.properties"));
        }catch(IOException e) {
            System.out.println("Unable to load launch.properties");
        }																												//  key				||  purpose
        scale = Double.parseDouble(properties.getProperty("scale", "1"));								//  scale			||  factor by which to scale whatever is drawn on the screen
        width = Integer.parseInt(properties.getProperty("width", "800"));						//  width			||  starting width of the window
        height = Integer.parseInt(properties.getProperty("height", "600"));						//  height			||  starting height of the window
        title = properties.getProperty("title", "A Game");												//  title			||  title of the window
        entities = properties.getProperty("entities", "/");												//  entities		||  location of the directory in which the entity .properties files are stored
        boxes = properties.getProperty("boxes", "/");													//  boxes			||	location of the directory in which the hitboxes .properties files are stored
        lights = properties.getProperty("lights", "/");													//  lights			||	location of the directory in which the lights .properties files are stored
        tps = Double.parseDouble(properties.getProperty("tps", "60"));									//  tps				||	target ticks per second of the game, aka how fast the game should update
        cameraAttach = properties.getProperty("camera_attach", "");										//  camera_attach	||	name of the entity to which the camera should follow
        numLayers = Integer.parseInt(properties.getProperty("num_layers", "10"));						//  num_layers		||	number of render layers that the renderer should render
        baseLightLevel = 255 - Integer.parseInt(properties.getProperty("light_level", "250"));			//  light_level		||	the base light level of the game on a scale of 0 - 255, only applies if lights are enabled
        lightsEnabled = Boolean.parseBoolean(properties.getProperty("lights_enabled", "true"));			//  lights_enabled	||	should the engine process lights and render them

        loadEntities();																									//	load Entities,
        loadBoxes();																									//	Hitboxes,
        loadLights();																									//	and Lights.

        renderer = new Renderer();																						//	initiate the renderer and physics
		physics = new Physics(entityList);

        this.addKeyListener(input);																						//	set up the canvas with inputs and size
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        this.setPreferredSize(new Dimension(width, height));


        frame = new JFrame(title);																						//	set up the window container with the icon stored in game/res/cover.png
        try {
            frame.setIconImage(ImageIO.read(new File("game/res/cover.png")));
        } catch(IOException ignored) {}
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(input);
        frame.addMouseListener(input);
        frame.addMouseMotionListener(input);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);

        start();
    }

    public void loadEntities() {
        if(!Objects.equals(entities, "/")) {																			//	make sure that the location we're loading exists
            Path folderPath = Paths.get(entities);																		//	get the path of the location for use in a directory stream
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {						//	set up the directory stream
                for (Path file : directoryStream) {																		//	iterate over every file in the directory
                    if (Files.isRegularFile(file)) {																	//	make sure we're only using actual files and not folders to avoid user hierarchy systems
                        Properties properties = new Properties();														//	assume that each file is a properties file and load its properties
                        try {
                            properties.load(new FileInputStream(file.toString()));
                        } catch (IOException e) {
                            System.out.println("Unable to load properties file for " + file);
                        }

                        Entity entity;
                        try {
                            String codePath = properties.getProperty("code");											//	attach the entity to whatever class is listed in the .properties file
                            if(codePath == null)
                                entity = new BasicEntity();																//	make the entity just a basicEntity if no class is specified
                            else {
                                codePath = codePath.replace('/', '.');
                                Class<?> playerClass = loadClass(codePath);
                                entity = (Entity) playerClass.getDeclaredConstructor().newInstance();
                            }																							//	key				||	purpose
                            entity.setName(properties.getProperty("name"));												//	name			||	what name the entity will be given. referenced by collision systems and to check camera and light attaches
                            entity.setPos(new Vec2(Double.parseDouble(													//	pos_x & pos_y	||	position where the entity will be rendered, and starting pos for child hitboxes
									properties.getProperty("pos_x", "0")),								//					||
									Double.parseDouble(properties.getProperty("pos_y", "0"))));			//					||
                            entity.setSize(new Vec2(Double.parseDouble(properties.getProperty("size_x")),				//	size_x & size_y	||	size of the entity, used by the renderer to determine how big it should render the entity's texture
									Double.parseDouble(properties.getProperty("size_y"))));								//					||
                            String texture = properties.getProperty("texture");											//	texture			||	location of the texture that should correspond to the entity
                            if (!Objects.isNull(texture))																//					||
                                entity.setTexture(ImageIO.read(new File(texture)));										//					||
                            entity.setCollidesWith(new ArrayList<>(Arrays.asList(										//	collides_with	||	list of the names of entities that this entity should collide with
									properties.getProperty("collides_with", "").split(","))));	//					||
                            if (Objects.equals(cameraAttach, properties.getProperty("name")))							//					||
                                cameraEntity = entity;																	//					||
                            entity.setLayer(Integer.parseInt(properties.getProperty("layer", "-1")));	//	layer			||	which layer should this entity be rendered on
                            entity.setIndex(entityList.size());															//					||
                            entity.setMass(Integer.parseInt(properties.getProperty("mass", "-1")));		//	mass			||	mass of the entity, determines how other objects should interact when collided with
                            entityList.add(entity);
                        } catch (Exception e) {
                            System.out.println("Error compiling " + properties.getProperty("name") + " class");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading entity folder");
            }
        }
    }

    private void loadBoxes() {
        if(!Objects.equals(boxes, "/")) {																			//	make sure that the location we're loading exists
            Path folderPath = Paths.get(boxes);																			//	get the path of the location for use in a directory stream
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {						//	set up the directory stream
                for (Path file : directoryStream) {																		//	iterate over every file in the directory
                    if (Files.isRegularFile(file)) {																	//	make sure we're only using actual files and not folders to avoid user hierarchy systems
                        Properties properties = new Properties();														//	treat each file in that folder as a properties file and load properties from it
                        try {
                            properties.load(new FileInputStream(file.toString()));
                        } catch (IOException e) {
                            System.out.println("Unable to load properties for "
									+ properties.getProperty("name") +" hitbox");
                        }
																														//	key			||	purpose
                        boolean box = Boolean.parseBoolean(properties.getProperty("box", "true"));		//	box			||	is the hitbox being defined a regular box, or a more advanced polygon
                        String attach = properties.getProperty("attach");												//	attach		||	what entity should the hitbox be attached to
                        if (attach != null) {																			//				||
                            Entity attachEntity = new BasicEntity();													//				||
                            for (Entity entity : entityList)															//				||
                                if(Objects.equals(entity.getName(), attach))											//				||
                                    attachEntity = entity;																//				||
                            if (!box) {																					//				||	if the hitbox is not a regular box
                                String[] xPointsRaw = properties.getProperty("x_points").split(",");				//	x_points	||	list of the x coordinates for each of the hitbox's points
                                String[] yPointsRaw = properties.getProperty("y_points").split(",");				//	y_points	||	list of the y coordinates for each of the hitbox's points
                                double x = Double.parseDouble(properties.getProperty("pos_x", "0"));	//	pos_x		||	starting x position of the hitbox in reference to the entities position
                                double y = Double.parseDouble(properties.getProperty("pos_y", "0"));	//	pos_y		||	starting y position of the hitbox in reference to the entities position

                                int xLength = xPointsRaw.length;
                                int yLength = yPointsRaw.length;
																														//	create a polygon based on the points given
                                assert xLength == yLength;
                                int[] xPoints = new int[xLength];
                                int[] yPoints = new int[yLength];
                                for (int i = 0; i < xPointsRaw.length; i++) {
                                    xPoints[i] = Integer.parseInt(xPointsRaw[i]);
                                    yPoints[i] = -Integer.parseInt(yPointsRaw[i]);
                                }
                                Polygon hitbox = new Polygon(xPoints, yPoints, xLength);

                                ArrayList<Hitbox> hitboxes = attachEntity.getHitboxes();
                                if (hitboxes == null) {
                                    hitboxes = new ArrayList<>();
                                    hitboxes.add(new Hitbox(hitbox, new Vec2(x, y)));
                                    attachEntity.setHitboxes(hitboxes);
                                }
                                else attachEntity.getHitboxes().add(new Hitbox(hitbox, new Vec2(x, y)));
                            } else {																					//	key		||	purpose
                                double w = Double.parseDouble(															//			||
										properties.getProperty("size_x", "0"));							//	size_x	||	width of the hitbox being defined
                                double h = Double.parseDouble(															//			||
										properties.getProperty("size_y", "0"));							//	size_y	||	height of the hitbox being defined
                                double x = Double.parseDouble(															//			||
										properties.getProperty("pos_x", "0")) - w / 2;					//	pos_x	||	origin x pos of the hitbox
                                double y = Double.parseDouble(															//			||
										properties.getProperty("pos_y", "0")) - h / 2;					//	pos_y	||	origin y pos of the hitbox

                                ArrayList<Hitbox> hitboxes = attachEntity.getHitboxes();
                                if (hitboxes == null) {
                                    hitboxes = new ArrayList<>();
                                    hitboxes.add(new Hitbox(attachEntity.getPos().plus(new Vec2(x, y)),
											new Vec2(w, h)));
                                    attachEntity.setHitboxes(hitboxes);
                                }
                                else attachEntity.getHitboxes().add(new Hitbox(new Vec2(x, y)
										.plus(attachEntity.getPos()), new Vec2(w, h)));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading hitbox folder");
            }
        }
    }

    private void loadLights() {
        if(!Objects.equals(lights, "/")) {																			//	make sure that the location we're loading exists
            Path folderPath = Paths.get(lights);																		//	get the path of the location for use in a directory stream
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {						//	set up the directory stream
                for (Path file : directoryStream) {																		//	iterate over every file in the directory
                    if (Files.isRegularFile(file)) {																	//	make sure we're only using actual files and not folders to avoid user hierarchy systems
                        Properties properties = new Properties();														//	treat each file in that folder as a properties file and load properties from it
                        try {
                            properties.load(new FileInputStream(file.toString()));
                        } catch (IOException e) {
                            System.out.println("Unable to load properties for "
									+ properties.getProperty("name") + " light");
                        }
																														//	key			||	purpose
                        double posx = Double.parseDouble(properties.getProperty("pos_x", "0"));			//	pos_x		||	x position of the light
                        double posy = Double.parseDouble(properties.getProperty("pos_y", "0"));			//	pos_y		||	y position of the light
                        double radius = Double.parseDouble(properties.getProperty("radius", "10"));		//	radius		||	radius of the light, note: no correlation between pixels or render size or anything, just an arbitrary value you have to mess around with until it look right
                        String attach = properties.getProperty("attach", "");							//	attach		||	which (if any) entity to attach this light to
                        Engine.lightList.add(new Light(new Vec2(posx, posy), radius, attach));
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading light folder");
            }
        }
    }

    private synchronized void start() {
        running = true;																									//	set the running status to true and run the main loop
        run();
    }

    public void run() {
        long lastTime = System.nanoTime();																				//	define time keeping variables
        long now;
        long timer = System.currentTimeMillis();
        int frames = 0;
        double delta;
        double sps = 0;

        while(running) {
            now = System.nanoTime();																					//	update the current time
            delta = (now - lastTime) / 1_000_000_000.;																	//	calculate the delta to the last update in seconds
            if(delta >= 1 / tps) {																						//	update if the delta has been more than 1 / tps, the change in time that should be between 2 frames
                lastTime += (int)(1_000_000_000 / tps);																	//	increment the lastTime variable by how long it should have taken
                update(1 / tps);																					//	pass in 1 / tps as the delta time. stored in seconds
                sps += 1 / tps;																							//	update the seconds per theoretical second variable
            }
            frames ++;																									//	increase the number of frames rendered
            render();																									//	render the current frame
            if (System.currentTimeMillis() - timer >= 1000) {															//	update the fps counter every second
                timer += 1000;																							//	increase the timer by a second
                frame.setTitle(title + " | " + frames + " fps" + " | " + Math.round(sps * tps) + " tps");				//	set the title of the window to have the current tps
                frames = 0;																								//	reset the frames variable for the next second of runtime
                sps = 0;																								//	reset the seconds per theoretical second variable for the next second of runtime
            }
        }
        System.exit(0);
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();																	//	get the canvas' current buffer strategy and create it if it doesn't exist
        if(bs == null) {
            this.createBufferStrategy(4);
            return;
        }

        Graphics g = bs.getDrawGraphics();																				//	get the graphics to be able to draw
        g.setColor(Color.black);																						//	set the background color to black
        g.fillRect(0, 0, width, height);																			//	clear the screen with the background color

        cameraPos = cameraEntity.getPos();																				//	update the camera position based on the camera entity

        renderer.update(g);																								//	update the custom renderer with the current graphics

        for(int i = 0; i < numLayers; i++)																				//	render each entity on its layer
            for(Entity entity : entityList)
                if(entity.getLayer() == i)
                    entity.render(renderer);

        if(lightsEnabled)																								//	render the light overlay if lights are enabled
            g.drawImage(overlay, 0, 0, width, height, null);

        for(Entity entity : entityList)																					//	render any entities that don't have a layer ex. menus or dialog boxes
            if(entity.getLayer() == -1)
                entity.render(renderer);

        g.dispose();
        bs.show();
    }

    private void update(double delta) {
        width = this.getWidth();																						//	update the static variables of width and height for use in the render class
        height = this.getHeight();

        if(lightsEnabled)																								//	update the light overlay if lights are enabled
            updateOverlay();

        input.update(width, height, scale);																				//	update the user input

        physics.update();																								//	update the physics system

        for (Entity entity : entityList) {																				//	update each entity
            entity.update(input, delta);
            if(lightsEnabled)
                for (Light light : lightList)
                    if (Objects.equals(light.attach, entity.getName()))
                        light.pos = new Vec2(entity.getPos());
        }

        updateEntityList();																								//	update the entity list. needs to be separate so that it doesn't break the entity loop
    }

    private void updateOverlay() {
        int imageWidth = (int) (width / scale);																			//	get the scaled width and height
        int imageHeight = (int) (height / scale);

        overlay = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);							//	define a new image for the overlay

        Graphics2D overlayGraphics = overlay.createGraphics();															//	get the graphics in order to draw different light levels
        overlayGraphics.setColor(new Color(0, 0, 0, baseLightLevel));											//	set the background of the entire image to be the base light level
        overlayGraphics.fillRect(0, 0, imageWidth, imageHeight);

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                for (Light light : lightList) {
                    Vec2 point = new Vec2(i, -j).plus(cameraPos).minus(													//	calculate the brightness of the pixel based on its distance to the light
							new Vec2(imageWidth / 2., -imageHeight / 2.));
                    double length = light.pos.minus(point).lengthSquared() / (light.radius * light.radius);
                    int alpha = (int) (length > baseLightLevel ? baseLightLevel : length);
                    int originalColor = overlay.getRGB(i, j);															//	get the original color of the pixel
                    int originalAlpha = (originalColor >> 24) & 0xFF;													//	extract the alpha channel
                    int newAlpha = Math.min(alpha, originalAlpha);														//	update the alpha to be the brightest of the previous color and the current
                    int newColor = (originalColor & 0x00FFFFFF) | (newAlpha << 24);

                    overlay.setRGB(i, j, newColor);																		//	update the color of the specific pixel
                }
            }
        }
    }

    private void updateEntityList() {
        for(Entity entity : removeList) {																				// apply the updates from the add and remove lists, then clear the update lists
            if(entity.getName() != null)
                if(Objects.equals(entity.getName(), cameraAttach))
                    cameraEntity = new BasicEntity();
            entityList.remove(entity);
        }
        for(Entity entity : addList) {
            if(entity.getName() != null)
                if(Objects.equals(entity.getName(), cameraAttach))
                    cameraEntity = entity;
            entityList.addFirst(entity);
        }
        removeList.clear();
        addList.clear();
    }

    private static Class<?> loadClass(String className) throws Exception {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("").toURI().toURL()});
        return Class.forName(className, true, classLoader);
    }

    public static void removeFromEntityList(Entity entity) {
        removeList.add(entity);
    }

    public static void addToEntityList(Entity entity) {
        addList.add(entity);
    }

    public static ArrayList<Entity> getEntityList() {
        return new ArrayList<>(Engine.entityList);
    }
}
