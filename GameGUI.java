import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.io.File;
import javax.imageio.ImageIO;

import java.util.Random;

public class GameGUI extends JComponent
{
    static final long serialVersionUID = 141L;

    private static final int WIDTH = 510;
    private static final int HEIGHT = 360;
    private static final int SPACE_SIZE = 60;
    private static final int GRID_W = 8;
    private static final int GRID_H = 5;
    private static final int START_LOC_X = 15;
    private static final int START_LOC_Y = 15;

    int x = START_LOC_X; 
    int y = START_LOC_Y;

    private Image bgImage;
    private Image player;
    private Point playerLoc;
    private int playerSteps;

    private int totalWalls;
    private Rectangle[] walls; 
    private Image prizeImage;
    private int totalPrizes;
    private Rectangle[] prizes;
    private int totalTraps;
    private Rectangle[] traps;

    private int prizeVal = 10;
    private int trapVal = 5;
    private int endVal = 10;
    private int offGridVal = 5;
    private int hitWallVal = 5;

    private JFrame frame;

    public GameGUI()
    {
        try { bgImage = ImageIO.read(new File("grid.png")); } 
        catch (Exception e) { System.err.println("Could not open file grid.png"); }
        try { prizeImage = ImageIO.read(new File("coin.png")); } 
        catch (Exception e) { System.err.println("Could not open file coin.png"); }
        try { player = ImageIO.read(new File("player.png")); } 
        catch (Exception e) { System.err.println("Could not open file player.png"); }

        playerLoc = new Point(x,y);

        frame = new JFrame();
        frame.setTitle("EscapeRoom");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
        frame.setResizable(false); 

        totalWalls = 20;
        totalPrizes = 3;
        totalTraps = 5;
    }

    public void createBoard()
    {
        traps = new Rectangle[totalTraps];
        createTraps();
        prizes = new Rectangle[totalPrizes];
        createPrizes();
        walls = new Rectangle[totalWalls];
        createWalls();
    }

    public int movePlayer(int incrx, int incry)
    {
        int newX = x + incrx;
        int newY = y + incry;
        playerSteps++;

        if ((newX < 0 || newX > WIDTH-SPACE_SIZE) || (newY < 0 || newY > HEIGHT-SPACE_SIZE))
        {
            System.out.println("OFF THE GRID!");
            return -offGridVal;
        }

        for (Rectangle r: walls)
        {
            int startX = (int) r.getX();
            int endX = startX + (int) r.getWidth();
            int startY = (int) r.getY();
            int endY = startY + (int) r.getHeight();

            if ((incrx > 0 && x <= startX && startX <= newX && y >= startY && y <= endY) ||
                (incrx < 0 && x >= startX && startX >= newX && y >= startY && y <= endY) ||
                (incry > 0 && y <= startY && startY <= newY && x >= startX && x <= endX) ||
                (incry < 0 && y >= startY && startY >= newY && x >= startX && x <= endX))
            {
                System.out.println("A WALL IS IN THE WAY");
                return -hitWallVal;
            }
        }

        x += incrx;
        y += incry;
        repaint();
        return 0;
    }

    public boolean isTrap(int newx, int newy)
    {
        double px = playerLoc.getX() + newx;
        double py = playerLoc.getY() + newy;

        for (Rectangle r: traps)
        {
            if (r.getWidth() > 0 && r.contains(px, py)) return true;
        }
        return false;
    }

    public int springTrap(int newx, int newy)
    {
        double px = playerLoc.getX() + newx;
        double py = playerLoc.getY() + newy;

        for (Rectangle r: traps)
        {
            if (r.contains(px, py) && r.getWidth() > 0)
            {
                r.setSize(0,0);
                System.out.println("TRAP IS SPRUNG!");
                return trapVal;
            }
        }

        System.out.println("THERE IS NO TRAP HERE TO SPRING");
        return -trapVal;
    }

    public int pickupPrize()
    {
        double px = playerLoc.getX();
        double py = playerLoc.getY();

        for (Rectangle p: prizes)
        {
            if (p.getWidth() > 0 && p.contains(px, py))
            {
                System.out.println("YOU PICKED UP A PRIZE!");
                p.setSize(0,0);
                repaint();
                return prizeVal;
            }
        }

        System.out.println("OOPS, NO PRIZE HERE");
        return -prizeVal;
    }

