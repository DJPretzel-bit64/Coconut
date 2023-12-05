package game.entities.code;

import engine.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.Random;

import static game.entities.code.Collective.aniIndex;

public class Player extends BasicEntity {
    public final Vec2 acceleration = new Vec2(0, -800);
    public Vec2 lastPos = new Vec2();
    public Vec2 lastVelocity = new Vec2();
    public final double speed = 120;
    public final double jumpSpeed = 280;
    public boolean canJump = true;
    public String direction = "left";
    public final int maxHealth = 8;
    public int health = 1;
    public int aniNum = 0;
    public final int aniLength = 4;
    public Entity lastCollisionEntity;
    public int score = 0;
    public double cooldown;
    public BufferedImage overlay;
    public final BufferedImage[] idleAni = new BufferedImage[aniLength];
    public final BufferedImage[] leftAni = new BufferedImage[aniLength];
    public final BufferedImage[] rightAni = new BufferedImage[aniLength];
    public final BufferedImage[] upAni = new BufferedImage[aniLength];
    public final BufferedImage[] downAni = new BufferedImage[aniLength];
    public final BufferedImage[] leftFallAni = new BufferedImage[aniLength];
    public final BufferedImage[] rightFallAni = new BufferedImage[aniLength];
    public final BufferedImage[] healthAni = new BufferedImage[maxHealth + 1];
    public final File coffeeSound, slurpSound, portalSound;
    public AudioInputStream audioStream;
    public Clip clip;
    public final Random random = new Random();

    public Player() {
        // setup sounds and position
        Collective.playerPos = pos;
        coffeeSound = new File("game/entities/res/crunch.wav");
        slurpSound = new File("game/entities/res/slurp.wav");
        portalSound = new File("game/entities/res/portal.wav");
        playSound(coffeeSound);
    }

    @Override
    public void setTexture(BufferedImage texture) {
        // get the texture animations
        this.texture = texture;
        for(int i = 0; i < aniLength; i++) {
            rightAni[i] =       texture.getSubimage(0,   i * 32, 32, 32);
            leftAni[i] =        texture.getSubimage(32,  i * 32, 32, 32);
            upAni[i] =          texture.getSubimage(64,  i * 32, 32, 32);
            downAni[i] =        texture.getSubimage(96,  i * 32, 32, 32);
            leftFallAni[i] =    texture.getSubimage(128, i * 32, 32, 32);
            rightFallAni[i] =   texture.getSubimage(160, i * 32, 32, 32);
            idleAni[i] =        texture.getSubimage(192, i * 32, 32, 32);
            healthAni[i] =      texture.getSubimage(224, i * 32, 32, 32);
            healthAni[i + 4] =  texture.getSubimage(256, i * 32, 32, 32);
        }
    }

