/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author arthu
 */
public class MandelbrotMenu extends JPanel {

    private final JButton plankButton;

    public MandelbrotMenu(GraphicPanel panel) {

        super();

        MandelKeyListener keyListener = new MandelKeyListener(panel);

        plankButton = new JButton("CustomButton");
        plankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Click custom button");
            }
        });
        plankButton.addKeyListener(keyListener);
        this.add(plankButton);
    }
}
