package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.SiteUser;
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

    @GetMapping
    public ResponseEntity<ResponseWrapper> getAllAds(){
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сохранение объявления в бд",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<CreateAdsDto> addAds(@RequestBody CreateAdsDto createAdsDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SiteUser user = userService.findUserByName(authentication.getName());
        return ResponseEntity.ok(adsService.addAds(createAdsDto, user.getSiteUserDetails().getId()));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список объявлений пользователя",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapper.class)
                    )
            )
    })
    @GetMapping(value = "/me")
    public ResponseEntity getAdsMe(@RequestParam boolean authenticated,
                                   @RequestParam String authorities,
                                   @RequestParam Role credentials,
                                   @RequestParam Integer details,
                                   @RequestParam String principal){
        if (!authenticated) {
            return ResponseEntity.status(401)
                    .body("Unauthorized");
        } else if (credentials != Role.ADMIN || credentials != Role.USER) {
            return ResponseEntity.status(403)
                    .body("forbidden");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SiteUser user = userService.findUserByName(authentication.getName());
        ResponseWrapper<AdsDto> adsMe = adsService.getAdsMe(details, principal, user);
        if (adsMe == null) {
            return ResponseEntity.status(404)
                    .body("Not Found");
        }
        return ResponseEntity.ok(adsMe);
    }

    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<ResponseWrapper<CommentDto>> getAdsComments(@PathVariable String ad_pk){
        return ResponseEntity.ok(commentService.getListCommentDto(ad_pk));
    }

    @PostMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<CommentDto> addAdsComment(@PathVariable String ad_pk, @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.addCommentDto(ad_pk, commentDto));
    }
    
    @DeleteMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<?> deleteAdsComment(@PathVariable String ad_pk,
                                              @PathVariable Integer id){
        commentService.deleteCommentDto(ad_pk, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> getAdsComment(@PathVariable String ad_pk,
                                                    @PathVariable Integer id){
        return ResponseEntity.ok(commentService.getCommentDto(ad_pk, id));
    }

    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> updateAdsComment(@PathVariable String ad_pk,
                                                       @PathVariable Integer id,
                                                       @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.updateCommentDto(ad_pk, id, commentDto));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Удаление объявления по id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> removeAds(@Parameter(example = "1") @PathVariable Integer id){
        return ResponseEntity.ok(adsService.removeAds(id));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение объявления по id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsAndUserDto.class)
                    )
            )
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<AdsAndUserDto> getAds(@Parameter(example = "1") @PathVariable Integer id){
        return ResponseEntity.ok(adsService.getAds(id));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Изменение объявления по id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class)
                    )
            )
    })
    @PatchMapping(value = "/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable Integer id,
                                       @RequestBody AdsDto ads){
        return ResponseEntity.ok(adsService.updateAds(id, ads));
    }
}
