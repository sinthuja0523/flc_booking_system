package resources.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import resources.controllers.BookingController;
import resources.models.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingControllerTest {

    private BookingController controller;
    private Member alice;
    private Member bob;
    private Lesson yogaSaturdayMorning;
    private Lesson zumbaSaturdayAfternoon;
    private Lesson aquaciseSaturdayEvening;

    @BeforeEach
    void setUp() {
        controller = new BookingController();
        alice = controller.getMember("M001");
        bob = controller.getMember("M002");
        yogaSaturdayMorning = controller.findLesson(1, 1, "Saturday", "Morning", "Yoga");
        zumbaSaturdayAfternoon = controller.findLesson(1, 1, "Saturday", "Afternoon", "Zumba");
        aquaciseSaturdayEvening = controller.findLesson(1, 1, "Saturday", "Evening", "Aquacise");
    }

    @Test
    void shouldLoadDefaultMembersLessonsAndExerciseTypes() {
        assertEquals(5, controller.getAllMembers().size());
        assertEquals(48, controller.getAllLessons().size());
        assertEquals(4, controller.getAllExerciseTypes().size());
        assertTrue(controller.getAllExerciseTypes().contains("Yoga"));
        assertTrue(controller.getAllExerciseTypes().contains("Zumba"));
        assertTrue(controller.getAllExerciseTypes().contains("Aquacise"));
        assertTrue(controller.getAllExerciseTypes().contains("BoxFit"));
    }

    @Test
    void shouldAddAndGetMember() {
        Member newMember = new Member("M010", "Farah");
        controller.addMember(newMember);

        assertEquals(newMember, controller.getMember("M010"));
    }

    @Test
    void shouldFindLessonsByIdDayTypeAndHelperMethods() {
        Lesson lessonById = controller.getLessonById("L001");
        assertNotNull(lessonById);
        assertEquals(lessonById, controller.getLesson("L001"));

        List<Lesson> saturdayLessons = controller.getLessonsByDay("Saturday");
        List<Lesson> yogaLessons = controller.getLessonsByType("Yoga");

        assertEquals(24, saturdayLessons.size());
        assertEquals(16, yogaLessons.size());
        assertNotNull(controller.findLesson(1, 1, "Saturday", "Morning", "Yoga"));
        assertNull(controller.findLesson(9, 9, "Monday", "Night", "Pilates"));
    }

    @Test
    void shouldBookLessonSuccessfully() {
        String result = controller.bookLesson(alice, yogaSaturdayMorning);

        assertTrue(result.startsWith("Success: B0001"));
        Booking booking = controller.getBooking("B0001");
        assertNotNull(booking);
        assertEquals(alice, booking.getMember());
        assertEquals(yogaSaturdayMorning, booking.getLesson());
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
        assertEquals(1, yogaSaturdayMorning.getCurrentAttendeesCount());
    }

    @Test
    void shouldRejectDuplicateBookingForSameMemberAndLesson() {
        controller.bookLesson(alice, yogaSaturdayMorning);

        String result = controller.bookLesson(alice, yogaSaturdayMorning);

        assertEquals("Error: Duplicate booking detected.", result);
        assertEquals(1, controller.getAllBookings().size());
    }

    @Test
    void shouldRejectBookingWhenLessonIsFull() {
        Member c = controller.getMember("M003");
        Member d = controller.getMember("M004");
        Member e = controller.getMember("M005");

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(bob, yogaSaturdayMorning);
        controller.bookLesson(c, yogaSaturdayMorning);
        controller.bookLesson(d, yogaSaturdayMorning);

        String result = controller.bookLesson(e, yogaSaturdayMorning);

        assertEquals("Error: Lesson is full.", result);
        assertEquals(4, yogaSaturdayMorning.getCurrentAttendeesCount());
    }

    @Test
    void shouldChangeBookingSuccessfully() {
        controller.bookLesson(alice, yogaSaturdayMorning);

        String result = controller.changeBooking("B0001", zumbaSaturdayAfternoon);

        assertEquals("Success: Booking changed to Zumba", result);
        Booking booking = controller.getBooking("B0001");
        assertEquals(zumbaSaturdayAfternoon, booking.getLesson());
        assertEquals(BookingStatus.CHANGED, booking.getStatus());
        assertEquals(0, yogaSaturdayMorning.getCurrentAttendeesCount());
        assertEquals(1, zumbaSaturdayAfternoon.getCurrentAttendeesCount());
    }

    @Test
    void shouldRejectChangeWhenBookingNotFound() {
        String result = controller.changeBooking("B9999", zumbaSaturdayAfternoon);
        assertEquals("Error: Booking not found.", result);
    }

    @Test
    void shouldRejectChangeForCancelledOrAttendedBooking() {
        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.cancelBooking("B0001");

        String cancelledResult = controller.changeBooking("B0001", zumbaSaturdayAfternoon);
        assertEquals("Error: Cannot change a cancelled or attended booking.", cancelledResult);

        controller = new BookingController();
        alice = controller.getMember("M001");
        yogaSaturdayMorning = controller.findLesson(1, 1, "Saturday", "Morning", "Yoga");
        zumbaSaturdayAfternoon = controller.findLesson(1, 1, "Saturday", "Afternoon", "Zumba");

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.attendLesson("B0001", 5, "Great");

        String attendedResult = controller.changeBooking("B0001", zumbaSaturdayAfternoon);
        assertEquals("Error: Cannot change a cancelled or attended booking.", attendedResult);
    }

    @Test
    void shouldRejectChangeToDuplicateNewLesson() {
        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(alice, zumbaSaturdayAfternoon);

        String result = controller.changeBooking("B0001", zumbaSaturdayAfternoon);

        assertEquals("Error: Duplicate booking detected in the new lesson.", result);
    }

    @Test
    void shouldRejectChangeWhenNewLessonIsFull() {
        Member c = controller.getMember("M003");
        Member d = controller.getMember("M004");
        Member e = controller.getMember("M005");

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(bob, zumbaSaturdayAfternoon);
        controller.bookLesson(c, zumbaSaturdayAfternoon);
        controller.bookLesson(d, zumbaSaturdayAfternoon);
        controller.bookLesson(e, zumbaSaturdayAfternoon);

        String result = controller.changeBooking("B0001", zumbaSaturdayAfternoon);

        assertEquals("Error: New lesson is full.", result);
    }

    @Test
    void shouldCancelBookingSuccessfully() {
        controller.bookLesson(alice, yogaSaturdayMorning);

        String result = controller.cancelBooking("B0001");

        assertEquals("Success: Booking B0001 cancelled.", result);
        assertEquals(BookingStatus.CANCELLED, controller.getBooking("B0001").getStatus());
        assertEquals(0, yogaSaturdayMorning.getCurrentAttendeesCount());
    }

    @Test
    void shouldRejectCancelForMissingCancelledOrAttendedBooking() {
        assertEquals("Error: Booking not found.", controller.cancelBooking("B9999"));

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.cancelBooking("B0001");
        assertEquals("Error: Cannot cancel a cancelled or attended booking.", controller.cancelBooking("B0001"));

        controller = new BookingController();
        alice = controller.getMember("M001");
        yogaSaturdayMorning = controller.findLesson(1, 1, "Saturday", "Morning", "Yoga");

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.attendLesson("B0001", 5, "Great class");
        assertEquals("Error: Cannot cancel a cancelled or attended booking.", controller.cancelBooking("B0001"));
    }

    @Test
    void shouldAttendLessonAndCreateReview() {
        controller.bookLesson(alice, yogaSaturdayMorning);

        String result = controller.attendLesson("B0001", 4, "Nice session");

        assertEquals("Success: Lesson attended. Review recorded.", result);
        assertEquals(BookingStatus.ATTENDED, controller.getBooking("B0001").getStatus());
        assertEquals(4.0, yogaSaturdayMorning.getAverageRating());
    }

    @Test
    void shouldRejectAttendForMissingCancelledOrAlreadyAttendedBooking() {
        assertEquals("Error: Booking not found.", controller.attendLesson("B9999", 5, "x"));

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.cancelBooking("B0001");
        assertEquals("Error: Booking is cancelled or already attended.", controller.attendLesson("B0001", 5, "x"));

        controller = new BookingController();
        alice = controller.getMember("M001");
        yogaSaturdayMorning = controller.findLesson(1, 1, "Saturday", "Morning", "Yoga");

        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.attendLesson("B0001", 5, "x");
        assertEquals("Error: Booking is cancelled or already attended.", controller.attendLesson("B0001", 4, "y"));
    }

    @Test
    void shouldReturnAvailableLessonsForChangeExcludingCurrentLesson() {
        controller.bookLesson(alice, yogaSaturdayMorning);

        List<Lesson> candidates = controller.getAvailableLessonsForChange("B0001");

        assertEquals(47, candidates.size());
        assertFalse(candidates.contains(yogaSaturdayMorning));
        assertTrue(candidates.contains(zumbaSaturdayAfternoon));
        assertTrue(controller.getAvailableLessonsForChange("B9999").isEmpty());
    }

    @Test
    void shouldGenerateMonthlyLessonReport() {
        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(bob, zumbaSaturdayAfternoon);
        controller.attendLesson("B0001", 5, "Excellent");
        controller.attendLesson("B0002", 4, "Good");

        String report = controller.generateMonthlyLessonReport(1);

        assertTrue(report.contains("Monthly Lesson Report - Month 1"));
        assertTrue(report.contains("L001"));
        assertTrue(report.contains("Yoga"));
        assertTrue(report.contains("Zumba"));
        assertTrue(report.contains("Total Attendees this month: 2"));
    }

    @Test
    void shouldGenerateMonthlyChampionReport() {
        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(bob, yogaSaturdayMorning);
        controller.attendLesson("B0001", 5, "Excellent");
        controller.attendLesson("B0002", 4, "Good");

        String report = controller.generateMonthlyChampionReport(1);

        assertTrue(report.contains("Monthly Champion Exercise Report - Month 1"));
        assertTrue(report.contains("Yoga"));
        assertTrue(report.contains("Income: $30.00"));
        assertTrue(report.contains("CHAMPION: Yoga ($30.00)"));
    }

    @Test
    void shouldHandlePriceQueriesAndUpdates() {
        assertEquals(15.0, controller.getPriceByExerciseType("Yoga"));
        assertEquals(0.0, controller.getPriceByExerciseType("Unknown"));

        assertEquals("Error: Price must be greater than 0.", controller.updatePriceByExerciseType("Yoga", 0));
        assertEquals("Error: Exercise type not found.", controller.updatePriceByExerciseType("Pilates", 20));

        String result = controller.updatePriceByExerciseType("Yoga", 25.0);
        assertEquals("Success: Price updated for Yoga", result);
        assertEquals(25.0, controller.getPriceByExerciseType("Yoga"));

        for (Lesson lesson : controller.getLessonsByType("Yoga")) {
            assertEquals(25.0, lesson.getPrice());
        }
    }

    @Test
    void shouldIncrementBookingIdsSequentially() {
        controller.bookLesson(alice, yogaSaturdayMorning);
        controller.bookLesson(bob, zumbaSaturdayAfternoon);
        controller.bookLesson(controller.getMember("M003"), aquaciseSaturdayEvening);

        assertNotNull(controller.getBooking("B0001"));
        assertNotNull(controller.getBooking("B0002"));
        assertNotNull(controller.getBooking("B0003"));
    }
}