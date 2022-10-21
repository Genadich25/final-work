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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
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
    public ResponseEntity<ResponseWrapper> getAllAds() {
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
            result.setImage(adsRepository.findAdsByPk(id).getImage());
            return ResponseEntity.ok(result);
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
    public ResponseEntity getAdsMe(@RequestParam boolean authenticated,
                                   @RequestParam String authorities,
                                   @RequestParam Role credentials,
                                   @RequestParam Integer details,
                                   @RequestParam String principal) {
        if (!authenticated) {
            return ResponseEntity.status(401)
                    .body("Unauthorized");
        } else if (credentials != Role.ADMIN || credentials != Role.USER) {
            return ResponseEntity.status(403)
                    .body("forbidden");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SiteUser user = userService.findUserByName(username);
        ResponseWrapper<AdsDto> adsMe = adsService.getAdsMe(details, principal, user);
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
        return ResponseEntity.ok(commentService.addCommentDto(ad_pk, commentDto));
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
        return ResponseEntity.ok(commentService.getCommentDto(ad_pk, id));
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
        if (result.getTitle().equals("Not access")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/images/{id}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {
        Image image = imageService.getImageById(id);
        if (image == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getMediaType()));
        headers.setContentLength(image.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(image.getData());
    }


}
