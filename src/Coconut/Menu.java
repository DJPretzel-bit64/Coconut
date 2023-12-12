package Coconut;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Menu extends BasicEntity {
	public boolean visible;
	public ArrayList<Button> buttons = new ArrayList<>();
	public BufferedImage background, buttonTextures;

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void update(Input input, double delta) {
		if(visible) {
			for (Button button : buttons) {
				button.update(input);
			}
		}
	}

	public void render(Renderer renderer) {
		if(visible) {
			renderer.draw(pos, size, background, false);
			for (Button button : buttons) {
				button.render(renderer, pos);
			}
		}
	}
}
