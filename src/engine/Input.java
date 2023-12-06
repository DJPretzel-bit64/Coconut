package engine;


import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener {
    // define accessible content
    private final boolean[] keys = new boolean[66568];
    public boolean left, right, up, down, escape, mouse;
    public Vec2 mousePos = new Vec2();
    private int width, height;
    private double scale;

    public void update(int width, int height, double scale) {
        // update window settings
        this.width = width;
        this.height = height;
        this.scale = scale;

        // update the key booleans
        left = keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
        right = keys[KeyEvent.VK_S] || keys[KeyEvent.VK_RIGHT];
        up = keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_SPACE];
        down = keys[KeyEvent.VK_R] || keys[KeyEvent.VK_DOWN];
        escape = keys[KeyEvent.VK_ESCAPE];
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        mouse = true;
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        mouse = false;
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = new Vec2(e.getX() - 0.5 * width, -e.getY() + 0.5 * height).divide(scale);
    }
}