    @Override
    public void update(Input input, double delta) {
        // update the play position
        Collective.playerPos = pos;

        // scale the speeds based on the delta value
        double speed = this.speed * delta;
        double jumpSpeed = this.jumpSpeed * delta;
        int aniSpeed = (int) (0.1 / delta);

        // if they press up and can jump, apply the jump velocity and don't let them jump again
        if(input.up && canJump) {
            velocity.y = jumpSpeed;
            canJump = false;
        }

        // check if the velocity is 0 and the player was falling, not jumping and set canJump to true
        if(velocity.y == 0 && lastVelocity.y < 0)
            canJump = true;

        // set the velocity and player direction
        if(input.left) {
            velocity.x = -speed;
            direction = velocity.y > 0 ? "up" : canJump ? "left" : "downLeft";
        }
        else if(input.right) {
            velocity.x = speed;
            direction = velocity.y > 0 ? "up" : canJump ? "right" : "downRight";
        }
        else {
            velocity.x = 0;
            direction = velocity.y == 0 ? "idle" : "up";
        }

        // increase the velocity by the acceleration
        velocity = velocity.plus(acceleration.times(delta * delta));

        // if they fell, don't allow the player to jump
        if(lastPos.y != pos.y)
            canJump = false;

        // update the last pos and velocity
        lastPos = new Vec2(pos);
        lastVelocity = new Vec2(velocity);

        // check if the player is colliding with an enemy and reduce health and increase score
        if(contains("Enemy")) {
            playSound(slurpSound);
            health--;
            score++;
            Engine.removeFromEntityList(lastCollisionEntity);
            if(health == 0) {
                System.out.println("You finished with " + score + " points!");
                System.exit(0);
            }
        }

        // check if the player is colliding with a bean and increase the health or the score
        if(contains("Bean")) {
            playSound(coffeeSound);
            if(health == maxHealth)
                score++;
            else
                health++;
            Engine.removeFromEntityList(lastCollisionEntity);
        }

        // check if the player is colliding with a portal and teleport the player to a random one if they are
        if(contains("Portal") && cooldown == 0) {
            playSound(portalSound);
            Entity entity;
            do {
                entity = Engine.getEntityList().get(random.nextInt(Engine.getEntityList().size()));
            } while(!Objects.equals(entity.getName(), "Portal") || entity.getIndex() == lastCollisionEntity.getIndex());

            pos = new Vec2(entity.getPos());
            hitboxes.get(0).setPos(new Vec2(entity.getPos().minus(new Vec2(8, 16))));
            cooldown = 3;
        }

        // if they jump in the door, they win and the program ends
        if(contains("Door") && input.up) {
            System.out.println("YOU WON! You scored " + score + " points!");
            System.exit(0);
        }

        // increase the animation number and update the animation index once in a while
        aniNum++;
        if(aniNum >= aniSpeed) {
            aniNum = 0;
            aniIndex++;
            if (aniIndex >= aniLength)
                aniIndex = 0;
        }

        // set the cooldown for going through portals
        cooldown = cooldown < 0 ? 0 : cooldown - delta;
    }

    @Override
    public void render(Renderer renderer) {
        // only render the animation if the program isn't in wireframe mode
        if(!Collective.wireframe){
//            // update the light overlay if the width and height are different
//            if(Engine.width != lastWidth || Engine.height != lastHeight) {
//                lastWidth = Engine.width;
//                lastHeight = Engine.height;
//                int imageWidth = Engine.width / 3;
//                int imageHeight = Engine.height / 3;
//
//                Vec2 center = new Vec2(imageWidth / 2., imageHeight / 2.);
//
//                this.overlay = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
//                for(int i = 0; i < imageWidth; i++) {
//                    for(int j = 0; j < imageHeight; j++) {
//                        // calculate the light level of the pixel based on the length to the player
//                        Vec2 point = new Vec2(i, j);
//                        double length = center.minus(point).length();
//                        int alpha = (int)Math.min((length - length % 10) * 5, 200);
//                        this.overlay.setRGB(i, j, alpha << 24);
//                    }
//                }
//            }

            // set the image based on the direction and animation index
            BufferedImage frame = switch(direction) {
                case "left" -> leftAni[aniIndex];
                case "right" -> rightAni[aniIndex];
                case "up" -> upAni[aniIndex];
                case "down" -> downAni[aniIndex];
                case "downLeft" -> leftFallAni[aniIndex];
                case "downRight" -> rightFallAni[aniIndex];
                default -> idleAni[aniIndex];
            };
            renderer.draw(pos, size, healthAni[maxHealth - health]);
            renderer.draw(pos, size, frame);
            renderer.draw(pos, new Vec2(Engine.width, Engine.height).divide(3), overlay);
        }
        // render hitbox if the program is in wireframe or hitbox mode
        if(Collective.wireframe || Collective.hitboxes)
            renderer.draw(hitboxes.get(0));
    }

    private boolean contains(String name) {
        // function to check if the player collided with a specific entity in the last frame
		for(Entity entity : lastCollision) {
			if(Objects.equals(entity.getName(), name)) {
				lastCollisionEntity = entity;
				return true;
			}
		}
        lastCollisionEntity = null;
        return false;
    }

    private void playSound(File file) {
        // function to play a sound given a file location
        try {
            audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error loading audio");
        }
    }
}
