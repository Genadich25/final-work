package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.CommentServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/ads")
public class AdsController {

    @Autowired
    private AdsServiceImpl adsService;
    @Autowired
    private CommentServiceImpl commentService;
    @Autowired
    private UserServiceImpl userService;
    private UserDetails userDetails;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список всех объявлений",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper> getAllAds(){
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сохранение в бд объявлени",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CreateAdsDto> addAds(@RequestBody CreateAdsDto createAdsDto){

        User user = userService.findUser(userDetails.getUsername());
        return ResponseEntity.ok(adsService.addAds(createAdsDto, user.getId()));
    }
    
    @GetMapping(value = "/me")
    public ResponseEntity<?> getAdsMe(@RequestParam boolean authenticated,
                                      @RequestParam String authorities,
                                      @RequestParam Object credentials){
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
                                              @PathVariable Integer id){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> getAdsComment(@PathVariable String ad_pk,
                                           @PathVariable Integer id){
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> updateAdsComment(@PathVariable String ad_pk,
                                              @PathVariable Integer id){
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeAds(@PathVariable Integer id){
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAds(@PathVariable Integer id){
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateAds(@PathVariable Integer id){
        return ResponseEntity.ok().build();
    }
}
