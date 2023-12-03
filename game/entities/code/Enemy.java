package game.entities.code;

import engine.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Enemy extends BasicEntity {
	String direction = "right";
	int speed = 80;

	public Enemy(Vec2 pos) {
		this.acceleration = new Vec2(0, -200);
		this.pos = pos.plus(new Vec2(16, 16));
		this.hitboxes.add(new Hitbox(pos.plus(new Vec2(4, 0)), new Vec2(24, 32)));
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
		if(velocity.x == 0)
			direction = Objects.equals(direction, "left") ? "right" : "left";
		if(Objects.equals(direction, "left"))
			velocity.x = speed;
		if(Objects.equals(direction, "right"))
			velocity.x = -speed;
	}

	@Override
	public void render(Renderer renderer) {
		if(!Collective.wireframe)
			renderer.draw(pos, size, texture);
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.get(0));
	}
}