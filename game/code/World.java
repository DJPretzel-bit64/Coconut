package game.code;

import engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class World extends BasicEntity {
    BufferedImage NSEW, SEW, NEW, NSW, NSE, EW, NS, NE, NW ,SE ,SW, N, S, E, W, none, dark, combinedImage, combinedBackground;
    char[][] rawWorld;
    BufferedImage[][] fancyWorld;
    int worldWidth, worldHeight, totalWidth, totalHeight, lastWidth, lastHeight;
    String world;
    
    public World(String world) {
        // set up the world
        size = new Vec2(256, 32);
        pos = new Vec2();
        try {
            texture = ImageIO.read(new File("game/res/world.png"));
        } catch(IOException e) {
            System.out.println("unable to load world texture");
        }
        layer = 0;
        name = "World";
        hitboxes = new ArrayList<>();
        this.world = world;
        getRawWorld();
        setWorldHitbox();
        setTexture();
    }
    
    private void calculateFancyWorld() {
        // test relative locations of other world positions and set the texture based on those
        fancyWorld = new BufferedImage[worldWidth][worldHeight];
        for(int j = 0; j < worldHeight; j++) {
            for(int i = 0; i < worldWidth; i++) {
                if(rawWorld[i][j] == 'g') {
                    boolean north = false;
                    boolean south = false;
                    boolean east = false;
                    boolean west = false;
                    if(j == 0) south = true;
                    else if(rawWorld[i][j-1] == 'g') south = true;
                    if(j == worldHeight - 1) north = true;
                    else if(rawWorld[i][j+1] == 'g') north = true;
                    if(i == 0) west = true;
                    else if(rawWorld[i-1][j] == 'g') west = true;
                    if(i == worldWidth - 1) east = true;
                    else if(rawWorld[i+1][j] == 'g') east = true;
                    if(north && south && east && west) fancyWorld[i][j] = NSEW;
                    else if(south && east && west) fancyWorld[i][j] = SEW;
                    else if(north && east && west) fancyWorld[i][j] = NEW;
                    else if(north && south && west) fancyWorld[i][j] = NSW;
                    else if(north && south && east) fancyWorld[i][j] = NSE;
                    else if(east && west) fancyWorld[i][j] = EW;
                    else if(north && south) fancyWorld[i][j] = NS;
                    else if(north && east) fancyWorld[i][j] = NE;
                    else if(north && west) fancyWorld[i][j] = NW;
                    else if(south && east) fancyWorld[i][j] = SE;
                    else if(south && west) fancyWorld[i][j] = SW;
                    else if(north) fancyWorld[i][j] = N;
                    else if(south) fancyWorld[i][j] = S;
                    else if(east) fancyWorld[i][j] = E;
                    else if(west) fancyWorld[i][j] = W;
                    else fancyWorld[i][j] = none;
                }
            }
        }
    }

    public void setTexture() {
        // override the set texture function to define different tile textures
        NSEW = texture.getSubimage(0,  0 ,  32, 32);
        SEW  = texture.getSubimage(32, 0 ,  32, 32);
        NEW  = texture.getSubimage(32, 32,  32, 32);
        NSW  = texture.getSubimage(32, 64,  32, 32);
        NSE  = texture.getSubimage(32, 96,  32, 32);
        EW   = texture.getSubimage(64, 0,   32, 32);
        NS   = texture.getSubimage(64, 32,  32, 32);
        NE   = texture.getSubimage(64, 64,  32, 32);
        NW   = texture.getSubimage(64, 96,  32, 32);
        SE   = texture.getSubimage(64, 128, 32, 32);
        SW   = texture.getSubimage(64, 160, 32, 32);
        N    = texture.getSubimage(96, 0,   32, 32);
        S    = texture.getSubimage(96, 32,  32, 32);
        E    = texture.getSubimage(96, 64,  32, 32);
        W    = texture.getSubimage(96, 96,  32, 32);
        none = texture.getSubimage(128, 0,   32, 32);
        dark = texture.getSubimage(96,  160, 32, 32);

        // create an array of BufferedImages that represents the world
        calculateFancyWorld();

        // calculate the width and height of the combined world image
        totalWidth = 32 * worldWidth;
        totalHeight = 32 * worldHeight;

        // define the combinedTexture and get its draw graphics
        combinedImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = combinedImage.createGraphics();

        for(int i = 0; i < fancyWorld.length; i++) {
            for(int j = 0; j < fancyWorld[i].length; j++) {
                // draw the image in the array to the combined image
                g.drawImage(fancyWorld[i][j], i * 32, totalHeight - (j + 1) * 32, null);
            }
        }

        // apply the changes
        g.dispose();
    }

    @Override
    public void update(Input input, double delta) {
        // update the background combined image if the size of the window changed
        if(lastWidth != Engine.width || lastHeight != Engine.height) {
            // update the width and height variables
            lastWidth = Engine.width;
            lastHeight = Engine.height;

            // define the combinedBackground image and get its graphics context
            combinedBackground = new BufferedImage(lastWidth / 3, lastHeight / 3, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = combinedBackground.createGraphics();

            for (int j = -Engine.height / 16 - 2; j < Engine.height / 16 + 2; j++) {
                for (int i = -Engine.width / 16 - 2; i < Engine.width / 16 + 2; i++) {
                    // add the dark texture to the background image
                    g.drawImage(dark, i * 32, j * 32, null);
                }
            }

            // apply the changes
            g.dispose();
        }
    }

    @Override
    public void render(Renderer renderer) {
        // only render textures if the program isn't in wireframe mode
        if(!Collective.wireframe) {
            // draw the background and world combined images
            renderer.draw(Collective.playerPos.divide(-3).mod(32), new Vec2(Engine.width / 3., Engine.height / 3.), combinedBackground, false);
            renderer.draw(new Vec2(0.5 * totalWidth - 16, 0.5 * totalHeight - 16), new Vec2(totalWidth, totalHeight), combinedImage, true);
        }
        // only render hitboxes if the program is in wireframe or hitbox mode
        if(Collective.wireframe || Collective.hitboxes)
            for(Hitbox hitbox : this.hitboxes) {
                renderer.draw(hitbox);
        }
    }

    private void getRawWorld() {
        // load the world1.dat file into a char[][]
        try(Scanner scanner = new Scanner(new File("game/res/world" + world + ".dat"))) {
            String value = "";
            int num = 0;
            for(char character : scanner.nextLine().toCharArray()) {
                if(character != ',' && character != '\n')
                    value = value + character;
                else {
                    if(num == 0) {
                        worldWidth = Integer.parseInt(value);
                    }
                    else if(num == 1) {
                        worldHeight = Integer.parseInt(value);
                    }
                    value = "";
                    num++;
                }
            }
            rawWorld = new char[worldWidth][worldHeight];
            for(int j = 0; j < worldHeight; j++) {
                char[] line = scanner.nextLine().toCharArray();
                for(int i = 0; i < worldWidth; i++) {
                    int y = worldHeight - j - 1;
                    rawWorld[i][y] = line[i];
                }
            }
        } catch(FileNotFoundException e) {
            System.out.println("Unable to load world1.dat file");
        }
    }

    private void setWorldHitbox() {
        // add entities based on character array
        for(int j = 0; j < worldHeight; j++) {
            for(int i = 0; i < worldWidth; i++) {
				switch (rawWorld[i][j]) {
                    case 'p' -> Engine.addToEntityList(new Player(new Vec2(i * 32,  j * 32)));
					case 'e' -> Engine.addToEntityList(new Enemy(new Vec2(i * 32, j * 32)));
					case 'b' -> Engine.addToEntityList(new Bean(new Vec2(i * 32, j * 32)));
                    case 'q' -> Engine.addToEntityList(new Portal(new Vec2(i * 32, j * 32)));
                    case 'd' -> Engine.addToEntityList(new Door(new Vec2(i * 32, j * 32)));
                    case 'm' -> Engine.addToEntityList(new Movable(new Vec2(i * 32, j * 32)));
					case 'g' -> hitboxes.add(new Hitbox(new Vec2(i * 32 - 16, j * 32 - 16), new Vec2(32, 32)));
					default -> {}
				}
            }
        }
    }
}
