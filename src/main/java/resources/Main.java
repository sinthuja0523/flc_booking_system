package resources;

import javax.swing.SwingUtilities;

import resources.Controllers.BookingController;
import resources.Views.Layout.Layout;

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
