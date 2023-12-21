package Coconut;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Font {
	private final BufferedImage[] letters;
	private final int tileWidth, tileHeight;
	private final char[] characters;
	private final String name;

	public Font(String name, BufferedImage texture, int tileWidth, int tileHeight, String characters) {
		this.name = name;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.characters = characters.toCharArray();

		letters = new BufferedImage[characters.length()];
		for(int i = 0; i < characters.length(); i++) {
			letters[i] = texture.getSubimage(i * tileWidth, 0, tileWidth, tileHeight);
		}
	}

	public BufferedImage print(String text) {
		BufferedImage output = new BufferedImage(tileWidth * text.length(), tileHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = output.createGraphics();
		for(int i = 0; i < text.length(); i++) {
			char character = text.toCharArray()[i];
			for (int j = 0; j < characters.length; j++)
				if (character == characters[j])
					g.drawImage(letters[j], i * tileWidth, 0, null);
		}
		g.dispose();
		return output;
	}

	public String getName() {
		return this.name;
	}
}
