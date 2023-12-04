package engine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
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
    private boolean running = false;
    public static int width;
    public static int height;
    private final String title;
    private final double tps;
    private final String entities;
    private final String boxes;
    private final String cameraAttach;
    private final Renderer renderer;
    private final Input input = new Input();
    private final Physics physics;
    private static final ArrayList<Entity> entityList = new ArrayList<>();
    private static final ArrayList<Entity> removeList = new ArrayList<>();
    private static final ArrayList<Entity> addList = new ArrayList<>();
    public static Vec2 cameraPos = new Vec2();
    private Entity cameraEntity;
    private final int numLayers;

    public static void main(String[] args) {
        new Engine();
    }

    public Engine() {
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
//        tps = Math.random() * (144 - 60) + 60;
        double scale = Integer.parseInt(properties.getProperty("scale", "1"));
        entities = properties.getProperty("entities", "/");
        boxes = properties.getProperty("boxes", "/");
        cameraAttach = properties.getProperty("camera_attach", "");
        numLayers = Integer.parseInt(properties.getProperty("num_layers", "10"));

        loadEntities();
        loadBoxes();

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
        start();
    }

    public void loadEntities() {
        Path folderPath = Paths.get(entities);

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
            for(Path file : directoryStream) {
                if(Files.isRegularFile(file)) {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(file.toString()));
                    } catch (IOException e) {
                        System.out.println("Unable to load properties file for " + file);
                    }

                    Entity entity;
                    try {
                        String codePath = properties.getProperty("code");
                        codePath = codePath.substring(0, codePath.length() - 5).replace('/', '.');
                        Class<?> playerClass = loadClass(codePath);
                        entity = (Entity) playerClass.getDeclaredConstructor().newInstance();
                        entity.setName(properties.getProperty("name"));
                        entity.setPos(new Vec2(Double.parseDouble(properties.getProperty("x")), Double.parseDouble(properties.getProperty("y"))));
                        entity.setSize(new Vec2(Double.parseDouble(properties.getProperty("width")), Double.parseDouble(properties.getProperty("height"))));
                        entity.setTexture(Objects.requireNonNull(ImageIO.read(new File(properties.getProperty("texture")))));
                        entity.setCollidesWith(new ArrayList<>(Arrays.asList(properties.getProperty("collides_with", "").split(","))));
                        if(Objects.equals(cameraAttach, properties.getProperty("name")))
                            cameraEntity = entity;
                        entity.setLayer(Integer.parseInt(properties.getProperty("layer", "-1")));
                        entity.setIndex(entityList.size());
                        entityList.add(entity);
                    } catch(Exception e) {
                        System.out.println("Error compiling " + properties.getProperty("name") + " class");
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("Folder: \"" + entities + "\" does not exist.");
        }
    }

    private void loadBoxes() {
        Path folderPath = Paths.get(boxes);

        try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folderPath)) {
            for(Path file: directoryStream) {
                if(Files.isRegularFile(file)) {
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

    private synchronized  void start() {
        running = true;
        run();
    }

    private synchronized void stop() {
        running = false;
        System.exit(0);
    }

    public void run() {
        long lastTime = System.nanoTime();
        long now;
        long timer = System.currentTimeMillis();
        int frames = 0;
        double delta;
        double sps = 0;
        while(running) {
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
        stop();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        cameraPos = cameraEntity.getPos();

        renderer.update(g);

        for(int i = 0; i < numLayers; i++)
            for(Entity entity : entityList)
                if(entity.getLayer() == i)
                    entity.render(renderer);

        g.dispose();
        bs.show();

        Toolkit.getDefaultToolkit().sync();
    }

    private void update(double delta) {
        width = this.getWidth();
        height = this.getHeight();
        input.update();
        physics.update();
        for(Entity entity : entityList) {
            entity.update(input, delta);
        }
        updateEntityList();
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
        return Engine.entityList;
    }

    private void updateEntityList() {
		for(Entity entity : removeList)
            entityList.remove(entity);
        for(Entity entity : addList)
            entityList.add(0, entity);
        removeList.clear();
        addList.clear();
    }
}
