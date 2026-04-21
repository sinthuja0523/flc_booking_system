package resources.models;

import org.junit.jupiter.api.Test;

import resources.models.Booking;
import resources.models.BookingStatus;
import resources.models.Lesson;
import resources.models.Member;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void shouldCreateBookingWithBookedStatus() {
        Member member = new Member("M001", "Alice");
        Lesson lesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);

        Booking booking = new Booking("B0001", member, lesson);

        assertEquals("B0001", booking.getBookingId());
        assertEquals(member, booking.getMember());
        assertEquals(lesson, booking.getLesson());
        assertEquals(BookingStatus.BOOKED, booking.getStatus());
    }

    @Test
    void shouldAllowLessonAndStatusUpdates() {
        Member member = new Member("M001", "Alice");
        Lesson oldLesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);
        Lesson newLesson = new Lesson("L002", "Zumba", 1, 1, "Saturday", "Afternoon", 12.0);

        Booking booking = new Booking("B0001", member, oldLesson);
        booking.setLesson(newLesson);
        booking.setStatus(BookingStatus.CHANGED);

        assertEquals(newLesson, booking.getLesson());
        assertEquals(BookingStatus.CHANGED, booking.getStatus());
        assertTrue(booking.toString().contains("B0001"));
        assertTrue(booking.toString().contains("Alice"));
        assertTrue(booking.toString().contains("Zumba"));
        assertTrue(booking.toString().contains("CHANGED"));
    }
}