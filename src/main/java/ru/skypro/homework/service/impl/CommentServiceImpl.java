package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.service.CommentService;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public List<Comment> getAdsComments(Integer idAds) {
        return null;
    }

    @Override
    public Comment addAdsComments(Comment comment) {
        return null;
    }

    @Override
    public Comment deleteAdsComment(Integer idComment) {
        return null;
    }

    @Override
    public Comment getAdsComment(Integer idComment) {
        return null;
    }

    @Override
    public Comment updateAdsComment(Integer idComment) {
        return null;
    }
}
