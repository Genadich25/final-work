package ru.skypro.homework.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
public class AdsComment {

    @Id
    @GeneratedValue
    private Long id;

    private Integer author;
    private LocalDateTime createdAt;
    private Integer pk;
    private String text;
}
