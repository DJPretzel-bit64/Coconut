package Coconut;

import java.awt.*;
import java.awt.geom.Area;

public class Hitbox {
    public final Polygon hitbox;
    private Vec2 pos, size;
    private final boolean box;

    public Hitbox(Vec2 pos, Vec2 size) {
        this.box = true;
        this.hitbox = new Polygon(new int[]{0, 0, (int)size.x, (int)size.x}, new int[]{0, (int)size.y, (int)size.y, 0}, 4);
        this.pos = pos;
        this.size = size;
    }

    public Hitbox(Polygon hitbox, Vec2 pos) {
        this.box = false;
        this.hitbox = translate(hitbox, pos);
    }

    public boolean intersects(Hitbox that) {
        // check if this hitbox collides with another
        if(this.box && that.box) {
            Rectangle thisBox = new Rectangle((int)this.pos.x, (int)this.pos.y, (int)this.size.x, (int)this.size.y);
            Rectangle thatBox = new Rectangle((int)that.pos.x, (int)that.pos.y, (int)that.size.x, (int)that.size.y);
            return thisBox.intersects(thatBox);
        }else {
            Rectangle thisTransBox = this.hitbox.getBounds();
            Rectangle thatTransBox = that.hitbox.getBounds();
            thisTransBox.translate((int)this.pos.x, (int)this.pos.y);
            thatTransBox.translate((int)that.pos.x, (int)that.pos.y);
            if(!thisTransBox.intersects(thatTransBox))
                return false;
            Area thisBox = new Area(translate(this.hitbox, new Vec2(this.pos.x, this.pos.y)));
            Area thatBox = new Area(translate(that.hitbox, new Vec2(that.pos.x, that.pos.y)));
            thisBox.intersect(thatBox);
            return !thisBox.isEmpty();
        }
    }

    public Vec2 getPos() {
        return this.pos;
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    public static Polygon translate(Polygon p1, Vec2 delta) {
        Polygon p2 = new Polygon(p1.xpoints, p1.ypoints, p1.npoints);
        p2.translate((int) delta.x, (int) delta.y);
        return p2;
    }

    public static Polygon scale(Polygon p1, double fac) {
        Polygon p2 = new Polygon();
        for(int i = 0; i < p1.npoints; i++) {
            p2.addPoint((int)(p1.xpoints[i] * fac), (int)(p1.ypoints[i] * fac));
        }
        return p2;
    }

    public static Polygon scale(Polygon p1, Vec2 fac) {
        Polygon p2 = new Polygon();
        for(int i = 0; i < p1.npoints; i++) {
            p2.addPoint((int)(p1.xpoints[i] * fac.x), (int)(p1.ypoints[i] * fac.y));
        }
        return p2;
    }
}
