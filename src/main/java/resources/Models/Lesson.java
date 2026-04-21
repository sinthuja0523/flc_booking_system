package resources.models;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String id;
    private String exerciseType;
    private int month; // e.g., 5 for May
    private int weekend; // e.g., 1 to 4
    private String day; // "Saturday" or "Sunday"
    private String time; // "Morning", "Afternoon", "Evening"
    private double price;
    private int capacity;
    private List<Member> attendees;
    private List<Review> reviews;

    public Lesson(String id, String exerciseType, int month, int weekend, String day, String time, double price) {
        this.id = id;
        this.exerciseType = exerciseType;
        this.month = month;
        this.weekend = weekend;
        this.day = day;
        this.time = time;
        this.price = price;
        this.capacity = 4;
        this.attendees = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public int getMonth() {
        return month;
    }

    public int getWeekend() {
        return weekend;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    // Status
    public int getCurrentAttendeesCount() {
        return attendees.size();
    }

    public boolean hasSpace() {
        return attendees.size() < capacity;
    }

    // Operations
    public boolean addAttendee(Member member) {
        if (hasSpace() && !attendees.contains(member)) {
            attendees.add(member);
            return true;
        }
        return false;
    }

    public boolean removeAttendee(Member member) {
        return attendees.remove(member);
    }

    public void addReview(Review review) {
        if (review != null) {
            reviews.add(review);
        }
    }

    public double getAverageRating() {
        if (reviews.isEmpty())
            return 0.0;
        double sum = 0;
        for (Review r : reviews)
            sum += r.getRating();
        return sum / reviews.size();
    }

    public List<Member> getAttendees() {
        return new ArrayList<>(attendees);
    }

    @Override
    public String toString() {
        return id + ": " + exerciseType + " (" + day + " " + time + ") - $" + String.format("%.2f", price) + " ["
                + attendees.size() + "/" + capacity + "]";
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
