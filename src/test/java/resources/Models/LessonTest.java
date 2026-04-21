package resources.Models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LessonTest {

    @Test
    void shouldReturnAllLessonFields() {
        Lesson lesson = new Lesson("L001", "Yoga", 1, 2, "Saturday", "Morning", 15.0);

        assertEquals("L001", lesson.getId());
        assertEquals("Yoga", lesson.getExerciseType());
        assertEquals(1, lesson.getMonth());
        assertEquals(2, lesson.getWeekend());
        assertEquals("Saturday", lesson.getDay());
        assertEquals("Morning", lesson.getTime());
        assertEquals(15.0, lesson.getPrice());
        assertEquals(4, lesson.getCapacity());
        assertEquals(0, lesson.getCurrentAttendeesCount());
        assertTrue(lesson.hasSpace());
    }

    @Test
    void shouldAddAndRemoveAttendeesAndPreventDuplicates() {
        Lesson lesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);
        Member alice = new Member("M001", "Alice");
        Member bob = new Member("M002", "Bob");
        Member charlie = new Member("M003", "Charlie");
        Member diana = new Member("M004", "Diana");
        Member eve = new Member("M005", "Eve");

        assertTrue(lesson.addAttendee(alice));
        assertFalse(lesson.addAttendee(alice));
        assertTrue(lesson.addAttendee(bob));
        assertTrue(lesson.addAttendee(charlie));
        assertTrue(lesson.addAttendee(diana));
        assertFalse(lesson.hasSpace());
        assertFalse(lesson.addAttendee(eve));
        assertEquals(4, lesson.getCurrentAttendeesCount());

        assertTrue(lesson.removeAttendee(bob));
        assertFalse(lesson.removeAttendee(eve));
        assertEquals(3, lesson.getCurrentAttendeesCount());
        assertTrue(lesson.hasSpace());
    }

    @Test
    void shouldReturnCopyOfAttendees() {
        Lesson lesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);
        Member alice = new Member("M001", "Alice");
        lesson.addAttendee(alice);

        List<Member> attendees = lesson.getAttendees();
        attendees.clear();

        assertEquals(1, lesson.getCurrentAttendeesCount());
    }

    @Test
    void shouldHandleReviewsAndAverageRating() {
        Lesson lesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);

        assertEquals(0.0, lesson.getAverageRating());

        lesson.addReview(new Review(4, "Good"));
        lesson.addReview(new Review(2, "Okay"));
        lesson.addReview(null);

        assertEquals(3.0, lesson.getAverageRating());
    }

    @Test
    void shouldUpdatePriceAndToString() {
        Lesson lesson = new Lesson("L001", "Yoga", 1, 1, "Saturday", "Morning", 15.0);
        lesson.setPrice(20.0);

        assertEquals(20.0, lesson.getPrice());
        assertTrue(lesson.toString().contains("L001"));
        assertTrue(lesson.toString().contains("Yoga"));
        assertTrue(lesson.toString().contains("Saturday Morning"));
        assertTrue(lesson.toString().contains("$20.00"));
    }
}