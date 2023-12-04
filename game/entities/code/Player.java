package game.entities.code;

import engine.*;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static game.entities.code.Collective.aniIndex;

public class Player extends BasicEntity {
    Vec2 acceleration = new Vec2(0, -800);
    Vec2 lastPos = new Vec2();
    Vec2 lastVelocity = new Vec2();
    double speed = 120;
    double jumpSpeed = 280;
    boolean canJump = true;
    String direction = "left";
    int maxHealth = 8;
    int health = 1;
    int aniNum = 0;
    int aniLength = 4;
    int contentIndex = -1;
    int score = 0;
    BufferedImage[] idleAni = new BufferedImage[aniLength];
    BufferedImage[] leftAni = new BufferedImage[aniLength];
    BufferedImage[] rightAni = new BufferedImage[aniLength];
    BufferedImage[] upAni = new BufferedImage[aniLength];
    BufferedImage[] downAni = new BufferedImage[aniLength];
    BufferedImage[] leftFallAni = new BufferedImage[aniLength];
    BufferedImage[] rightFallAni = new BufferedImage[aniLength];
    BufferedImage[] healthAni = new BufferedImage[maxHealth + 1];

    public Player() {
        Collective.playerPos = pos;
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
            health--;
            score++;
            Engine.removeFromEntityList(lastCollision.get(contentIndex));
            if(health == 0) {
                System.out.println("You finished with " + score + " points!");
                System.exit(0);
            }
        }

        if(contains("Bean")) {
            if(health == maxHealth)
                score++;
            else
                health++;
            Engine.removeFromEntityList(lastCollision.get(contentIndex));
        }

        aniNum++;
        if(aniNum >= aniSpeed) {
            aniNum = 0;
            aniIndex++;
            if (aniIndex >= aniLength)
                aniIndex = 0;
        }
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

    private boolean contains(String name) {
        for(int i = 0; i < lastCollision.size(); i++) {
            Entity entity = lastCollision.get(i);
            if (Objects.equals(entity.getName(), name)) {
                contentIndex = i;
                return true;
            }
        }
        contentIndex = -1;
        return false;
    }
}
