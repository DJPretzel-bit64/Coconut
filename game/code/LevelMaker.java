package game.code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileWriter;
import java.io.IOException;

public class LevelMaker extends JFrame {
    int levelWidth = 10, levelHeight = 10;
    int buttonSize = 32;
    char[][] level = new char[levelWidth][levelHeight];

    public static void main(String[] args) {
        new LevelMaker();
    }

    public LevelMaker() {
        for(int i = 0; i < levelHeight; i++) {
            for(int j = 0; j < levelWidth; j++) {
                level[j][i] = '0';
            }
        }
        JTextField options = new JTextField();
        for(int i = 0; i < levelHeight; i++) {
            for(int j = 0; j < levelWidth; j++) {
                Button button = getButton(j, i, options);
                this.add(button);
            }
        }
        options.setSize(new Dimension(2 * buttonSize, buttonSize));
        options.setMaximumSize(new Dimension(2 * buttonSize, buttonSize));
        options.setMinimumSize(new Dimension(2 * buttonSize, buttonSize));
        options.setLocation(0, levelHeight * (buttonSize));
        this.add(options);
        Button enter = getEnter();
        this.add(enter);
        this.pack();
        this.setSize(new Dimension(levelWidth * (buttonSize), levelHeight * (buttonSize)));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private Button getButton(int j, int i, JTextField options) {
        Button button = new Button();
        button.setMaximumSize(new Dimension(buttonSize, buttonSize));
        button.setSize(new Dimension(buttonSize, buttonSize));
        button.setMinimumSize(new Dimension(buttonSize, buttonSize));
        button.setLocation(j * buttonSize, i * buttonSize);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                level[j][i] = options.getText().toCharArray()[0];
                button.setBackground(switch (level[j][i]) {
                    case 'g' -> Color.GREEN;
                    case 'b' -> Color.YELLOW;
                    case 'e' -> Color.RED;
                    case 'p' -> Color.BLUE;
                    case 'd' -> Color.CYAN;
                    case 'q' -> Color.PINK;
                    case 'm' -> new Color(100, 0, 100);
                    default -> Color.WHITE;
                });
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return button;
    }

    private Button getEnter() {
        Button enter = new Button("Enter");
        enter.setMinimumSize(new Dimension(buttonSize, buttonSize));
        enter.setMaximumSize(new Dimension(buttonSize, buttonSize));
        enter.setSize(new Dimension(buttonSize, buttonSize));
        enter.setLocation(buttonSize, levelHeight * buttonSize);
        enter.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    FileWriter writer = new FileWriter("game/res/world.dat");
                    writer.write(levelWidth + "," + levelHeight + ",\n");
                    for(int i = 0; i < levelHeight; i++) {
                        for(int j = 0; j < levelWidth; j++) {
                            System.out.print(level[j][i]);
                            writer.write(level[j][i]);
                        }
                        System.out.println();
                        writer.write('\n');
                    }
                    writer.close();
                } catch (IOException ex) {
                    System.out.println("Error Writing FIle");
                }
            }
            @Override
            public void mousePressed(MouseEvent e){}
            @Override
            public void mouseReleased(MouseEvent e){}
            @Override
            public void mouseEntered(MouseEvent e){}
            @Override
            public void mouseExited(MouseEvent e){}
        });
        return enter;
    }
}
