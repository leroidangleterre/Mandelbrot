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

        // Paint all the dots
        for (int i = 0; i < nbPoints; i++) {

            // Coordinates of the tested point
            double dx = xMax - xMin;
            double dy = yMax - yMin;
            double x = (xMax + xMin) / 2 + dx * (new Random().nextDouble() - .5);
            double y = (yMax + yMin) / 2 + dy * (new Random().nextDouble() - .5);

            int xApp = (int) (x0 + x * zoom);
            int yApp = (int) (h - (y0 + y * zoom));
            g.setColor(getColor(x, y));
            int radius = 5;
            g.fillOval(xApp - radius, yApp - radius, 2 * radius, 2 * radius);
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
