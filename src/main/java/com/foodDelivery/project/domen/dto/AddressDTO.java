package com.foodDelivery.project.domen.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressDTO {

        @NotBlank
        @Size(min = 2, max = 100)
        private String city;

        @NotBlank
        @Size(min = 2, max = 150)
        private String street;

        @NotBlank
        @Size(max = 20)
        private String building;

        @Min(value = 1)
        @Max(value = 1000)
        private int apartment;

        @Min(value = 1)
        @Max(value = 100)
        private int entrance;

        @Min(value = 1)
        @Max(value = 200)
        private int floor;
}
