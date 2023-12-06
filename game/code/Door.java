package game.code;

import engine.BasicEntity;
import engine.Hitbox;
import engine.Vec2;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Door extends BasicEntity {
	public final Random random = new Random();

	public Door(Vec2 pos) {
		// set data for the door
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(16, 16)), new Vec2(32, 32)));
		this.name = "Door";
		try {
			this.texture = ImageIO.read(new File("game/res/world.png")).getSubimage(96, 128, 32, 32);
		} catch(IOException e) {
			System.out.println("Error loading bean textures");
		}
	}
}
