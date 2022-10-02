package ru.skypro.homework.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class AdsDto {

    private Integer author;
    private String image;
    private Integer pk;
    private Integer price;
    private String title;
}