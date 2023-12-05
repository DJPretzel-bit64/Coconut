package engine;

public class Light {
	public Vec2 pos;
	public double radius;
	public String attach;

	public Light(Vec2 pos, int radius, String attach) {
		this.pos = pos;
		this.radius = radius;
		this.attach = attach;
	}
}
