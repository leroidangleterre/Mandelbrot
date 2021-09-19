package mandelbrot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * @author arthu
 */
public class Mandelbrot {

    public static void main(String[] args) {

        int width = 1000;
        int height = 1000;

        JFrame window = new JFrame();
        World world = new World();
        GraphicPanel panel = new GraphicPanel(world);
        window.setPreferredSize(new Dimension(width, height));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setLayout(new BorderLayout());
        window.add(panel, BorderLayout.CENTER);

        window.setVisible(true);
        window.pack();
    }

}
