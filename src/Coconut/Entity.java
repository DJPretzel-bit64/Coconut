package Coconut;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public interface Entity {
    void setName(String name);
    String getName();
    void setSize(Vec2 size);
    Vec2 getSize();
    void setPos(Vec2 pos);
    Vec2 getPos();
    void setTexture(BufferedImage texture);
    BufferedImage getTexture();
    void setHitboxes(ArrayList<Hitbox> hitboxes);
    ArrayList<Hitbox> getHitboxes();
    void setVelocity(Vec2 velocity);
    Vec2 getVelocity();
    ArrayList<String> getCollidesWith();
    void setCollidesWith(ArrayList<String> collidesWith);
    ArrayList<Entity> getLastCollision();
    void setLastCollision(ArrayList<Entity> lastCollision);
    int getLayer();
    void setLayer(int priority);
    int getIndex();
    void setIndex(int index);
    double getMass();
    void setMass(double mass);
    void update(Input input, double delta);
    void render(Renderer renderer);
}
