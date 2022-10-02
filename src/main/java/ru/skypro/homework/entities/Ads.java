package ru.skypro.homework.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Модель объявления
 */
@Entity
@Data
public class Ads {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "author")
    private Integer author;

    @Column(name = "image")
    private String image;

    @Column(name = "pk")
    private Integer pk;

    @Column(name = "price")
    private Integer price;

    @Column(name = "title")
    private String title;
}
