package engine;

public class Vec2 {
    public double x;
    public double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2() {
        this.x = 0;
        this.y = 0;
    }

    public Vec2 plus(Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    public Vec2 minus(Vec2 v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }

    public Vec2 times(double s) {
        return new Vec2(this.x * s, this.y * s);
    }

    public Vec2 divide(double s) {
        return this.times(1 / s);
    }

    public Vec2 normal() {
        return this.divide(this.length());
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public String toString() {
        return "x: " + this.x + " ; y: " + this.y;
    }
}
