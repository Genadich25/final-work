package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/ads")
public class AdsController {
    
    @GetMapping
    public ResponseEntity<?> getAllAds(){
        return ResponseEntity.ok().build();
    }
    
    @PostMapping
    public ResponseEntity<?> addAds(){
        return ResponseEntity.ok().build();
    }
    
    @GetMapping(value = "/me")
    public ResponseEntity<?> getAdsMe(){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<?> getAdsComments(@PathVariable String ad_pk){
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<?> addAdsComments(@PathVariable String ad_pk){
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> deleteAdsComment(@PathVariable String ad_pk, 
                                              @PathVariable Long id){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> getAdsComment(@PathVariable String ad_pk,
                                           @PathVariable Long id){
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> updateAdsComment(@PathVariable String ad_pk,
                                              @PathVariable Long id){
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeAds(@PathVariable Long id){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAds(@PathVariable Long id){
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateAds(@PathVariable Long id){
        return ResponseEntity.ok().build();
    }
}
