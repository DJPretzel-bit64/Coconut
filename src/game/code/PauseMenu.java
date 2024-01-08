package game.code;

import Coconut.*;

import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PauseMenu extends Menu {
	private boolean lastEscape;

	public PauseMenu() {
		try {
			this.background = ImageIO.read(new File("game/res/pauseMenu.png"));
			this.buttonTextures = ImageIO.read(new File("game/res/buttons.png"));
		} catch(IOException e) {
			System.out.println("Error loading pauseMenu textures");
		}

		BufferedImage resumeNormal = buttonTextures.getSubimage(0, 0, 59, 12);
		BufferedImage resumeHovered = buttonTextures.getSubimage(0, 12, 59, 12);
		BufferedImage exitNormal = buttonTextures.getSubimage(0, 24, 40, 12);
		BufferedImage exitHovered = buttonTextures.getSubimage(0, 36, 40, 12);
		BufferedImage menuNormal = buttonTextures.getSubimage(0, 72, 43, 12);
		BufferedImage menuHovered = buttonTextures.getSubimage(0, 84, 43, 12);

		this.buttons.add(new Button(resumeNormal, resumeHovered, new Vec2(0, -10), new Vec2(59, 12), () -> Collective.paused = false));
		this.buttons.add(new Button(exitNormal, exitHovered, new Vec2(0, 10), new Vec2(40, 12), () -> System.exit(0)));
		this.buttons.add(new Button(menuNormal, menuHovered, new Vec2(0, -30), new Vec2(43, 12), () -> {
			Collective.mainMenu.setVisible(true);
			Collective.paused = false;
			Engine.lightsEnabled = false;
			for(Entity entity : Engine.getEntityList()) {
				if(! (entity instanceof Menu))
					Engine.removeFromEntityList(entity);
			}
			Engine.lightList.clear();
		}));
	}

	public void update(Input input, double delta) {
		if(input.getKeyEvent(KeyEvent.VK_ESCAPE) && !lastEscape)
			Collective.paused = !Collective.paused;
		lastEscape = input.getKeyEvent(KeyEvent.VK_ESCAPE);
		visible = Collective.paused && Collective.running;
		if(visible) {
			for(Button button : buttons)
				button.update(input);
		}
	}
}
