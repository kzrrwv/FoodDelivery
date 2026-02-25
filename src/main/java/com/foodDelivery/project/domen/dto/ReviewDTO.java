package com.foodDelivery.project.domen.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ReviewDTO {

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
