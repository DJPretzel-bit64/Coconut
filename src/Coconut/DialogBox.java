package Coconut;

import java.awt.image.BufferedImage;
import java.util.Objects;

public class DialogBox extends BasicEntity {
	private final BufferedImage render;
	private boolean visible;

	public DialogBox(String text, String font) {
		render = Objects.requireNonNull(Fonts.getFont(font)).print(text);
	}

	@Override
	public void render(Renderer renderer) {
		if(visible)
			renderer.draw(new Vec2(), render, false);
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return this.visible;
	}
}
