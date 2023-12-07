package engine;

import java.awt.image.BufferedImage;

public class Button {
	BufferedImage normal, hovered;
	Vec2 pos;
	Vec2 size;
	OnClick action;
	boolean hov = false;

	public Button(BufferedImage normal, BufferedImage hovered, Vec2 pos, Vec2 size, OnClick action) {
		this.normal = normal;
		this.hovered = hovered;
		this.pos = pos;
		this.size = size;
		this.action = action;
	}

	public void update(Input input) {
		if(input.mousePos.x > pos.x - 0.5 * size.x && input.mousePos.x < pos.x + 0.5 * size.x &&
				input.mousePos.y > pos.y - 0.5 * size.y && input.mousePos.y < pos.y + 0.5 * size.y) {
			hov = true;
			if(input.mouse)
				action.runAction();
		}
		else hov = false;
	}

	public void render(Renderer renderer, Vec2 offset) {
		renderer.draw(pos.plus(offset), size, hov ? hovered : normal, false);
	}
}
