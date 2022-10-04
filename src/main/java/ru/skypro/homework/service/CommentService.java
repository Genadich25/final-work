package ru.skypro.homework.service;

import ru.skypro.homework.dto.AdsComment;
import ru.skypro.homework.dto.ResponseWrapper;

public interface CommentService {

    public ResponseWrapper<AdsComment> getAdsComments(Integer idAds);

    public AdsComment addAdsComments(String idAds);

    public void deleteAdsComment(String idAds, Integer idComment);

    public AdsComment getAdsComment(String idAds, Integer idComment);

    public AdsComment updateAdsComment(String idAds, Integer idComment);
}
