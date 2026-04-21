package resources.models;

import org.junit.jupiter.api.Test;

import resources.models.Review;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void shouldReturnRatingAndText() {
        Review review = new Review(5, "Excellent class");

        assertEquals(5, review.getRating());
        assertEquals("Excellent class", review.getText());
    }
}