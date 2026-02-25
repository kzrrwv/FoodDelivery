package com.foodDelivery.project.domen.responce;

import java.time.LocalDateTime;

public class ReviewToRetrieve {

    private int rating;

    private String comment;

    private LocalDateTime createdAt;

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
