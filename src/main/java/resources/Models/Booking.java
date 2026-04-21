package resources.models;

public class Booking {
    private String bookingId;
    private Member member;
    private Lesson lesson;
    private BookingStatus status;

    public Booking(String bookingId, Member member, Lesson lesson) {
        this.bookingId = bookingId;
        this.member = member;
        this.lesson = lesson;
        this.status = BookingStatus.BOOKED;
    }

    public String getBookingId() { return bookingId; }
    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }
    public BookingStatus getStatus() { return status; }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Booking " + bookingId + ": " + member.getName() + " -> " + lesson.getExerciseType() + " [" + status + "]";
    }
}
