package game.code;

import engine.Menu;
import engine.Vec2;

public class Collective {
    // accessible data from entity classes
    public static Vec2 playerPos = new Vec2();
    public static final boolean hitboxes = false;
    public static final boolean wireframe = false;
    public static int aniIndex = 0;
    public static boolean paused = false, running = false;
    public static Menu mainMenu, lostMenu, wonMenu;
    public static int score;
}
