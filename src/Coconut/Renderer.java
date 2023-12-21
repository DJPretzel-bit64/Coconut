package Coconut;

import java.awt.*;
import java.awt.image.BufferedImage;

import static Coconut.Engine.*;
import static Coconut.Hitbox.scale;
import static Coconut.Hitbox.translate;

public class Renderer {
    Graphics g;

    public void update(Graphics g) {
        this.g = g;
    }

    public void draw(Vec2 pos, Vec2 size, BufferedImage texture, boolean offset) {
        // draw the texture at the pos with a size
        int midX = (int)(0.5 * width);
        int midY = (int)(0.5 * height);
        if(offset)
            g.drawImage(texture, (int)(scale * (pos.x - 0.5 * size.x - cameraPos.x)) + midX, (int)(scale * (-pos.y - 0.5 * size.y + cameraPos.y)) + midY, (int)(scale * size.x), (int)(scale * size.y), null);
        else
            g.drawImage(texture, (int)(scale * (pos.x - 0.5 * size.x)) + midX, (int)(scale * (-pos.y - 0.5 * size.y)) + midY, (int)(scale * size.x), (int)(scale * size.y), null);
    }

    public void draw(Vec2 pos, BufferedImage texture, boolean offset) {
        // draw the texture at the pos with a size
        int midX = (int)(0.5 * width);
        int midY = (int)(0.5 * height);
        Vec2 size = new Vec2(texture.getWidth(), texture.getHeight());
        if(offset)
            g.drawImage(texture, (int)(scale * (pos.x - 0.5 * size.x - cameraPos.x)) + midX, (int)(scale * (-pos.y - 0.5 * size.y + cameraPos.y)) + midY, (int)(scale * size.x), (int)(scale * size.y), null);
        else
            g.drawImage(texture, (int)(scale * (pos.x - 0.5 * size.x)) + midX, (int)(scale * (-pos.y - 0.5 * size.y)) + midY, (int)(scale * size.x), (int)(scale * size.y), null);
    }

    public void draw(Hitbox hitbox) {
        // render a hitbox
        int midX = width / 2;
        int midY = height / 2;
        g.setColor(Color.ORANGE);
        g.drawPolygon(translate(scale(translate(hitbox.hitbox, hitbox.getPos().minus(cameraPos)), new Vec2(scale, -scale)), new Vec2(midX, midY)));
    }
}
