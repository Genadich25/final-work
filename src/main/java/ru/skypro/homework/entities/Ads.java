package ru.skypro.homework.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Модель объявления
 */
@Entity
@Data
public class Ads {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "author")
    private Integer author;

    @Column(name = "image")
    private String image;

    @Column(name = "price")
    private Integer price;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private SiteUserDetails siteUserDetails;

    @OneToMany(mappedBy = "ads", cascade = CascadeType.ALL)
    private List<Comment> commentList;

    @OneToMany(mappedBy = "ads", cascade = CascadeType.ALL)
    private List<Image> images;

    public Comment addComment(Comment comment){
        commentList.add(comment);
        return comment;
    }

    public void removeComment(Comment comment){
        commentList.remove(comment);
    }

    public Comment updateComment(Comment comment){
        if(commentList.contains(comment)){
            commentList.set(commentList.indexOf(comment),comment);
            return comment;
        }
        return new Comment();
    }
}
