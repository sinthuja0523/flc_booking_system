package resources.Views.Layout;

import resources.Controllers.BookingController;
import resources.Views.Pages.AttendLessonPanel;
import resources.Views.Pages.BookLessonPanel;
import resources.Views.Pages.ManageBookingPanel;
import resources.Views.Pages.ReportsPanel;
import resources.Views.Pages.SettingsPanel;

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
        BookLessonPanel bookLessonPanel = new BookLessonPanel(system, this);

        tabbedPane.addTab("Book Lesson", new BookLessonPanel(system, this));
        tabbedPane.addTab("Manage Booking", new ManageBookingPanel(system, this));
        tabbedPane.addTab("Attend Lesson", new AttendLessonPanel(system, this));
        tabbedPane.addTab("Reports", new ReportsPanel(system, this));
        tabbedPane.addTab("Settings", new SettingsPanel(system, this, bookLessonPanel));
        
        add(tabbedPane);
    }
}
