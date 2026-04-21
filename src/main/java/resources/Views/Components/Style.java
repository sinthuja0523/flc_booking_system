package resources.views.components;

import javax.swing.*;
import java.awt.*;

public class Style {
    private static final Color PRIMARY = new Color(52, 152, 219);
    private static final Color HOVER = new Color(41, 128, 185);
    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 13);

    // default style
    public static void changeButtonStyle(JButton btn) {
        changeButtonStyle(btn, PRIMARY, HOVER);
    }

    // custom style
    public static void changeButtonStyle(JButton btn, Color bg, Color hover) {

        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
    }
}