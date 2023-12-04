package game.entities.code;

import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Bean extends BasicEntity {
	public final Random random = new Random();
	public final int aniLength = 4;
	public final BufferedImage[] beanAni = new BufferedImage[aniLength];

	public Bean(Vec2 pos) {
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(16, 16)), new Vec2(32, 32)));
		this.name = "Bean";
		try {
			this.texture = ImageIO.read(new File("game/entities/res/world.png"));
		} catch(IOException e) {
			System.out.println("Error loading bean textures");
		}
		setTexture(this.texture);
	}

	@Override
	public void setTexture(BufferedImage texture) {
		for(int i = 0; i < aniLength; i++)
			beanAni[i] = this.texture.getSubimage(0, (i+1) * 32, 32, 32);
	}

	@Override
	public void render(Renderer renderer) {
		if(!Collective.wireframe)
			renderer.draw(pos, size, beanAni[Collective.aniIndex]);
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.get(0));
	}
}
