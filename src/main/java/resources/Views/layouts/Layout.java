package resources.views.layouts;

import resources.controllers.BookingController;
import resources.views.Pages.AttendLessonPanel;
import resources.views.Pages.BookLessonPanel;
import resources.views.Pages.ManageBookingPanel;
import resources.views.Pages.ReportsPanel;
import resources.views.Pages.SettingsPanel;
import resources.views.Pages.ReviewsCommentsPanel;

import javax.swing.*;
import java.awt.*;

public class Layout extends JFrame {
    private final Color BG = new Color(245, 247, 250);

    public Layout(BookingController system) {
        getContentPane().setBackground(BG);
        setTitle("Furzefield Leisure Centre (FLC) Booking System");
        setSize(1200, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        BookLessonPanel bookLessonPanel = new BookLessonPanel(system, this);

        tabbedPane.addTab("Book Lesson", new BookLessonPanel(system, this));
        tabbedPane.addTab("Manage Booking", new ManageBookingPanel(system, this));
        tabbedPane.addTab("Attend Lesson", new AttendLessonPanel(system, this));
        tabbedPane.addTab("Reports", new ReportsPanel(system, this));
        tabbedPane.addTab("Reviews & Comments", new ReviewsCommentsPanel(system, this));
        tabbedPane.addTab("Settings", new SettingsPanel(system, this, bookLessonPanel));
        
        add(tabbedPane);
    }
}
