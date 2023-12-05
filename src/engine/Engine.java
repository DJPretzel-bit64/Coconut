package engine;

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
import java.util.List;

public class Engine extends Canvas {
    private final JFrame frame;
    public static boolean running = false;
    public static int width;
    public static int height;
    private final String title;
    private final double tps;
    private final String entities;
    private final String boxes;
    private final String lights;
    private final String cameraAttach;
    private final double scale;
    private final Renderer renderer;
    private final Input input = new Input();
    private final Physics physics;
    private static final ArrayList<Entity> entityList = new ArrayList<>();
    public static final ArrayList<Light> lightList = new ArrayList<>();
    private static final ArrayList<Entity> removeList = new ArrayList<>();
    private static final ArrayList<Entity> addList = new ArrayList<>();
    public static Vec2 cameraPos = new Vec2();
    private Entity cameraEntity;
    private final int numLayers;
    BufferedImage overlay = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

    public static void main(String[] args) {
        new Engine();
    }

    public Engine() {
        // Load properties from the default location of game/launch.properties
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("game/launch.properties"));
        }catch(IOException e) {
            System.out.println("Unable to load launch.properties");
        }
        width = Integer.parseInt(properties.getProperty("width", "800"));
        height = Integer.parseInt(properties.getProperty("height", "600"));
        title = properties.getProperty("title", "A Game");
        tps = Double.parseDouble(properties.getProperty("tps", "60"));
        scale = Double.parseDouble(properties.getProperty("scale", "1"));
        entities = properties.getProperty("entities", "/");
        boxes = properties.getProperty("boxes", "/");
        lights = properties.getProperty("lights", "/");
        cameraAttach = properties.getProperty("camera_attach", "");
        numLayers = Integer.parseInt(properties.getProperty("num_layers", "10"));

        // load entities, hitboxes, and lights
        loadEntities();
        loadBoxes();
        loadLights();

        // setup basic window stuff
        renderer = new Renderer(scale);
        this.addKeyListener(input);
        this.setPreferredSize(new Dimension(width, height));

        physics = new Physics(entityList);

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(input);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);

        // start the main loop
        start();
    }

    public void loadEntities() {
        // load entities from directory set in launch.properties
        Path folderPath = Paths.get(entities);
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
            for(Path file : directoryStream) {
                // for each file in the directory that is a regular file
                if(Files.isRegularFile(file)) {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(file.toString()));
                    } catch (IOException e) {
                        System.out.println("Unable to load properties file for " + file);
                    }

                    // define the entity and set hte properties based on its config file
                    Entity entity;
                    try {
                        // compile the entity linked in its properties file
                        String codePath = properties.getProperty("code");
                        codePath = codePath.substring(0, codePath.length() - 5).replace('/', '.');
                        Class<?> playerClass = loadClass(codePath);
                        entity = (Entity) playerClass.getDeclaredConstructor().newInstance();
                        entity.setName(properties.getProperty("name"));
                        entity.setPos(new Vec2(Double.parseDouble(properties.getProperty("x", "0")), Double.parseDouble(properties.getProperty("y", "0"))));
                        entity.setSize(new Vec2(Double.parseDouble(properties.getProperty("width")), Double.parseDouble(properties.getProperty("height"))));
                        entity.setTexture(Objects.requireNonNull(ImageIO.read(new File(properties.getProperty("texture")))));
                        entity.setCollidesWith(new ArrayList<>(Arrays.asList(properties.getProperty("collides_with", "").split(","))));
                        if(Objects.equals(cameraAttach, properties.getProperty("name")))
                            cameraEntity = entity;
                        entity.setLayer(Integer.parseInt(properties.getProperty("layer", "-1")));
                        entity.setIndex(entityList.size());
                        entityList.add(entity);
                    } catch(Exception e) {
                        e.printStackTrace();
                        System.out.println("Error compiling " + properties.getProperty("name") + " class");
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("Folder: \"" + entities + "\" does not exist.");
        }
    }

    private void loadBoxes() {
        // get the data for the hitboxes from the file defined in the launch.properties file
        Path folderPath = Paths.get(boxes);
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
            for(Path file: directoryStream) {
                if(Files.isRegularFile(file)) {
                    // for each regular file in that directory, create a hitbox based on the properties
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(file.toString()));
                    }
                    catch(IOException e) {
                        System.out.println("Unable to load properties for " + properties.getProperty("name") + " hitbox");
                    }

                    int w = Integer.parseInt(properties.getProperty("width", "0"));
                    int h = Integer.parseInt(properties.getProperty("height", "0"));
                    int x = Integer.parseInt(properties.getProperty("x", "0")) - w / 2;
                    int y = Integer.parseInt(properties.getProperty("y", "0")) - h / 2;

                    String attach = properties.getProperty("attach");
                    if(attach != null) {
                        for (Entity entity : entityList) {
                            if (Objects.equals(entity.getName(), attach)) {
                                List<Hitbox> hitboxes = entity.getHitboxes();
                                if(Objects.isNull(hitboxes)) {
                                    hitboxes = new ArrayList<>();
                                    hitboxes.add(new Hitbox(new Vec2(x + entity.getPos().x, y + entity.getPos().y), new Vec2(w, h)));
                                    entity.setHitboxes(hitboxes);
                                }
                                else
                                    entity.getHitboxes().add(new Hitbox(new Vec2(x + entity.getPos().x, y + entity.getPos().y), new Vec2(w, h)));
                            }
                        }
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("Error loading hitbox folder");
        }
    }

    private void loadLights() {
        // get the data for the lights from the file defined in the launch.properties file
        Path folderPath = Paths.get(lights);
        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
            for(Path file: directoryStream) {
                if(Files.isRegularFile(file)) {
                    // for each regular file in that directory, create a hitbox based on the properties
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(file.toString()));
                    }
                    catch(IOException e) {
                        System.out.println("Unable to load properties for " + properties.getProperty("name") + " light");
                    }

                    double posx = Double.parseDouble(properties.getProperty("posx", "0"));
                    double posy = Double.parseDouble(properties.getProperty("posy", "0"));
                    int radius = Integer.parseInt(properties.getProperty("radius", "10"));
                    String attach = properties.getProperty("attach", "");
                    Engine.lightList.add(new Light(new Vec2(posx, posy), radius, attach));
                }
            }
        } catch(IOException e) {
            System.out.println("Error loading light folder");
        }
    }

    private synchronized void start() {
        // set the running status to true and run the main loop
        running = true;
        run();
    }

    public void run() {
        // define time keeping variables
        long lastTime = System.nanoTime();
        long now;
        long timer = System.currentTimeMillis();
        int frames = 0;
        double delta;
        double sps = 0;

        while(running) {
            // code that makes it run at a consistent rate
            now = System.nanoTime();
            delta = (now - lastTime) / 1_000_000_000.;
            if(delta >= 1 / tps) {
                lastTime += (int)(1_000_000_000 / tps);
                update(1 / tps);
                sps += 1 / tps;
            }
            frames ++;
            render();
            if (System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
                frame.setTitle(title + " | " + frames + " fps" + " | " + Math.round(sps * tps) + " tps");
                frames = 0;
                sps = 0;
            }
        }
        // when the program ends, stop it and exit with status code 0
        System.exit(0);
    }

    private void render() {
        // get the buffer strategy and make it double buffered
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(2);
            return;
        }

        // get the drawGraphics
        Graphics g = bs.getDrawGraphics();

        // update the camera pos used
        cameraPos = cameraEntity.getPos();

        // update the renderer with the current graphics context
        renderer.update(g);

        // render each entity based on its layer number
        for(int i = 0; i < numLayers; i++)
            for(Entity entity : entityList)
                if(entity.getLayer() == i)
                    entity.render(renderer);

        drawOverlay(g);

        // apply the graphics context
        g.dispose();
        bs.show();
    }

    private void update(double delta) {
        // update the dimensions of the current window
        width = this.getWidth();
        height = this.getHeight();

        // update the user input and physics systems
        input.update();
        physics.update();

        // update each entity
        for(Entity entity : entityList) {
            entity.update(input, delta);
            for (Light light : lightList)
                if (Objects.equals(light.attach, entity.getName()))
                    light.pos = new Vec2(entity.getPos().divide(scale));
        }

        // update the entity list based on items scheduled to be added or removed
        updateEntityList();
    }

    private void drawOverlay(Graphics g) {
        int imageWidth = (int) (width / scale);
        int imageHeight = (int) (height / scale);

        overlay = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D overlayGraphics = overlay.createGraphics();
        overlayGraphics.setColor(new Color(0, 0, 0, 200));
        overlayGraphics.fillRect(0, 0, imageWidth, imageHeight);

        for (Light light : lightList) {
            for (int i = 0; i < imageWidth; i++) {
                for (int j = 0; j < imageHeight; j++) {
                    // calculate the light level of the pixel based on the length to the player
                    Vec2 point = new Vec2(i, -j).plus(cameraPos).minus(new Vec2(imageWidth / 2., -imageHeight / 2.)).divide(scale);
                    double length = Math.pow(light.pos.minus(point).length() / light.radius, 2);
                    int alpha = (int) Math.min((length), 220);

                    // Get the original color of the pixel
                    int originalColor = overlay.getRGB(i, j);

                    // Extract the original alpha value
                    int originalAlpha = (originalColor >> 24) & 0xFF;

                    // Update the alpha channel and set the new color
                    int newAlpha = Math.min(alpha, originalAlpha);
                    int newColor = (originalColor & 0x00FFFFFF) | (newAlpha << 24);

                    overlay.setRGB(i, j, newColor);
                }
            }
        }
        g.drawImage(overlay, 0, 0, width, height, null);
    }

    private static Class<?> loadClass(String className) throws Exception {
        // compile and load a class from its className
        URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("").toURI().toURL()});
        return Class.forName(className, true, classLoader);
    }

    public static void removeFromEntityList(Entity entity) {
        // add to a que to remove from the entity list
        removeList.add(entity);
    }

    public static void addToEntityList(Entity entity) {
        // add to a que to add to the entity list
        addList.add(entity);
    }

    public static ArrayList<Entity> getEntityList() {
        // allow access to the entity list
        return new ArrayList<>(Engine.entityList);
    }

    private void updateEntityList() {
        // apply the updates from the add and remove lists, then clear the update lists
		for(Entity entity : removeList) {
            entityList.remove(entity);
        }
        for(Entity entity : addList) {
            entityList.add(0, entity);
        }
        removeList.clear();
        addList.clear();
    }
}
