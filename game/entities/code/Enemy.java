package game.entities.code;

import engine.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Enemy extends BasicEntity {
	public final Random random = new Random();
	public final Vec2 acceleration = new Vec2(0, -800);
	public final double speed = 100;
	String direction = "right";

	public Enemy(Vec2 pos) {
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(12, 16)), new Vec2(24, 32)));
		this.collidesWith.add("World");
		this.collidesWith.add("Enemy");
		this.name = "Enemy";
		try {
			this.texture = ImageIO.read(new File("game/entities/res/enemy.png"));
		} catch(IOException e) {
			System.out.println("Unable to load enemy textures");
		}
	}

	@Override
	public void update(Input input, double delta) {
		double speed = this.speed * delta;
		if(velocity.x == 0) {
			direction = Objects.equals(direction, "left") ? "right" : "left";
		}
		if(Objects.equals(direction, "left"))
			velocity.x = -speed;
		if(Objects.equals(direction, "right"))
			velocity.x = speed;
		this.velocity = this.velocity.plus(this.acceleration.times(delta * delta));
	}

	@Override
	public void render(Renderer renderer) {
		if(!Collective.wireframe)
			renderer.draw(pos, size, texture);
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.get(0));
	}
}