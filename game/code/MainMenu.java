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
			this.background = ImageIO.read(new File("game/res/mainMenu.png"));
			this.buttonTextures = ImageIO.read(new File("game/res/buttons.png"));
		} catch(IOException e) {
			System.out.println("Error loading pauseMenu textures");
		}

		BufferedImage coffeeQuestNormal = buttonTextures.getSubimage(0, 96, 59, 25);
		BufferedImage coffeeQuestHovered = buttonTextures.getSubimage(0, 121, 59, 25);
		BufferedImage startNormal = buttonTextures.getSubimage(0, 48, 49, 12);
		BufferedImage startHovered = buttonTextures.getSubimage(0, 60, 49, 12);
		BufferedImage exitNormal = buttonTextures.getSubimage(0, 24, 40, 12);
		BufferedImage exitHovered = buttonTextures.getSubimage(0, 36, 40, 12);

		this.buttons.add(new Button(coffeeQuestNormal, coffeeQuestHovered, new Vec2(0, 40), new Vec2(59, 25), () -> {}));
		this.buttons.add(new Button(startNormal, startHovered, new Vec2(0, 10), new Vec2(49, 12), () -> {
			Collective.running = true;
			visible = false;
			Engine.lightsEnabled = true;
			Engine.addToEntityList(new World());
		}));
		this.buttons.add(new Button(exitNormal, exitHovered, new Vec2(0, -10), new Vec2(40, 12), () -> System.exit(0)));
	}

	@Override
	public void update(Input input, double delta) {
		if(visible) {
			if(Engine.width > 4 * Engine.width / 3)
				size = new Vec2(Engine.width, 0.75 * Engine.width).divide(Engine.scale);
			else
				size = new Vec2(4 * Engine.height / 3., Engine.height).divide(Engine.scale);
			for (Button button : buttons) {
				button.update(input);
			}
		}
	}
}
