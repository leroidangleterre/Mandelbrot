package mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author arthu
 */
class World {

    private int nbPoints;

    public World() {
        nbPoints = 50000;
    }

    public void paint(Graphics g, double x0, double y0, double zoom) {

        paintCenter(g, x0, y0, zoom);
        paintDots(g, x0, y0, zoom);
    }

    private void paintCenter(Graphics g, double x0, double y0, double zoom) {
        g.setColor(Color.black);
        int h = g.getClipBounds().height;
        g.drawLine((int) x0, (int) (h - y0), (int) (x0 + zoom), (int) (h - y0));
        g.drawLine((int) x0, (int) (h - y0), (int) x0, (int) (h - (y0 + zoom)));
    }

    protected void mousePressed(double xWorld, double yWorld) {
    }

    protected void mouseReleased() {
    }

    void mouseDragged(double xWorld, double yWorld) {
    }

    void step() {
    }

    void restart() {
    }

    /**
     * Randomly choose points on the screen and paint them.
     *
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     */
    private void paintDots(Graphics g, double x0, double y0, double zoom) {

        // Choose the coordinates
        int w = g.getClipBounds().width;
        int h = g.getClipBounds().height;

        // xWorld = (xApp-x0)/zoom
        double xMin = -x0 / zoom;
        double xMax = (w - x0) / zoom;

        // yWorld = (h-yApp-y0)/zoom;
        double yMin = (h - y0) / zoom;
        double yMax = (h - h - y0) / zoom;

        // Paint each pixel
        for (int col = 0; col < w; col++) {
            for (int line = 0; line < h; line++) {
                double xReal = (col - x0) / zoom;
                double yReal = (h - line - y0) / zoom;
                g.setColor(getColor(xReal, yReal));
                g.drawRect(col, line, 1, 1);
            }
        }
    }

    /**
     * Choose the color of the dot based on its position,
     * and the convergence of the operation.
     *
     * @param x
     * @param y
     * @return a color that depends on the convergence of the suite.
     */
    private Color getColor(double x, double y) {

        // Limit of convergence; if value goes higher, we consider it does not converge
        double max = 100000;

        double xCurrent = 0;
        double yCurrent = 0;
        double xNext;
        double yNext;

        for (int i = 0; i < 50; i++) {
            xNext = xCurrent * xCurrent - yCurrent * yCurrent + x;
            yNext = 2 * xCurrent * yCurrent + y;
            xCurrent = xNext;
            yCurrent = yNext;
        }
        if (xCurrent * xCurrent + yCurrent * yCurrent < max) {
            return Color.red;
        } else {
            return Color.black;
        }
    }
}
