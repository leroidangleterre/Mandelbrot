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
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class GraphicPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    private World world;

    private double x0, y0, zoom;
    private int xMouse, yMouse;
    private double xWorld, yWorld;

    private boolean isPanning;

    private int delay, paintingPeriod; // milliseconds

    private boolean paintFromBeginning;
    private Timer paintingTimer;

    // The list of regions must be kept sorted: list must start with regions with lowest LOD
//    private ArrayList<PaintingRegion> regionList;
    // When painting in square regions, this is how many layers deep we go.
    private int nbLevels;
    private JFrame window;

    public GraphicPanel(World newWorld) {
        super();
        world = newWorld;
        if (newWorld.getType() == World.DrawingType.MANDELBROT) {
            x0 = 684;
            y0 = 453;
            zoom = 304.48;
        } else {
            x0 = 0.0;
            y0 = 0.0;
            zoom = 1.0;
        }
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

//        regionList = new ArrayList<>();
//        regionList.add(new PaintingRegion(zoom));
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

        boolean keepOldMethod = true;
        if (keepOldMethod) {
            if (paintFromBeginning) {
                paintFromBeginning = false;
                g.setColor(Color.black);
                g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
                world.resetStep();
            }
            world.paintWithTimeLimit(g, x0, y0, zoom);
            setWindowTitle(g.getClipBounds().width, g.getClipBounds().height);
//            world.paintRecursionPath(xWorld, yWorld, g, x0, y0, zoom);
        } else {
//            if (regionList.isEmpty()) {
//                // Create first region
//                regionList.add(new PaintingRegion(0, 0, g.getClipBounds().width, g.getClipBounds().height));
//            }
//
//            boolean keepPainting = true;
//            long startDate = System.currentTimeMillis();
//            long maxDrawingDuration = 300;
//
//            while (keepPainting) {
//
//                // Select region with the current lowest LOD
//                PaintingRegion lowestLODRegion = regionList.get(0);
//                // Paint a chunk of it
////                if (!lowestLODRegion.isDone()) {
//                lowestLODRegion.paint(g, world, x0, y0, zoom);
////                }
//                if (System.currentTimeMillis() > startDate + maxDrawingDuration) {
//                    keepPainting = false;
//                }
//            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            isPanning = false;
            xWorld = (e.getX() - x0) / zoom;
            yWorld = (this.getHeight() - e.getY() - y0) / zoom;
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

    /**
     * Shift the current PaintRegions, clip those that go outside the frame, and
     * create new ones on the other side.
     *
     * @param e
     */
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
            recomputeRegions(dx, dy);
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
        xWorld = (e.getX() - x0) / zoom;
        yWorld = (this.getHeight() - e.getY() - y0) / zoom;
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
        resetRegions();
        paintFromBeginning = true;
        restartTimer();
//        System.out.println("x0 = " + x0 + ", y0 = " + y0 + ", zoom = " + zoom);
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

    void setWindow(JFrame w) {
        this.window = w;
    }

    private void setWindowTitle(int currentWidth, int currentHeight) {

        double xCenter = (currentWidth / 2 - x0) / this.zoom;
        double yCenter = (currentHeight / 2 - y0) / zoom;

        String newTitle = "x: " + xCenter + ", y: " + yCenter + ", zoom: " + zoom;
        window.setTitle(newTitle);
    }

    private void recomputeRegions(int dx, int dy) {
        // TODO
    }

    private void resetRegions() {
//        regionList.clear();
    }

}
