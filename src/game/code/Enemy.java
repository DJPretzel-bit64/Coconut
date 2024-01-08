package game.code;

import Coconut.*;

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
	private boolean lastPaused = false;

	public Enemy(Vec2 pos) {
		// set enemy data
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(12, 16)), new Vec2(24, 32)));
		this.collidesWith.add("World");
		this.collidesWith.add("Enemy");
		this.name = "Enemy";
		try {
			this.texture = ImageIO.read(new File("game/res/enemy.png"));
		} catch(IOException e) {
			System.out.println("Unable to load enemy textures");
		}
	}

	@Override
	public void update(Input input, double delta) {
		// scale the speed based on the deltaTime
		double speed = this.speed * delta;

		// if the velocity is 0 (i.e. it hit something) change direction
		if(velocity.x == 0 && !lastPaused) {
			direction = Objects.equals(direction, "left") ? "right" : "left";
		}

		lastPaused = Collective.paused;

		// move left or right based on their direction
		if(Objects.equals(direction, "left"))
			velocity.x = -speed;
		if(Objects.equals(direction, "right"))
			velocity.x = speed;
		if(Collective.paused)
			velocity.x = 0;
		else {
			// update the velocity based on the acceleration
			this.velocity = this.velocity.plus(this.acceleration.times(delta * delta));
		}
	}

	@Override
	public void render(Renderer renderer) {
		// render texture if the program isn't in wireframe mode
		if(!Collective.wireframe)
			renderer.draw(pos, size, texture, true);
		// render the hitbox if the program is in wireframe or hitbox mode
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.getFirst());
	}
}