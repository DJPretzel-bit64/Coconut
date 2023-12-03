package engine;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BasicEntity implements Entity, Serializable {
    public String name;
    public Vec2 size = new Vec2(32, 32);
    public Vec2 pos = new Vec2();
    public BufferedImage texture;
    public List<Hitbox> hitboxes = new ArrayList<>();
    public Vec2 velocity = new Vec2();
    public Vec2 acceleration = new Vec2();
    public List<Entity> lastCollision;
    public List<String> collidesWith = new ArrayList<>();

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSize(Vec2 size) {
        this.size = size;
    }

    @Override
    public Vec2 getSize() {
        return size;
    }

    @Override
    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    @Override
    public Vec2 getPos() {
        return pos;
    }

    @Override
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    @Override
    public BufferedImage getTexture() {
        return this.texture;
    }

    @Override
    public void setHitboxes(List<Hitbox> hitboxes) {
        this.hitboxes = hitboxes;
    }

    @Override
    public List<Hitbox> getHitboxes() {
        return hitboxes;
    }

    @Override
    public void setVelocity(Vec2 velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vec2 getVelocity() {
        return velocity;
    }
    @Override
    public void setAcceleration(Vec2 acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public Vec2 getAcceleration() {
        return acceleration;
    }

    @Override
    public void setLastCollision(List<Entity> lastCollision) {
        this.lastCollision = lastCollision;
    }

    @Override
    public List<Entity> getLastCollision() {
        return this.lastCollision;
    }

    @Override
    public void setCollidesWith(List<String> collidesWith) {
        this.collidesWith = collidesWith;
    }

    @Override
    public List<String> getCollidesWith() {
        return this.collidesWith;
    }

    @Override
    public void update(Input input, double delta) {}

    @Override
    public void render(Renderer renderer) {
        renderer.draw(this.pos, this.size, texture);
    }
}
