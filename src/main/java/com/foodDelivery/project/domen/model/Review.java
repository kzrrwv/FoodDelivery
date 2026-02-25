package com.foodDelivery.project.domen.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    private int id;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order_id;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user_id;

    public Review() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
