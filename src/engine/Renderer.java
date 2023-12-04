package engine;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;

import static engine.Engine.height;
import static engine.Engine.width;

public class Renderer {
    Graphics g;
    double scale;
    Vec2 cameraPos;

    public Renderer(double scale, Vec2 cameraPos) {
        this.scale = scale;
        this.cameraPos = cameraPos;
    }

    public void update(Graphics g) {
        this.g = g;
    }

    public void draw(Vec2 pos, Vec2 size, BufferedImage texture) {
        int midX = (int)(0.5 * width);
        int midY = (int)(0.5 * height);
        g.drawImage(texture, (int)(scale * (pos.x - 0.5 * size.x - cameraPos.x)) + midX, (int)(scale * (-pos.y - 0.5 * size.y + cameraPos.y)) + midY, (int)(scale * size.x), (int)(scale * size.y), null);
    }

    public void draw(Hitbox hitbox) {
        int midX = width / 2;
        int midY = height / 2;
        g.setColor(Color.ORANGE);
        g.drawRect((int)(scale * (hitbox.getPos().x - cameraPos.x)) + midX, (int)(scale * -(hitbox.getPos().y + hitbox.getSize().y - cameraPos.y)) + midY, (int)(scale * hitbox.getSize().x), (int)(scale * hitbox.getSize().y));
    }
}
