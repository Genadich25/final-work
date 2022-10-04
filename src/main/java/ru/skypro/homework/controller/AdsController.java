package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.CommentServiceImpl;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/ads")
public class AdsController {

    private final AdsServiceImpl adsService;
    private final CommentServiceImpl commentService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Ads>> getAllAds(){
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @PostMapping
    public ResponseEntity<CreateAds> addAds(@RequestBody CreateAds createAds){
        return ResponseEntity.ok(adsService.addAds(createAds));
    }

    @GetMapping(value = "/me")
    public ResponseEntity<ResponseWrapper<Ads>> getAdsMe(@RequestParam(required = false) boolean authenticated,
                                                         @RequestParam(required = false) String authority,
                                                         @RequestParam(required = false) Object credentials,
                                                         @RequestParam(required = false) Object details,
                                                         @RequestParam(required = false) Object principal){
        return ResponseEntity.ok(adsService.getAdsMe(authenticated, authority, credentials, details, principal));
    }

    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<ResponseWrapper<AdsComment>> getAdsComments(@PathVariable String ad_pk){
        return ResponseEntity.ok(commentService.getAdsComments(Integer.valueOf(ad_pk)));
    }

    @PostMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<AdsComment> addAdsComments(@PathVariable String ad_pk){
        return ResponseEntity.ok(commentService.addAdsComments(ad_pk));
    }
    
    @DeleteMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity deleteAdsComment(@PathVariable String ad_pk,
                                              @PathVariable Integer id){
        commentService.deleteAdsComment(ad_pk, id);
        return ResponseEntity.ok().build();

    }

    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsComment> getAdsComment(@PathVariable String ad_pk,
                                                    @PathVariable Integer id){
        return ResponseEntity.ok(commentService.getAdsComment(ad_pk, id));
    }

    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<AdsComment> updateAdsComment(@PathVariable String ad_pk,
                                                       @PathVariable Integer id){
        return ResponseEntity.ok(commentService.updateAdsComment(ad_pk, id));
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity removeAds(@PathVariable Integer id){
        adsService.removeAds(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<FullAds> getAds(@PathVariable Integer id){
        return ResponseEntity.ok(adsService.getAds(id));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Ads> updateAds(@PathVariable Integer id){
        return ResponseEntity.ok(adsService.updateAds(id));
    }
}
