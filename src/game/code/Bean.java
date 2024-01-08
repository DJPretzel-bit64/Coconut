package game.code;

import Coconut.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Bean extends BasicEntity {
	public final Random random = new Random();
	public final int aniLength = 4;
	public final BufferedImage[] beanAni = new BufferedImage[aniLength];
	public final char power;

	public Bean(Vec2 pos, char power) {
		// set data for the bean
		this.power = power;
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(16, 16)), new Vec2(32, 32)));
		this.name = "Bean";
		try {
			this.texture = ImageIO.read(new File("game/res/beans.png"));
		} catch(IOException e) {
			System.out.println("Error loading bean textures");
		}
		setTexture(this.texture);
	}

	@Override
	public void setTexture(BufferedImage texture) {
		// add the animation textures to an array
		for(int i = 0; i < aniLength; i++) {
			switch(power) {
				case 'm' -> beanAni[i] = this.texture.getSubimage(32, i * 32, 32, 32);
				case 's' -> beanAni[i] = this.texture.getSubimage(64, i * 32, 32, 32);
				case 'j' -> beanAni[i] = this.texture.getSubimage(96, i * 32, 32, 32);
				default -> beanAni[i] = this.texture.getSubimage(0, i * 32, 32, 32);
			}
		}
	}

	@Override
	public void render(Renderer renderer) {
		// render the current animation frame if the program isn't in wireframe mode
		if(!Collective.wireframe)
			renderer.draw(pos, size, beanAni[Collective.aniIndex], true);
		// render the hitboxes if the program is in wireframe or hitbox mode
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.getFirst());
	}
}
