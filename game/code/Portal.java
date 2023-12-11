package game.code;

import Coconut.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Portal extends BasicEntity {
	public final Random random = new Random();
	public final int aniLength = 4;
	public final BufferedImage[] portalAni = new BufferedImage[aniLength];

	public Portal(Vec2 pos) {
		// configure basic portal based on the input position
		this.index = random.nextInt();
		this.layer = 2;
		this.pos = pos;
		this.hitboxes.add(new Hitbox(pos.minus(new Vec2(8, 16)), new Vec2(16, 32)));
		this.name = "Portal";
		try {
			this.texture = ImageIO.read(new File("game/res/world.png"));
		} catch(IOException e) {
			System.out.println("Error loading portal textures");
		}
		setTexture(this.texture);
	}

	@Override
	public void setTexture(BufferedImage texture) {
		// create an animation loop when the texture is loaded
		for(int i = 0; i < aniLength; i++)
			portalAni[i] = this.texture.getSubimage(128, (i+1) * 32, 32, 32);
	}

	@Override
	public void render(Renderer renderer) {
		// only draw the current animation frame if the program isn't in wireframe mode
		if(!Collective.wireframe)
			renderer.draw(pos, size, portalAni[Collective.aniIndex], true);
		// only draw the hitboxes if the program is in wireframe or hitbox mode
		if(Collective.wireframe || Collective.hitboxes)
			renderer.draw(hitboxes.get(0));
	}
}
