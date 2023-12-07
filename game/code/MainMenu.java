package game.code;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.*;

public class MainMenu extends Menu {
	public MainMenu() {
		Collective.mainMenu = this;
		visible = true;

		try {
			this.background = ImageIO.read(new File("game/res/pauseMenu.png"));
			this.buttonTextures = ImageIO.read(new File("game/res/buttons.png"));
		} catch(IOException e) {
			System.out.println("Error loading pauseMenu textures");
		}

		BufferedImage startNormal = buttonTextures.getSubimage(0, 48, 49, 12);
		BufferedImage startHovered = buttonTextures.getSubimage(0, 60, 49, 12);
		BufferedImage exitNormal = buttonTextures.getSubimage(0, 24, 40, 12);
		BufferedImage exitHovered = buttonTextures.getSubimage(0, 36, 40, 12);

		this.buttons.add(new Button(startNormal, startHovered, new Vec2(0, 10), new Vec2(49, 12), () -> {
			visible = false;
			Collective.paused = false;
			Collective.started = true;
			Engine.lightsEnabled = true;
			Engine.addToEntityList(new World());
		}));
		this.buttons.add(new Button(exitNormal, exitHovered, new Vec2(0, -10), new Vec2(40, 12), () -> System.exit(0)));
	}
}
