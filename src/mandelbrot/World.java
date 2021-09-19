package mandelbrot;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author arthu
 */
class World {

    int currentStep;

    /**
     * First pass, resolution is set to its default value, so only a few lines
     * are displayed.
     * For every next pass, the resolution is increased by one, and each line is
     * split in two, so we draw twice as many lines as before.
     */
    private int chunkResolution;
    private int chunkHeight; // 0 if not set, or the actual nb of pixel lines of one chunk;

    public World() {
        currentStep = 0;
    }

    public void paint(Graphics g, double x0, double y0, double zoom) {

        paintCenter(g, x0, y0, zoom);
        paintDots(g, x0, y0, zoom);
    }

    /**
     * Paint several rows of the image.
     * Each step corresponds to a batch of rows. First batch starts at the top
     * left pixel and spans over several rows, second batch follows. Combined,
     * all batches make up for the whole image.
     *
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     * @param currentStep
     */
    public void paintStepRow(Graphics g, double x0, double y0, double zoom) {

        int nbSteps = g.getClipBounds().height / chunkResolution;

        if (currentStep < nbSteps) {
            int nbPixels = g.getClipBounds().height * g.getClipBounds().width;

            final int startPixel = nbPixels * currentStep / nbSteps;
            final int endPixel = nbPixels * (currentStep + 1) / nbSteps;

            for (int pixelIndex = startPixel; pixelIndex < endPixel; pixelIndex++) {
                // Find the coordinates of pixel numbered pixelIndex.
                int width = g.getClipBounds().width;
                int height = g.getClipBounds().height;
                int line = pixelIndex / width;
                int col = pixelIndex - line * width;

                // Draw the pixel at (line, col);
                double xReal = (col - x0) / zoom;
                double yReal = (height - line - y0) / zoom;
                g.setColor(getColor(xReal, yReal));
                g.drawRect(col, line, 1, 1);
            }
            if (10 * (currentStep / 10) == currentStep) {
                System.out.println("Painted step " + currentStep);
            }
        }
        currentStep++;
    }

    void resetStep() {
        currentStep = 0;
        chunkResolution = 1;
        chunkHeight = -1; // -1 means "not set", 1 or 0 mean "highest resolution"
    }

    /**
     * Paint one row of the drawing; the position and width of the row are given
     * by the tree.
     *
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     * @param level
     */
    public void paintAdaptiveStepRow(Graphics g, double x0, double y0, double zoom) {

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;

        if (chunkHeight < 0) {
            chunkHeight = height / 20;
        }

        if (chunkHeight >= 1) {

            int nbChunks = g.getClipBounds().height / chunkHeight;

            for (int chunkIndex = 0; chunkIndex < nbChunks; chunkIndex++) {
                // Draw the n-th chunk using rectangles that are one pixel wide.
                for (int col = 0; col < width; col++) {

                    double xReal = (col - x0) / zoom;
                    int yAppCenter = (int) ((0.5 + chunkIndex) * chunkHeight);
                    double yReal = (height - yAppCenter - y0) / zoom;
                    g.setColor(getColor(xReal, yReal));
                    int xAppCorner = col;
                    int yAppCorner = chunkIndex * chunkHeight;
                    g.drawLine(xAppCorner, yAppCorner, xAppCorner, yAppCorner + g.getClipBounds().height / nbChunks);
                }
            }
            chunkHeight = chunkHeight / 2;
        }
        // If size of chunk is lower than one,
        // then we have already drawn everything with the highest possible resolution.
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
