package mandelbrot;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * @author arthu
 */
public class Mandelbrot {

    public static void main(String[] args) {

        int width = 1200;
        int height = 800;

        JFrame window = new JFrame();
        World world = new World();
        GraphicPanel panel = new GraphicPanel(world);
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setLayout(new BorderLayout());
        window.add(panel, BorderLayout.CENTER);

        window.add(new MandelbrotMenu(panel), BorderLayout.NORTH);

        window.setVisible(true);
    }

}
