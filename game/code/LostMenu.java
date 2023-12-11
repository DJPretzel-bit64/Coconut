package game.code;

import Coconut.*;
import Coconut.Button;
import Coconut.Menu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LostMenu extends Menu {
	private BufferedImage numberSprite;
	private final BufferedImage[] numbers = new BufferedImage[10];
	BufferedImage score;
	private Vec2 scoreSize;

	public LostMenu() {
		Collective.lostMenu = this;

		try {
			this.background = ImageIO.read(new File("game/res/lostMenu.png"));
			this.buttonTextures = ImageIO.read(new File("game/res/buttons.png"));
			this.numberSprite = ImageIO.read(new File("game/res/numbers.png"));
		} catch(IOException e) {
			System.out.println("Error loading lostMenu textures");
		}

		BufferedImage exitNormal = buttonTextures.getSubimage(0, 24, 40, 12);
		BufferedImage exitHovered = buttonTextures.getSubimage(0, 36, 40, 12);
		BufferedImage menuNormal = buttonTextures.getSubimage(0, 72, 40, 12);
		BufferedImage menuHovered = buttonTextures.getSubimage(0, 84, 40, 12);

		for(int i = 0; i < 10; i++)
			numbers[i] = numberSprite.getSubimage(i * 9, 0, 9, 12);

		this.buttons.add(new Button(exitNormal, exitHovered, new Vec2(-30, -20), new Vec2(40, 12), () -> System.exit(0)));
		this.buttons.add(new Button(menuNormal, menuHovered, new Vec2(30, -20), new Vec2(40, 12), () -> {
			visible = false;
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

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
		Collective.running = false;
		Collective.paused = true;
		int[] scoreArray = integerToArray(Collective.score);
		score = new BufferedImage(scoreArray.length * 10, 12, BufferedImage.TYPE_4BYTE_ABGR);
		scoreSize = new Vec2(scoreArray.length * 10, 12);
		Graphics2D g = score.createGraphics();
		int xpos = 0;
		for(int num : scoreArray) {
			g.drawImage(numbers[num], xpos, 0, null);
			xpos += 10;
		}
		g.dispose();
	}

	int[] integerToArray(int number) {
		// Convert the integer to a string to get its length
		String numberStr = Integer.toString(number);
		int length = numberStr.length();

		// Initialize the result array with the length of the number
		int[] result = new int[length];

		// Loop through each digit and store it in the array
		for (int i = 0; i < length; i++) {
			// Extract each digit by using the modulo and division operations
			int digit = number % 10;
			result[length - 1 - i] = digit; // Store digit in reverse order
			number /= 10;
		}

		return result;
	}

	public void render(Renderer renderer) {
		if(visible) {
			renderer.draw(pos, size, background, false);
			renderer.draw(new Vec2(), scoreSize, score, false);
			for (Button button : buttons) {
				button.render(renderer, pos);
			}
		}
	}
}
