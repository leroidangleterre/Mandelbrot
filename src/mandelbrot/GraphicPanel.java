package mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class GraphicPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    private World world;

    private double x0, y0, zoom;
    private int xMouse, yMouse;

    private boolean isPanning;

    private int delay, paintingPeriod; // milliseconds

    private boolean paintFromBeginning;
    private Timer paintingTimer;

    // When painting in square regions, this is how many layers deep we go.
    private int nbLevels;

    public GraphicPanel(World newWorld) {
        super();
        world = newWorld;
        x0 = 501;
        y0 = 444;
        zoom = 27.00;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(new MandelKeyListener(this));
        this.addComponentListener(this);

        isPanning = false;

        delay = 0;
        paintingPeriod = 100;

        nbLevels = 1;

        paintingTimer = new Timer();
        restartTimer();

        paintFromBeginning = true;
        world.resetStep();
    }

    private void restartTimer() {
        paintingTimer.cancel();
        paintingTimer = new Timer();
        paintingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, paintingPeriod);
    }

    @Override
    public void paintComponent(Graphics g) {

        if (paintFromBeginning) {
            paintFromBeginning = false;
            g.setColor(Color.gray);
            g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
            world.resetStep();
        }
        world.paintAdaptiveStepSquares(g, x0, y0, zoom);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            isPanning = false;
            double xWorld = (e.getX() - x0) / zoom;
            double yWorld = (this.getHeight() - e.getY() - y0) / zoom;
            world.mousePressed(xWorld, yWorld);
        } else if (e.getButton() == 2) {
            isPanning = true;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            world.mouseReleased();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - xMouse;
        int dy = e.getY() - yMouse;
        xMouse = e.getX();
        yMouse = e.getY();

        if (isPanning) {
            x0 += dx;
            y0 -= dy;
            paintFromBeginning = true;
            restartTimer();
        } else {
            double xWorld = (e.getX() - x0) / zoom;
            double yWorld = (this.getHeight() - e.getY() - y0) / zoom;
            world.mouseDragged(xWorld, yWorld);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        double zoomFact = 1.0;

        switch (e.getWheelRotation()) {
        case -1:
            // Zoom in
            zoomFact = 1.1;
            break;
        case 1:
            // Zoom out
            zoomFact = 1 / 1.1;
            break;
        default:
            break;
        }

        int h = this.getHeight();

        x0 = zoomFact * (x0 - e.getX()) + e.getX();
        y0 = h - e.getY() - zoomFact * (h - y0 - e.getY());
        zoom = zoom * zoomFact;
        paintFromBeginning = true;
        restartTimer();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        paintFromBeginning = true;
        restartTimer();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        paintFromBeginning = true;
        restartTimer();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }

    void increaseNbLevels(int increase) {
        if (nbLevels + increase >= 0) {
            nbLevels += increase;
            repaint();
        }
    }

}
