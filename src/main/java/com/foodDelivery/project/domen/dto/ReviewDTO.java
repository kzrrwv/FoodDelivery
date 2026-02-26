package com.foodDelivery.project.domen.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ReviewDTO {

    @Min(1)
    @Max(5)
    private int rating;

    @Size(max = 500)
    private String comment;

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
