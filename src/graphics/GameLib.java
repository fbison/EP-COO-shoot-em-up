package graphics;

import javax.swing.*;
import java.awt.*;

public class GameLib {

    private static MyFrame frame = null;
    private static Graphics g = null;
    private static MyKeyAdapter keyboard = null;

    public static void initGraphics(){

        frame = new MyFrame("Projeto COO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Util.WIDTH, Util.HEIGHT);
        frame.setResizable(false);
        frame.setVisible(true);

        keyboard = new MyKeyAdapter();
        frame.addKeyListener(keyboard);
        frame.requestFocus();

        frame.createBufferStrategy(2);
        g = frame.getBufferStrategy().getDrawGraphics();
    }

    public static void setColor(Color c){

        g.setColor(c);
    }

    public static void drawLine(double x1, double y1, double x2, double y2){

        g.drawLine((int) Math.round(x1), (int) Math.round(y1), (int) Math.round(x2), (int) Math.round(y2));
    }

    public static void drawCircle(double cx, double cy, double radius){

        int x = (int) Math.round(cx - radius);
        int y = (int) Math.round(cy - radius);
        int width = (int) Math.round(2 * radius);
        int height = (int) Math.round(2 * radius);

        g.drawOval(x, y, width, height);
    }

    public static void drawDiamond(double x, double y, double radius){

        int x1 = (int) Math.round(x);
        int y1 = (int) Math.round(y - radius);

        int x2 = (int) Math.round(x + radius);
        int y2 = (int) Math.round(y);

        int x3 = (int) Math.round(x);
        int y3 = (int) Math.round(y + radius);

        int x4 = (int) Math.round(x - radius);
        int y4 = (int) Math.round(y);

        drawLine(x1, y1, x2, y2);
        drawLine(x2, y2, x3, y3);
        drawLine(x3, y3, x4, y4);
        drawLine(x4, y4, x1, y1);
    }

    public static void drawPlayer(double player_X, double player_Y, double player_size){

        GameLib.drawLine(player_X - player_size, player_Y + player_size, player_X, player_Y - player_size);
        GameLib.drawLine(player_X + player_size, player_Y + player_size, player_X, player_Y - player_size);
        GameLib.drawLine(player_X - player_size, player_Y + player_size, player_X, player_Y + player_size * 0.5);
        GameLib.drawLine(player_X + player_size, player_Y + player_size, player_X, player_Y + player_size * 0.5);
    }

    public static void drawExplosion(double x, double y, double alpha){

        int p = 5;
        int r = (int) (255 - Math.pow(alpha, p) * 255);
        int g = (int) (128 - Math.pow(alpha, p) * 128);
        int b = 0;

        GameLib.setColor(new Color(r, g, b));
        GameLib.drawCircle(x, y, alpha * alpha * 40);
        GameLib.drawCircle(x, y, alpha * alpha * 40 + 1);
    }

    public static void fillRect(double cx, double cy, double width, double height){

        int x = (int) Math.round(cx - width/2);
        int y = (int) Math.round(cy - height/2);

        g.fillRect(x, y, (int) Math.round(width), (int) Math.round(height));
    }

    public static void display(){

        g.dispose();
        frame.getBufferStrategy().show();
        Toolkit.getDefaultToolkit().sync();
        g = frame.getBufferStrategy().getDrawGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, frame.getWidth() - 1, frame.getHeight() - 1);
        g.setColor(Color.WHITE);
    }

    public static boolean isKeyPressed(int index){

        return keyboard.isKeyPressed(index);
    }

    public static void debugKeys(){

        keyboard.debug();
    }
}
