package resources.Models;

public class Review {
    private int rating; // 1 to 5
    private String text;

    public Review(int rating, String text) {
        this.rating = rating;
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }
}
