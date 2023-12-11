package Coconut;

import java.awt.*;

public class Hitbox {
    private Vec2 pos, size;

    public Hitbox(Vec2 pos, Vec2 size) {
        this.pos = pos;
        this.size = size;
    }

    public boolean intersects(Hitbox collide) {
        // check if this hitbox collides with another
        Rectangle hitbox = new Rectangle((int)pos.x, (int)pos.y, (int)size.x, (int)size.y);
        Rectangle collideBox = new Rectangle((int)collide.pos.x, (int)collide.pos.y, (int)collide.size.x, (int)collide.size.y);
        return hitbox.intersects(collideBox);
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    public Vec2 getPos() {
        return this.pos;
    }

    public void setSize(Vec2 size) {
        this.size = size;
    }

    public Vec2 getSize() {
        return this.size;
    }
}
