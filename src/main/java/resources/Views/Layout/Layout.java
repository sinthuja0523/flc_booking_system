package resources.Views.Layout;

import resources.Controllers.BookingController;
import resources.Views.Pages.BookLessonPanel;

import javax.swing.*;
import java.awt.*;

public class Layout extends JFrame {
    private final Color BG = new Color(245, 247, 250);
    

    public Layout(BookingController system) {
        getContentPane().setBackground(BG);
        setTitle("Furzefield Leisure Centre (FLC) Booking System");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Book Lesson", new BookLessonPanel(system, this));
        // tabbedPane.addTab("Manage Booking", createManagePanel());
        // tabbedPane.addTab("Attend Lesson", createAttendPanel());
        // tabbedPane.addTab("Reports", createReportsPanel());

        add(tabbedPane);
    }
}
