package resources.Controllers;

import resources.Models.*;
import java.util.*;

public class BookingController {
    private Map<String, Member> members;
    private List<Lesson> lessons;
    private Map<String, Booking> bookings;
    private int nextBookingId = 1;

    public BookingController() {
        this.members = new HashMap<>();
        this.lessons = new ArrayList<>();
        this.bookings = new LinkedHashMap<>();
        initializeData();
    }

    private void initializeData() {
        addMember(new Member("M001", "Alice"));
        addMember(new Member("M002", "Bob"));
        addMember(new Member("M003", "Charlie"));
        addMember(new Member("M004", "Diana"));
        addMember(new Member("M005", "Eve"));

        String[] types = { "Yoga", "Zumba", "Aquacise", "BoxFit" };
        double[] prices = { 15.0, 12.0, 18.0, 10.0 };
        String[] days = { "Saturday", "Sunday" };
        String[] times = { "Morning", "Afternoon", "Evening" };

        int lessonIdCounter = 1;

        for (int month = 1; month <= 2; month++) {
            for (int weekend = 1; weekend <= 4; weekend++) {
                for (String day : days) {
                    for (int t = 0; t < times.length; t++) {
                        String time = times[t];
                        int index = (day.equals("Saturday") ? 0 : 3) + t;
                        String type = types[index % 4];
                        double price = prices[index % 4];

                        String id = "L" + String.format("%03d", lessonIdCounter++);
                        Lesson lesson = new Lesson(id, type, month, weekend, day, time, price);
                        lessons.add(lesson);
                    }
                }
            }
        }
    }

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public Member getMember(String id) {
        return members.get(id);
    }

    public List<Lesson> getLessonsByDay(String day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) {
            if (l.getDay().equalsIgnoreCase(day)) {
                result.add(l);
            }
        }
        return result;
    }

    public List<Lesson> getLessonsByType(String type) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : lessons) {
            if (l.getExerciseType().equalsIgnoreCase(type)) {
                result.add(l);
            }
        }
        return result;
    }

    public Lesson getLesson(String id) {
        for (Lesson l : lessons) {
            if (l.getId().equals(id))
                return l;
        }
        return null;
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public String bookLesson(Member member, Lesson lesson) {
        for (Booking b : bookings.values()) {
            if (b.getMember().equals(member) && b.getLesson().equals(lesson) &&
                    (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.CHANGED)) {
                return "Error: Duplicate booking detected.";
            }
        }

        if (lesson.hasSpace()) {
            lesson.addAttendee(member);
            String bookingId = "B" + String.format("%04d", nextBookingId++);
            Booking booking = new Booking(bookingId, member, lesson);
            bookings.put(bookingId, booking);
            return "Success: " + bookingId;
        } else {
            return "Error: Lesson is full.";
        }
    }

    public String changeBooking(String bookingId, Lesson newLesson) {
        Booking booking = bookings.get(bookingId);
        if (booking == null)
            return "Error: Booking not found.";
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return "Error: Cannot change a cancelled or attended booking.";
        }

        for (Booking b : bookings.values()) {
            if (!b.getBookingId().equals(bookingId) && b.getMember().equals(booking.getMember())
                    && b.getLesson().equals(newLesson) &&
                    (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.CHANGED)) {
                return "Error: Duplicate booking detected in the new lesson.";
            }
        }

        if (newLesson.hasSpace()) {
            booking.getLesson().removeAttendee(booking.getMember());
            newLesson.addAttendee(booking.getMember());
            booking.setLesson(newLesson);
            booking.setStatus(BookingStatus.CHANGED);
            return "Success: Booking changed to " + newLesson.getExerciseType();
        } else {
            return "Error: New lesson is full.";
        }
    }

    public String cancelBooking(String bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null)
            return "Error: Booking not found.";
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return "Error: Cannot cancel a cancelled or attended booking.";
        }

        booking.getLesson().removeAttendee(booking.getMember());
        booking.setStatus(BookingStatus.CANCELLED);
        return "Success: Booking " + bookingId + " cancelled.";
    }

    public String attendLesson(String bookingId, int rating, String reviewText) {
        Booking booking = bookings.get(bookingId);
        if (booking == null)
            return "Error: Booking not found.";
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.ATTENDED) {
            return "Error: Booking is cancelled or already attended.";
        }

        booking.setStatus(BookingStatus.ATTENDED);
        Review review = new Review(rating, reviewText);
        booking.getLesson().addReview(review);
        return "Success: Lesson attended. Review recorded.";
    }

    public String generateMonthlyLessonReport(int month) {
        StringBuilder sb = new StringBuilder();
        sb.append("Monthly Lesson Report - Month ").append(month).append("\n");
        sb.append(String.format("%-10s %-15s %-10s %-10s %-10s %-10s\n", "LessonID", "Exercise", "Day", "Attendees",
                "AvgRating", "Price"));
        sb.append("----------------------------------------------------------------------\n");

        int totalAttendees = 0;
        for (Lesson l : lessons) {
            if (l.getMonth() == month) {
                int attendedCount = 0;
                for (Booking b : bookings.values()) {
                    if (b.getLesson().equals(l) && b.getStatus() == BookingStatus.ATTENDED) {
                        attendedCount++;
                    }
                }

                sb.append(String.format("%-10s %-15s %-10s %-10d %-10.1f $%.2f\n",
                        l.getId(), l.getExerciseType(), l.getDay(), attendedCount, l.getAverageRating(), l.getPrice()));
                totalAttendees += attendedCount;
            }
        }
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Total Attendees this month: ").append(totalAttendees).append("\n");
        return sb.toString();
    }

    public String generateMonthlyChampionReport(int month) {
        Map<String, Double> incomeByType = new HashMap<>();
        for (Lesson l : lessons) {
            if (l.getMonth() == month) {
                int attendedCount = 0;
                for (Booking b : bookings.values()) {
                    if (b.getLesson().equals(l) && b.getStatus() == BookingStatus.ATTENDED) {
                        attendedCount++;
                    }
                }
                double income = attendedCount * l.getPrice();
                incomeByType.put(l.getExerciseType(), incomeByType.getOrDefault(l.getExerciseType(), 0.0) + income);
            }
        }

        if (incomeByType.isEmpty())
            return "No data for month " + month;

        List<Map.Entry<String, Double>> list = new ArrayList<>(incomeByType.entrySet());
        list.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("Monthly Champion Exercise Report - Month ").append(month).append("\n");
        sb.append("--------------------------------------------------\n");
        for (Map.Entry<String, Double> entry : list) {
            sb.append(String.format("%-20s Income: $%.2f\n", entry.getKey(), entry.getValue()));
        }
        sb.append("--------------------------------------------------\n");
        sb.append("CHAMPION: ").append(list.get(0).getKey()).append(" ($")
                .append(String.format("%.2f", list.get(0).getValue())).append(")\n");

        return sb.toString();
    }

    public Collection<Member> getAllMembers() {
        return members.values();
    }

    public List<Lesson> getAllLessons() {
        return new ArrayList<>(lessons);
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }
}
