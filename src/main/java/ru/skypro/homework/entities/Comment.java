package ru.skypro.homework.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Class contains entity of comment
 */
@Entity
@Data
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "idAuthor")
    private Integer idAuthor;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "text")
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ads")
    private Ads ads;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private SiteUserDetails siteUserDetails;

}
