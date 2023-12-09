package game.code;

import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LevelMenu extends Menu {
	BufferedImage numbers;
	int lastFurthest = -1;

	public LevelMenu() {
		Collective.levelMenu = this;
		visible = false;

		try {
			this.background = ImageIO.read(new File("game/res/mainMenu.png"));
			this.buttonTextures = ImageIO.read(new File("game/res/buttons.png"));
			this.numbers = ImageIO.read(new File("game/res/numbers.png"));
		} catch(IOException e) {
			System.out.println("Error loading pauseMenu textures");
		}

		for(int i = 0; i <= Collective.numLevels; i++) {
			BufferedImage normal = numbers.getSubimage(i * 9, 0, 9, 12);
			BufferedImage hovered = numbers.getSubimage(i * 9, 12, 9, 12);
			int finalI = i;
			Button button = new Button(normal, hovered, new Vec2(12 * (i - 0.5 * Collective.numLevels), 12), new Vec2(9, 12), () -> {
				Collective.running = true;
				visible = false;
				Engine.lightsEnabled = true;
				Collective.currentLevel = finalI;
				Engine.addToEntityList(new World("" + finalI));
			});
			if(i >= Collective.furthestLevel)
				button.setEnabled(false);
			this.buttons.add(button);
		}

		BufferedImage exitNormal = buttonTextures.getSubimage(0, 24, 40, 12);
		BufferedImage exitHovered = buttonTextures.getSubimage(0, 36, 40, 12);

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
		if(Collective.furthestLevel != lastFurthest) {
			for(int i = 0; i <= Collective.furthestLevel; i++)
				buttons.get(i).setEnabled(true);
			lastFurthest = Collective.furthestLevel;
		}
	}
}
