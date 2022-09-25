package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;

import java.util.List;

public interface CommentService {

    public List<Comment> getAdsComments(Integer idAds);

    public Comment addAdsComments(Comment comment);

    public Comment deleteAdsComment(Integer idComment);

    public Comment getAdsComment(Integer idComment);

    public Comment updateAdsComment(Integer idComment);
}
