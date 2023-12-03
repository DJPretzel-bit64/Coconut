package engine;

import java.awt.image.BufferedImage;
import java.util.List;

public interface Entity {
    void setName(String name);
    String getName();
    void setSize(Vec2 size);
    Vec2 getSize();
    void setPos(Vec2 pos);
    Vec2 getPos();
    void setTexture(BufferedImage texture);
    BufferedImage getTexture();
    void setHitboxes(List<Hitbox> hitboxes);
    List<Hitbox> getHitboxes();
    void setVelocity(Vec2 velocity);
    Vec2 getVelocity();
    void setAcceleration(Vec2 acceleration);
    Vec2 getAcceleration();
    List<String> getCollidesWith();
    void setCollidesWith(List<String> collidesWith);
    List<Entity> getLastCollision();
    void setLastCollision(List<Entity> lastCollision);
    void update(Input input, double delta);
    void render(Renderer renderer);
}
