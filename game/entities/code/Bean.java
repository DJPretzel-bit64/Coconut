package game.entities.code;

import engine.BasicEntity;
import engine.Hitbox;
import engine.Renderer;
import engine.Vec2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bean extends BasicEntity {
	int aniLength = 4;
	BufferedImage[] beanAni = new BufferedImage[aniLength];

	public Bean(Vec2 pos) {
		this.pos = pos.plus(new Vec2(16, 16));
		this.hitboxes.add(new Hitbox(pos, new Vec2(32, 32)));
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
