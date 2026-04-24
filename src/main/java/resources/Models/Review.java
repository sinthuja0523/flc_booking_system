package resources.models;

public class Review {
    private int rating; 
    private String text;

    public Review(int rating, String text) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public String getText() {
        return text;
    }



public String getComment() {
    return text;
}
}
