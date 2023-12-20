package Coconut;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

public class BasicEntity implements Entity, Serializable {
    public String name;
    public Vec2 size = new Vec2(32, 32);
    public Vec2 pos = new Vec2();
    public BufferedImage texture;
    public ArrayList<Hitbox> hitboxes = new ArrayList<>();
    public Vec2 velocity = new Vec2();
    public ArrayList<Entity> lastCollision = new ArrayList<>();
    public ArrayList<String> collidesWith = new ArrayList<>();
    public int layer = -1;
    public int index;
    public double mass = -1;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Vec2 getSize() {
        return size;
    }

    @Override
    public void setSize(Vec2 size) {
        this.size = size;
    }

    @Override
    public Vec2 getPos() {
        return pos;
    }

    @Override
    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    @Override
    public BufferedImage getTexture() {
        return this.texture;
    }

    @Override
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    @Override
    public ArrayList<Hitbox> getHitboxes() {
        return hitboxes;
    }

    @Override
    public void setHitboxes(ArrayList<Hitbox> hitboxes) {
        this.hitboxes = hitboxes;
    }

    @Override
    public Vec2 getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(Vec2 velocity) {
        this.velocity = velocity;
    }

    @Override
    public ArrayList<Entity> getLastCollision() {
        return this.lastCollision;
    }

    @Override
    public void setLastCollision(ArrayList<Entity> lastCollision) {
        this.lastCollision = lastCollision;
    }

    @Override
    public ArrayList<String> getCollidesWith() {
        return this.collidesWith;
    }

    @Override
    public void setCollidesWith(ArrayList<String> collidesWith) {
        this.collidesWith = collidesWith;
    }

    @Override
    public int getLayer() {
        return this.layer;
    }

    @Override
    public void setLayer(int priority) {
        this.layer = priority;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    @Override
    public void setMass(double mass) {
        this.mass = mass;
    }

    @Override
    public void update(Input input, double delta) {}

    @Override
    public void render(Renderer renderer) {
        renderer.draw(this.pos, this.size, this.texture, true);
        try {
            renderer.draw(this.hitboxes.getFirst());
        } catch(Exception ignored) {}
    }
}
