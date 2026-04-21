package resources;

import javax.swing.SwingUtilities;

import resources.controllers.BookingController;
import resources.views.layouts.Layout;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Furzefield Leisure Centre (FLC) Booking System");

            BookingController system = new BookingController();
            Layout layout = new Layout(system);
            layout.setVisible(true);
        });
    }
}
