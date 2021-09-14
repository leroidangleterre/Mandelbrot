/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author arthu
 */
public class MandelKeyListener implements KeyListener {

    private GraphicPanel p;

    public MandelKeyListener(GraphicPanel newPanel) {
        p = newPanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
        case 'b':
            break;
        default:
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
