package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.entities.Image;
import ru.skypro.homework.entities.SiteUser;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.CommentServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;

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

    @Autowired
    private ImageService imageService;

    @Autowired
    private AdsRepository adsRepository;


    @GetMapping
    public ResponseEntity<ResponseWrapper<AdsDto>> getAllAds() {
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
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<AdsDto> addAds(@RequestPart("properties") @Valid @NotNull @NotBlank CreateAdsDto createAdsDto,
                                         @RequestPart("image") @Valid @NotNull @NotBlank MultipartFile image) throws IOException {
        if (createAdsDto.getTitle() == null || createAdsDto.getDescription() == null || createAdsDto.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AdsDto result = adsService.addAds(createAdsDto, email);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            Integer id = result.getPk();
            imageService.uploadImage(image, email, id);
            Optional<Ads> adsOptional = adsRepository.findById(id);
            if (adsOptional.isPresent()) {
                result.setImage(adsOptional.get().getImage());
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
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
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/me")
    public ResponseEntity getAdsMe(
//            @RequestParam boolean authenticated,
//            @RequestParam String authorities,
//            @RequestParam Role credentials,
            @RequestParam(required = false) Integer details,
            @RequestParam(required = false) String principal) {
        ResponseWrapper<AdsDto> adsMe = adsService.getAdsMe(details, principal);
        if (adsMe == null) {
            return ResponseEntity.status(404)
                    .body("Not Found");
        }
        return ResponseEntity.ok(adsMe);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<ResponseWrapper<CommentDto>> getAdsComments(@PathVariable String ad_pk) {
        return ResponseEntity.ok(commentService.getListCommentDto(ad_pk));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<CommentDto> addAdsComment(@PathVariable String ad_pk, @RequestBody CommentDto commentDto) {
        CommentDto result = commentService.addCommentDto(ad_pk, commentDto);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<String> deleteAdsComment(@PathVariable String ad_pk,
                                                   @PathVariable Integer id) {
        String result = commentService.deleteCommentDto(ad_pk, id);
        if (result.equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> getAdsComment(@PathVariable String ad_pk,
                                                    @PathVariable Integer id) {
        CommentDto result = commentService.getCommentDto(ad_pk, id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> updateAdsComment(@PathVariable String ad_pk,
                                                       @PathVariable Integer id,
                                                       @RequestBody CommentDto commentDto) {
        CommentDto result = commentService.updateCommentDto(ad_pk, id, commentDto);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (result.getText().equals("Bad")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (result.getText().equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
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
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> removeAds(@Parameter(example = "1") @PathVariable Integer id) {
        String result = adsService.removeAds(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (result.equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Получение объявления по id",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FullAds.class)
                    )
            )
    })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FullAds> getAds(@Parameter(example = "1") @PathVariable Integer id) {
        FullAds result = adsService.getAds(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (result.getTitle().equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
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
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable Integer id,
                                            @RequestBody AdsDto ads) {
        AdsDto result = adsService.updateAds(id, ads);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (result.getTitle().equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping(value = "/images/{image}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable Integer image) {
        Image imageById = imageService.getImageById(image);
        if (imageById == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageById.getMediaType()));
        headers.setContentLength(imageById.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(imageById.getData());
    }


    @PatchMapping(value = "/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE})
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestPart("image") @Valid @NotNull @NotBlank MultipartFile image) throws IOException {

        Image image1 = imageService.updateImage(id, image);

        if (image1 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image1.getMediaType()));
        headers.setContentLength(image1.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(image1.getData());
    }


    @GetMapping("/adsTitleContains")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AdsDto>> getAdsWithTitleContainsText(@RequestParam ("text") String text) {
        ResponseWrapper<AdsDto> result = adsService.getAdsWithTitleContainsText(text);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping("/commentContains")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<CommentDto>> getAdsCommentsWithText(@RequestParam ("text") String text) {
        ResponseWrapper<CommentDto> result = commentService.getCommentWithText(text);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }


}
