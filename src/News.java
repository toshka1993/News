import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class News {
    public static void main(String[] args) throws IOException{

        MainW frame = new MainW();

        MouseAdapter listener = new MouseAdapter() {
            int startX;
            int startY;

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    startX = e.getX();
                    startY = e.getY();
                }
            }

            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - startX, currCoords.y - startY);
            }
        };

        frame.addMouseListener(listener);
        frame.addMouseMotionListener(listener);

        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.setVisible(true);
            }
        });
    }
}
