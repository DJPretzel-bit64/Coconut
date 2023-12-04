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
    public int lastWidth;
    public int lastHeight;
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
        Collective.playerPos = pos;
        coffeeSound = new File("game/entities/res/crunch.wav");
        slurpSound = new File("game/entities/res/slurp.wav");
        portalSound = new File("game/entities/res/portal.wav");
        playSound(coffeeSound);
    }

    @Override
    public void setTexture(BufferedImage texture) {
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
        Collective.playerPos = pos;

        double speed = this.speed * delta;
        double jumpSpeed = this.jumpSpeed * delta;
        int aniSpeed = (int) (0.1 / delta);
        if(input.up && canJump) {
            velocity.y = jumpSpeed;
            canJump = false;
        }
        if(velocity.y == 0 && lastVelocity.y < 0)
            canJump = true;
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
        velocity = velocity.plus(acceleration.times(delta * delta));
        if(lastPos.y != pos.y)
            canJump = false;
        lastPos = new Vec2(pos);
        lastVelocity = new Vec2(velocity);

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

        if(contains("Bean")) {
            playSound(coffeeSound);
            if(health == maxHealth)
                score++;
            else
                health++;
            Engine.removeFromEntityList(lastCollisionEntity);
        }

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

        if(contains("Door") && input.up) {
            System.out.println("YOU WON! You scored " + score + " points!");
            System.exit(0);
        }

        aniNum++;
        if(aniNum >= aniSpeed) {
            aniNum = 0;
            aniIndex++;
            if (aniIndex >= aniLength)
                aniIndex = 0;
        }

        cooldown = cooldown < 0 ? 0 : cooldown - delta;
    }

    @Override
    public void render(Renderer renderer) {
        if(!Collective.wireframe){
            if(Engine.width != lastWidth || Engine.height != lastHeight) {
                lastWidth = Engine.width;
                lastHeight = Engine.height;
                int imageWidth = Engine.width / 3;
                int imageHeight = Engine.height / 3;

                Vec2 center = new Vec2(imageWidth / 2., imageHeight / 2.);

                this.overlay = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
                for(int i = 0; i < imageWidth; i++) {
                    for(int j = 0; j < imageHeight; j++) {
                        Vec2 point = new Vec2(i, j);
                        double length = center.minus(point).length();
                        int alpha = (int)Math.min((length - length % 10) * 5, 200);
                        this.overlay.setRGB(i, j, alpha << 24);
                    }
                }
            }

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
        if(Collective.wireframe || Collective.hitboxes)
            for(Hitbox hitbox : hitboxes)
                renderer.draw(hitbox);
    }

    private boolean contains(String name) {
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
