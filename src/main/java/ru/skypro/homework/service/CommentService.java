package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsComment;
import ru.skypro.homework.dto.ResponseWrapper;

public interface CommentService {


    ResponseWrapper<AdsComment> getAdsComments(Integer idAds);

    AdsComment addAdsComments(String idAds);

    void deleteAdsComment(String idAds, Integer idComment);

    AdsComment getAdsComment(String idAds, Integer idComment);

    public AdsComment updateAdsComment(String idAds, Integer idComment);
}
