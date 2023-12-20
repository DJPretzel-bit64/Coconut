package Coconut;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Arrays;

public class Hitbox {
    public final Polygon hitbox;
    private Vec2 pos = new Vec2();

    public Hitbox(Vec2 pos, Vec2 size) {
        this.hitbox = new Polygon(new int[]{0, 0, (int)size.x, (int)size.x}, new int[]{0, (int)size.y, (int)size.y, 0}, 4);
        this.pos = pos;
    }

    public Hitbox(Polygon hitbox, Vec2 pos) {
        System.out.println(Arrays.toString(hitbox.xpoints));
        System.out.println(Arrays.toString(hitbox.ypoints));
        System.out.println(pos);
        this.hitbox = translate(hitbox, pos);
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
