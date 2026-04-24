package resources.controllers;

import java.util.*;

import resources.models.*;

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
        initializeSampleBookingsAndReviews();
    }

    private void initializeData() {
        addMember(new Member("M001", "Alice"));
        addMember(new Member("M002", "Bob"));
        addMember(new Member("M003", "Charlie"));
        addMember(new Member("M004", "Diana"));
        addMember(new Member("M005", "Eve"));
        addMember(new Member("M006", "Frank"));
        addMember(new Member("M007", "Grace"));
        addMember(new Member("M008", "Henry"));
        addMember(new Member("M009", "Ivy"));
        addMember(new Member("M010", "Jack"));

        String[] types = { "Yoga", "Zumba", "Aquacise", "BoxFit" };
        double[] prices = { 15.0, 12.0, 18.0, 10.0 };
        String[] days = { "Saturday", "Sunday" };
        String[] times = { "Morning", "Afternoon", "Evening" };
        String[] months = { "January", "February" };

        int lessonIdCounter = 1;

        for (int month = 0; month < months.length; month++) {
            for (int weekend = 1; weekend <= 4; weekend++) {
                for (String day : days) {
                    for (int t = 0; t < times.length; t++) {
                        String time = times[t];
                        int index = (day.equals("Saturday") ? 0 : 3) + t;
                        String type = types[index % 4];
                        double price = prices[index % 4];

                        String id = "L" + String.format("%03d", lessonIdCounter++);
                        Lesson lesson = new Lesson(id, type, month + 1, weekend, day, time, price);
                        lessons.add(lesson);
                    }
                }
            }
        }
    }

    public String viewLessonReviews(String lessonId) {
        Lesson lesson = getLessonById(lessonId);

        if (lesson == null) {
            return "Error: Lesson not found.";
        }

        List<Review> reviews = lesson.getReviews();

        if (reviews.isEmpty()) {
            return "No reviews available for this lesson.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Reviews for Lesson ").append(lessonId).append(" (")
                .append(lesson.getExerciseType()).append(")\n");
        sb.append("--------------------------------------------------\n");

        int count = 1;
        for (Review review : reviews) {
            sb.append("Review ").append(count++).append(":\n");
            sb.append("Rating: ").append(review.getRating()).append("\n");
            sb.append("Comment: ").append(review.getComment()).append("\n");
            sb.append("--------------------------------------------------\n");
        }

        return sb.toString();
    }

    private void createAttendedSampleBooking(String memberId, String lessonId, int rating, String reviewText) {
        Member member = getMember(memberId);
        Lesson lesson = getLessonById(lessonId);

        if (member == null || lesson == null) {
            return;
        }

        String result = bookLesson(member, lesson);

        if (result.startsWith("Success")) {
            String bookingId = result.replace("Success: ", "").trim();
            attendLesson(bookingId, rating, reviewText);
        }
    }

    private boolean hasTimeConflict(Member member, Lesson newLesson, String ignoreBookingId) {
        for (Booking booking : bookings.values()) {
            if (ignoreBookingId != null && booking.getBookingId().equals(ignoreBookingId)) {
                continue;
            }

            boolean activeBooking = booking.getStatus() == BookingStatus.BOOKED
                    || booking.getStatus() == BookingStatus.CHANGED
                    || booking.getStatus() == BookingStatus.ATTENDED;

            if (!activeBooking) {
                continue;
            }

            if (!booking.getMember().equals(member)) {
                continue;
            }

            Lesson existingLesson = booking.getLesson();

            boolean sameSlot = existingLesson.getMonth() == newLesson.getMonth()
                    && existingLesson.getWeekend() == newLesson.getWeekend()
                    && existingLesson.getDay().equalsIgnoreCase(newLesson.getDay())
                    && existingLesson.getTime().equalsIgnoreCase(newLesson.getTime());

            if (sameSlot) {
                return true;
            }
        }

        return false;
    }

    private void initializeSampleBookingsAndReviews() {
        createAttendedSampleBooking("M001", "L001", 5, "Excellent class");
        createAttendedSampleBooking("M002", "L001", 4, "Very good session");
        createAttendedSampleBooking("M003", "L002", 5, "Enjoyed the workout");
        createAttendedSampleBooking("M004", "L002", 3, "It was okay");
        createAttendedSampleBooking("M005", "L003", 4, "Good instructor");

        createAttendedSampleBooking("M006", "L004", 5, "Great energy");
        createAttendedSampleBooking("M007", "L004", 4, "Nice class");
        createAttendedSampleBooking("M008", "L005", 5, "Very useful");
        createAttendedSampleBooking("M009", "L005", 3, "Average class");
        createAttendedSampleBooking("M010", "L006", 4, "Good experience");

        createAttendedSampleBooking("M001", "L007", 5, "Loved it");
        createAttendedSampleBooking("M002", "L007", 4, "Good training");
        createAttendedSampleBooking("M003", "L008", 5, "Excellent workout");
        createAttendedSampleBooking("M004", "L008", 4, "Very satisfying");
        createAttendedSampleBooking("M005", "L009", 3, "Okay session");

        createAttendedSampleBooking("M006", "L010", 5, "Best class");
        createAttendedSampleBooking("M007", "L010", 4, "Helpful lesson");
        createAttendedSampleBooking("M008", "L011", 5, "Amazing class");
        createAttendedSampleBooking("M009", "L011", 4, "Good exercise");
        createAttendedSampleBooking("M010", "L012", 5, "Very satisfied");
    }

    public List<Lesson> getAvailableLessonsForChange(String bookingId) {
        Booking booking = bookings.get(bookingId);
        List<Lesson> result = new ArrayList<>();

        if (booking == null) {
            return result;
        }

        for (Lesson l : lessons) {
            if (!l.getId().equals(booking.getLesson().getId())) {
                result.add(l);
            }
        }

        return result;
    }

    public Lesson findLesson(int month, int week, String day, String time, String exerciseType) {
        for (Lesson l : lessons) {
            if (l.getMonth() == month &&
                    l.getWeekend() == week &&
                    l.getDay().equalsIgnoreCase(day) &&
                    l.getTime().equalsIgnoreCase(time) &&
                    l.getExerciseType().equalsIgnoreCase(exerciseType)) {
                return l;
            }
        }
        return null;
    }

    public Lesson getLessonById(String id) {
        for (Lesson l : lessons) {
            if (l.getId().equals(id)) {
                return l;
            }
        }
        return null;
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
                    (b.getStatus() == BookingStatus.BOOKED ||
                            b.getStatus() == BookingStatus.CHANGED ||
                            b.getStatus() == BookingStatus.ATTENDED)) {
                return "Error: Duplicate booking detected.";
            }
        }

        if (hasTimeConflict(member, lesson, null)) {
            return "Error: Time conflict detected. Member already has another lesson at the same time.";
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
        if (hasTimeConflict(booking.getMember(), newLesson, bookingId)) {
            return "Error: Time conflict detected.";
        }

        for (Booking b : bookings.values()) {
            if (!b.getBookingId().equals(bookingId) && b.getMember().equals(booking.getMember())
                    && b.getLesson().equals(newLesson) &&
                    (b.getStatus() == BookingStatus.BOOKED ||
                            b.getStatus() == BookingStatus.CHANGED ||
                            b.getStatus() == BookingStatus.ATTENDED)) {
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
        if (rating < 1 || rating > 5) {
            return "Error: Rating must be between 1 and 5.";
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

    public List<String> getAllExerciseTypes() {
        List<String> types = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (!types.contains(lesson.getExerciseType())) {
                types.add(lesson.getExerciseType());
            }
        }

        return types;
    }

    public double getPriceByExerciseType(String exerciseType) {
        for (Lesson lesson : lessons) {
            if (lesson.getExerciseType().equalsIgnoreCase(exerciseType)) {
                return lesson.getPrice();
            }
        }
        return 0.0;
    }

    public String updatePriceByExerciseType(String exerciseType, double newPrice) {
        if (newPrice <= 0) {
            return "Error: Price must be greater than 0.";
        }

        boolean updated = false;

        for (Lesson lesson : lessons) {
            if (lesson.getExerciseType().equalsIgnoreCase(exerciseType)) {
                lesson.setPrice(newPrice);
                updated = true;
            }
        }

        if (updated) {
            return "Success: Price updated for " + exerciseType;
        }

        return "Error: Exercise type not found.";
    }
}
