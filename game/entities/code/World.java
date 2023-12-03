package game.entities.code;

import engine.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class World extends BasicEntity {
    BufferedImage NSEW, SEW, NEW, NSW, NSE, EW, NS, NE, NW ,SE ,SW, N, S, E, W, none, door, dark;
    char[][] rawWorld;
    BufferedImage[][] fancyWorld;
    int worldWidth, worldHeight;
    
    public World() {
        hitboxes = new ArrayList<>();
        getRawWorld();
        setWorldHitbox();
    }
    
    private void calculateFancyWorld() {
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
    
    @Override
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
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
        door = texture.getSubimage(96,  128, 32, 32);
        dark = texture.getSubimage(96,  160, 32, 32);
        calculateFancyWorld();
    }

    @Override
    public void render(Renderer renderer) {
        if(!Collective.wireframe) {
            for(int j = 0; j < worldHeight; j++) {
                for(int i = 0; i < worldWidth; i++) {
                    if(rawWorld[i][j] == 'g')
                        renderer.draw(new Vec2(i * 32, j * 32), new Vec2(32, 32), fancyWorld[i][j]);
                }
            }
        }
        if(Collective.wireframe || Collective.hitboxes)
            for(Hitbox hitbox : this.hitboxes) {
                renderer.draw(hitbox);
        }
    }

    private void getRawWorld() {
        try(Scanner scanner = new Scanner(new File("game/entities/res/world.dat"))) {
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
            System.out.println("Unable to load world.dat file");
        }
    }

    private void setWorldHitbox() {
        for(int j = 0; j < worldHeight; j++) {
            for(int i = 0; i < worldWidth; i++) {
                char character = rawWorld[i][j];
                if(character == 'g')
                    hitboxes.add(new Hitbox(new Vec2(i * 32 - 16, j * 32 - 16), new Vec2(32, 32)));
                else if(character == 'e')
                    Engine.addToEntityList(new Enemy(new Vec2(i * 32 - 16, j * 32 - 16)));
                else if(character == 'b')
                    Engine.addToEntityList(new Bean(new Vec2(i * 32 - 16, j * 32 - 16)));
            }
        }
    }
}
