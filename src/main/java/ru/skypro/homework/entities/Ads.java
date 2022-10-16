package ru.skypro.homework.entities;

import lombok.Data;
import ru.skypro.homework.dto.CommentDto;

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
    @Column(name = "pk")
    private Integer pk;

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

    @OneToMany(mappedBy = "ads")
    private List<Comment> commentList;

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
