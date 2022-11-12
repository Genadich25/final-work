package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.Comment;

import java.util.List;

/**
 * Repository for interaction with database and table stored data about comments
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * method for getting list comments by id their ad
     *
     * @return List<AdsComment></AdsComment>
     */
    List<Comment> findCommentsByAds_Id(Integer id);

    /**
     * method for getting list comments by part of comment
     *
     * @return List<AdsComment></AdsComment>
     */
    List<Comment> findCommentsByTextContains(String text);
}
