package ru.skypro.homework.service.impl;

import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdsComment;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public ResponseWrapper<AdsComment> getAdsComments(Integer idAds) {
        return null;
    }

    @Override
    public AdsComment addAdsComments(String idAds) {
        return null;
    }

    @Override
    public void deleteAdsComment(String idAds, Integer idComment) {

    }

    @Override
    public AdsComment getAdsComment(String idAds, Integer idComment) {
        return null;
    }

    @Override
    public AdsComment updateAdsComment(String idAds, Integer idComment) {
        return null;
    }
}
