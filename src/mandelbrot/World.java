package mandelbrot;

import java.awt.Color;
import java.awt.Graphics;

import colorramp.ColorRamp;

/**
 *
 * @author arthu
 */
class World {

    int currentStep;

    enum DrawingType {
        HYPERBOLIC, MANDELBROT, HEART
    }

    /**
     * First pass, resolution is set to its default value, so only a few lines
     * are displayed.
     * For every next pass, the resolution is increased by one, and each line is
     * split in two, so we draw twice as many lines as before.
     */
    private int chunkResolution;
    private int chunkHeight; // 0 if not set, or the actual nb of pixel lines of one chunk;

    private int lastLine;

    private ColorRamp ramp;
    int maxSteps;

    public World() {
        currentStep = 0;

        ramp = new ColorRamp();
        ramp.addValue(20, Color.blue.darker());
        ramp.addValue(30, Color.white);
        ramp.addValue(50, Color.yellow);
        ramp.addValue(200, Color.red);
        ramp.addValue(400, Color.blue);
        ramp.addValue(800, Color.white);
        ramp.addValue(1300, Color.red);
        ramp.addValue(1900, Color.black);
        maxSteps = 1900;

        lastLine = 0;
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

    /**
     * Paint the Mandelbrot set with increasing resolution, using square chunks.
     *
     * @param g
     * @param x0
     * @param y0
     * @param zoom
     */
    public void paintAdaptiveStepSquares(Graphics g, double x0, double y0, double zoom) {

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;

        if (chunkHeight < 0) {
            chunkHeight = height / 20;
        }

        if (chunkHeight >= 1) {

            int nbLines = height / chunkHeight;
            int nbCols = width / chunkHeight;

            for (int line = 0; line < nbLines; line++) {

                for (int col = 0; col < nbCols; col++) {

                    int xAppCenter = (int) ((0.5 + col) * chunkHeight);
                    double xReal = (xAppCenter - x0) / zoom;

                    int yAppCenter = (int) ((0.5 + line) * chunkHeight);
                    double yReal = (height - yAppCenter - y0) / zoom;

                    g.setColor(getColor(xReal, yReal));
                    int xAppCorner = col * chunkHeight;
                    int yAppCorner = line * chunkHeight;
                    g.fillRect(xAppCorner, yAppCorner, chunkHeight, chunkHeight);
                }
            }
            chunkHeight = chunkHeight / 2;
        }
        // If size of chunk is lower than one,
        // then we have already drawn everything with the highest possible resolution.
    }

    public void paintWithTimeLimit(Graphics g, double x0, double y0, double zoom) {
        long startDate = System.currentTimeMillis();
        long maxDrawingDuration = 300;

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;

        if (chunkHeight < 0) {
            chunkHeight = height / 20;
        }

        if (chunkHeight >= 1) {

            int nbLines = height / chunkHeight;
            int nbCols = width / chunkHeight;

            int line = lastLine;
            boolean keepPainting = true;
            while (keepPainting) {

                for (int col = 0; col <= nbCols; col++) {
                    int xAppCenter = (int) ((0.5 + col) * chunkHeight);
                    double xReal = (xAppCenter - x0) / zoom;

                    int yAppCenter = (int) ((0.5 + line) * chunkHeight);
                    double yReal = (height - yAppCenter - y0) / zoom;

                    g.setColor(getColor(xReal, yReal));
                    int xAppCorner = col * chunkHeight;
                    int yAppCorner = line * chunkHeight;
                    g.fillRect(xAppCorner, yAppCorner, chunkHeight, chunkHeight);
                }

                if (System.currentTimeMillis() > startDate + maxDrawingDuration) {
                    keepPainting = false;
                    // Next time, start where we left off with the same resolution.
                    lastLine = line;
                }

                if (line >= nbLines) {
                    keepPainting = false;
                    // Next time, restart from scratch with a better resolution.
                    lastLine = 0;
                    chunkHeight = chunkHeight / 2;
                }

                line++;
            }
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

        DrawingType type = DrawingType.MANDELBROT;

        switch (type) {
        case HYPERBOLIC:
            // Hyperbolic space:
            // First, not hyperbolic
            double xCurrent = x,
             yCurrent = y;

            // Second, hyperbolic space
            // Map the norm of points from [0, 1[ to [0, infinity[
            double norm = Math.sqrt(x * x + y * y);
            if (norm > 1) {
                return Color.gray;
            }
            double proportionFactor = 5 / (norm - 1);
            xCurrent *= proportionFactor;
            yCurrent *= proportionFactor;

            boolean xIsEven,
             yIsEven;
            if (xCurrent < 0) {
                xCurrent--;
            }
            if (yCurrent
                    < 0) {
                yCurrent--;
            }
            xIsEven = ((((int) xCurrent) / 2) * 2 == (int) xCurrent);
            yIsEven = ((((int) yCurrent) / 2) * 2 == (int) yCurrent);

            if (xIsEven && yIsEven || (!xIsEven) && (!yIsEven)) {
                return Color.red;
            } else {
                return Color.blue;
            }
        case MANDELBROT:
            // Mandelbrot:
            // Limit of convergence; if value goes higher, we consider it does not converge
            double max = 10000000;

            xCurrent = 0;
            yCurrent = 0;
            double xNext;
            double yNext;
            int i = 0;
            while (i < maxSteps && xCurrent * xCurrent + yCurrent * yCurrent < max * max) {

                xNext = xCurrent * xCurrent - yCurrent * yCurrent + x;
                yNext = 2 * xCurrent * yCurrent + y;

                xCurrent = xNext;
                yCurrent = yNext;
                i++;
            }
            return ramp.getValue(i);
        case HEART:
            // Mandelbrot:
            // Limit of convergence; if value goes higher, we consider it does not converge
            max = 10000000;

            xCurrent = 0;
            yCurrent = 0;
            i = 0;
            while (i < maxSteps && xCurrent * xCurrent + yCurrent * yCurrent < max * max) {

                // The Heart
                xNext = xCurrent * xCurrent - yCurrent * yCurrent + x;
                xNext = xNext * xNext;
                yNext = 2 * xCurrent * yCurrent + y;

                xCurrent = xNext;
                yCurrent = yNext;
                i++;
            }
            return ramp.getValue(i);
        }
        return Color.black;
    }

    void paintRecursionPath(double xInit, double yInit, Graphics g,
            double x0, double y0, double zoom) {

        double xPrev = 0;
        double yPrev = 0;

        for (int step = 0; step < maxSteps; step++) {
            // Compute coordinates after one step
            double xCurrent = xPrev * xPrev - yPrev * yPrev + xInit;
            double yCurrent = 2 * xPrev * yPrev + yInit;

            // Draw the line
            int xAppPrev = (int) (xPrev * zoom + x0);
            int yAppPrev = (int) (g.getClipBounds().height - (yPrev * zoom + x0));
            int xCurrentPrev = (int) (xCurrent * zoom + x0);
            int yCurrentPrev = (int) (g.getClipBounds().height - (yCurrent * zoom + x0));
            g.setColor(Color.black);
            g.drawLine(xAppPrev, yAppPrev, xCurrentPrev, yCurrentPrev);

            // Prepare next step
            xPrev = xCurrent;
            yPrev = yCurrent;
        }
    }

}