    public int getSteps() { return playerSteps; }

    public void setPrizes(int p) { totalPrizes = p; }
    public void setTraps(int t) { totalTraps = t; }
    public void setWalls(int w) { totalWalls = w; }

    public int replay()
    {
        int win = playerAtEnd();

        for (Rectangle p: prizes) p.setSize(SPACE_SIZE/3, SPACE_SIZE/3);
        for (Rectangle t: traps) t.setSize(SPACE_SIZE/3, SPACE_SIZE/3);

        x = START_LOC_X;
        y = START_LOC_Y;
        playerSteps = 0;
        repaint();
        return win;
    }

    public int endGame() 
    {
        int win = playerAtEnd();
        setVisible(false);
        frame.dispose();
        return win;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.drawImage(bgImage, 0, 0, null);

        for (Rectangle t: traps)
        {
            g2.setPaint(Color.WHITE);
            g2.fill(t);
        }

        for (Rectangle p: prizes)
        {
            if (p.getWidth() > 0)
            {
                g.drawImage(prizeImage, (int)p.getX(), (int)p.getY(), null);
            }
        }

        for (Rectangle r: walls)
        {
            g2.setPaint(Color.BLACK);
            g2.fill(r);
        }

        g.drawImage(player, x, y, 40, 40, null);
        playerLoc.setLocation(x,y);
    }

    private void createPrizes()
    {
        int s = SPACE_SIZE; 
        Random rand = new Random();
        for (int numPrizes = 0; numPrizes < totalPrizes; numPrizes++)
        {
            int h = rand.nextInt(GRID_H);
            int w = rand.nextInt(GRID_W);
            prizes[numPrizes] = new Rectangle(w*s + 15, h*s + 15, 15, 15);
        }
    }

    private void createTraps()
    {
        int s = SPACE_SIZE;
        Random rand = new Random();
        for (int numTraps = 0; numTraps < totalTraps; numTraps++)
        {
            int h = rand.nextInt(GRID_H);
            int w = rand.nextInt(GRID_W);
            traps[numTraps] = new Rectangle(w*s + 15, h*s + 15, 15, 15);
        }
    }

    private void createWalls()
    {
        int s = SPACE_SIZE;
        Random rand = new Random();
        for (int numWalls = 0; numWalls < totalWalls; numWalls++)
        {
            int h = rand.nextInt(GRID_H);
            int w = rand.nextInt(GRID_W);
            Rectangle r;
            if (rand.nextInt(2) == 0) r = new Rectangle(w*s + s - 5, h*s, 8, s);
            else r = new Rectangle(w*s, h*s + s - 5, s, 8);
            walls[numWalls] = r;
        }
    }

    private int playerAtEnd()
    {
        int score;
        if (playerLoc.getX() > (WIDTH - 2*SPACE_SIZE))
        {
            System.out.println("YOU MADE IT!");
            score = endVal;
        }
        else
        {
            System.out.println("OOPS, YOU QUIT TOO SOON!");
            score = -endVal;
        }
        return score;
    }

    // ----------- NEW TRAP FEATURES -------------

    public boolean findTrap() {
        double px = playerLoc.getX();
        double py = playerLoc.getY();
        for (Rectangle t : traps) {
            if (t.getWidth() > 0 && t.contains(px, py)) {
                System.out.println("There is a trap here!");
                return true;
            }
        }
        System.out.println("No trap at your location.");
        return false;
    }

    public void detrap() {
        for (Rectangle t : traps) {
            if (t.getWidth() == 0) t.setSize(SPACE_SIZE / 3, SPACE_SIZE / 3);
        }
        System.out.println("All sprung traps are reset!");
        repaint();
    }

    public int trapVictim() {
        double px = playerLoc.getX();
        double py = playerLoc.getY();
        for (Rectangle t : traps) {
            if (t.contains(px, py) && t.getWidth() > 0) {
                t.setSize(0,0);
                System.out.println("Trap sprung! You got trapped!");
                return trapVal;
            }
        }
        System.out.println("No trap to spring here.");
        return -trapVal;
    }
}
