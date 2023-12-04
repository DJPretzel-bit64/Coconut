package game.entities.code;

import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Portal extends BasicEntity {
	Random random = new Random();
	final public int aniLength = 4;
	final public BufferedImage[] portalAni = new BufferedImage[aniLength];

	public Portal(Vec2 pos) {
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(16, 16)), new Vec2(32, 32)));
		this.name = "Portal";
		try {
			this.texture = ImageIO.read(new File("game/entities/res/world.png"));
		} catch(IOException e) {
			System.out.println("Error loading portal textures");
		}
		setTexture(this.texture);
	}

	@Override
	public void setTexture(BufferedImage texture) {
		for(int i = 0; i < aniLength; i++)
			portalAni[i] = this.texture.getSubimage(128, (i+1) * 32, 32, 32);
	}

	@Override
	public void render(Renderer renderer) {
		if(!Collective.wireframe)
			renderer.draw(pos, size, portalAni[Collective.aniIndex]);
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.get(0));
	}
}
