package game.code;

import Coconut.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Movable extends BasicEntity {
	final double gravity = -800;

	public Movable(Vec2 pos) {
		this.mass = 2;
		this.pos = pos;
		this.size = new Vec2(32, 32);
		this.layer = 2;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(16, 16)), new Vec2(32, 32)));
		this.name = "Movable";
		this.collidesWith.add("World");
		try {
			this.texture = ImageIO.read(new File("game/res/block.png"));
		} catch(IOException e){
			System.out.println("Error loading Movable texture");
		}
	}

	@Override
	public void update(Input input, double delta) {
		double gravity = this.gravity * delta * delta;
		this.velocity = new Vec2(0, velocity.y).plus(new Vec2(0, gravity));
	}

	@Override
	public void render(Renderer renderer) {
		if(!Collective.wireframe) {
			renderer.draw(pos, size, texture, true);
		}
		if(Collective.wireframe || Collective.hitboxes) {
			for(Hitbox hitbox : hitboxes) {
				renderer.draw(hitbox);
			}
		}
	}
}
