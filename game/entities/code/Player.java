package game.entities.code;

import engine.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import static game.entities.code.Collective.aniIndex;

public class Player extends BasicEntity {
    double speed = 100;
    double jumpSpeed = 100;
    boolean canJump = true;
    String direction;
    int maxHealth = 8;
    int health = maxHealth;
    int aniLength = 4;
    int contentIndex = -1;
    BufferedImage[] idleAni = new BufferedImage[aniLength];
    BufferedImage[] leftAni = new BufferedImage[aniLength];
    BufferedImage[] rightAni = new BufferedImage[aniLength];
    BufferedImage[] upAni = new BufferedImage[aniLength];
    BufferedImage[] downAni = new BufferedImage[aniLength];
    BufferedImage[] leftFallAni = new BufferedImage[aniLength];
    BufferedImage[] rightFallAni = new BufferedImage[aniLength];
    BufferedImage[] healthAni = new BufferedImage[maxHealth + 1];

    public Player() {
        acceleration = new Vec2(0, -100);
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
        double speed = this.speed * delta;
        double jumpSpeed = this.jumpSpeed * delta;
        if(input.up && canJump) {
            direction = "up";
            this.velocity.y = jumpSpeed;
            System.out.println(this.velocity.y);
            canJump = false;
        }
        if(this.velocity.y == 0)
            canJump = true;
        if(input.left) {
            this.velocity.x = -speed;
            direction = "left";
        }
        else if(input.right) {
            this.velocity.x = speed;
            direction = "right";
        }
        else {
            this.velocity.x = 0;
            direction = Objects.equals(direction, "up") ? "up" : "idle";
        }
        this.velocity = this.velocity.plus(this.acceleration.times(delta * delta));
    }

    @Override
    public void render(Renderer renderer) {
        if(!Collective.wireframe){
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
        }
        if(Collective.wireframe || Collective.hitboxes)
            for(Hitbox hitbox : hitboxes)
                renderer.draw(hitbox);
    }

    private boolean contains(List<Entity> lastCollision, String content) {
        for(int i = 0; i < lastCollision.size(); i++) {
            Entity entity = lastCollision.get(i);
            if (Objects.equals(entity.getName(), content)) {
                contentIndex = i;
                return true;
            }
        }
        contentIndex = -1;
        return false;
    }
}
