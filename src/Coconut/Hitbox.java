package Coconut;

import java.awt.*;
import java.awt.geom.Area;

public class Hitbox {
    public final Polygon hitbox;
    private final Vec2 pos = new Vec2();

    public Hitbox(Vec2 pos, Vec2 size) {
        int x1 = (int) pos.x;
        int x2 = (int) (pos.x + size.x);
        int y1 = (int) pos.y;
        int y2 = (int) (pos.y + size.y);
        this.hitbox = new Polygon(new int[]{x1, x1, x2, x2}, new int[]{y1, y2, y2, y1}, 4);
    }

    public Hitbox(Polygon hitbox) {
        this.hitbox = hitbox;
    }

    public boolean intersects(Hitbox that) {
        // check if this hitbox collides with another
        Area thisBox = new Area(translate(this.hitbox, new Vec2(this.pos.x, this.pos.y)));
        Area thatBox = new Area(translate(that.hitbox, new Vec2(that.pos.x, that.pos.y)));
        thisBox.intersect(thatBox);
        return !thisBox.isEmpty();
    }

    public Vec2 getPos() {
        return this.pos;
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
