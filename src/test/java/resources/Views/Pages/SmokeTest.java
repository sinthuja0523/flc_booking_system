package resources.views.Pages;

import org.junit.jupiter.api.Test;

import resources.controllers.BookingController;
import resources.views.Pages.AttendLessonPanel;
import resources.views.Pages.BookLessonPanel;
import resources.views.Pages.ManageBookingPanel;
import resources.views.Pages.ReportsPanel;
import resources.views.Pages.SettingsPanel;

import static org.junit.jupiter.api.Assertions.*;

class PanelSmokeTest {

    @Test
    void shouldCreateAllPanels() {
        BookingController controller = new BookingController();

        BookLessonPanel bookLessonPanel = new BookLessonPanel(controller, null);
        ManageBookingPanel manageBookingPanel = new ManageBookingPanel(controller, null);
        AttendLessonPanel attendLessonPanel = new AttendLessonPanel(controller, null);
        ReportsPanel reportsPanel = new ReportsPanel(controller, null);
        SettingsPanel settingsPanel = new SettingsPanel(controller, null, bookLessonPanel);

        assertNotNull(bookLessonPanel);
        assertNotNull(manageBookingPanel);
        assertNotNull(attendLessonPanel);
        assertNotNull(reportsPanel);
        assertNotNull(settingsPanel);
    }

    @Test
    void refreshLessonTableShouldNotThrow() {
        BookingController controller = new BookingController();
        BookLessonPanel panel = new BookLessonPanel(controller, null);

        assertDoesNotThrow(panel::refreshLessonTable);
    }
}