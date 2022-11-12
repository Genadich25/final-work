package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ru.skypro.homework.entities.Image;
import ru.skypro.homework.exceptionsHandler.exceptions.BadInputDataException;
import ru.skypro.homework.exceptionsHandler.exceptions.NotAccessActionException;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.CommentServiceImpl;

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
    private ImageService imageService;


    @Operation(summary = "Getting list of all ads existed in data base",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All ads are got successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There aren't ads in data base"
                    )
            })
    @GetMapping
    public ResponseEntity<ResponseWrapper<AdsDto>> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }


    @Operation(summary = "Creating new ad",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New ad is created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Check your request"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access to create ad"
                    )
            })
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<AdsDto> addAds(@RequestPart("properties") @Valid @NotNull @NotBlank CreateAdsDto createAdsDto,
                                         @RequestPart("image") @Valid @NotNull @NotBlank MultipartFile image) throws IOException {
        if (createAdsDto.getTitle() == null || createAdsDto.getDescription() == null || createAdsDto.getPrice() == null) {
            throw new BadInputDataException();
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        AdsDto result = adsService.addAds(createAdsDto, email);
        if (result == null) {
            throw new NotAccessActionException();
        } else {
            Integer id = result.getPk();
            String imageS = imageService.uploadImage(image, email, id);
            result.setImage(imageS);
            return ResponseEntity.ok(result);
        }
    }


    @Operation(summary = "Getting all ads of one authorized user by his username,price in ads (if it's indicated) and part of ad's title (if it's indicated)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List ads of user is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class))),
                    @ApiResponse(
                            responseCode = "403",
                            description = "User hasn't access to getting ads"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/me")
    public ResponseEntity<ResponseWrapper<AdsDto>> getAdsMe(
            @RequestParam(required = false) Integer details,
            @RequestParam(required = false) String principal) {
        ResponseWrapper<AdsDto> adsMe = adsService.getAdsMe(details, principal);
        return ResponseEntity.ok(adsMe);
    }


    @Operation(summary = "Getting all comments of ad with fixed id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of comments is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Wrong id of ads"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad with this id hasn't comments"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<ResponseWrapper<CommentDto>> getAdsComments(@PathVariable String ad_pk) {
        return ResponseEntity.ok(commentService.getListCommentDto(ad_pk));
    }


    @Operation(summary = "Creating new comment for ad with fixed id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New comment is created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Check your request"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(value = "/{ad_pk}/comment")
    public ResponseEntity<CommentDto> addAdsComment(@PathVariable String ad_pk, @RequestBody CommentDto commentDto) {
        if (ad_pk == null || commentDto.getText() == null) {
            throw new BadInputDataException();
        }
        CommentDto result = commentService.addCommentDto(ad_pk, commentDto.getText());
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Deleting comment by id of ad with fixed id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment is deleted successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Wrong id of ad or wrong id of comment"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access for deleting this comment"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<String> deleteAdsComment(@PathVariable String ad_pk,
                                                   @PathVariable Integer id) {
        String result = commentService.deleteCommentDto(ad_pk, id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Getting one comment by id of ad with fixed id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Wrong id of ads"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> getAdsComment(@PathVariable String ad_pk,
                                                    @PathVariable Integer id) {
        CommentDto result = commentService.getCommentDto(ad_pk, id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Updating comment by id of ad with fixed id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment is updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Wrong id of ad or wrong id of comment"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access for updating this comment"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{ad_pk}/comment/{id}")
    public ResponseEntity<CommentDto> updateAdsComment(@PathVariable String ad_pk,
                                                       @PathVariable Integer id,
                                                       @RequestBody CommentDto commentDto) {
        if (commentDto.getText() == null) {
            throw new BadInputDataException();
        }
        CommentDto result = commentService.updateCommentDto(ad_pk, id, commentDto);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Deleting ads by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ad is deleted successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access for deleting this ad"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> removeAds(@Parameter(example = "1") @PathVariable Integer id) {
        String result = adsService.removeAds(id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Getting full info about ads by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Full info is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FullAds.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access for getting info"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FullAds> getAds(@Parameter(example = "1") @PathVariable Integer id) {
        FullAds result = adsService.getAds(id);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Updating ads by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ad is updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AdsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access for updating this ad"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ad with this id doesn't exist"
                    )
            })
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<AdsDto> updateAds(@PathVariable Integer id,
                                            @RequestBody AdsDto ads) {
        AdsDto result = adsService.updateAds(id, ads);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Getting image of one ad",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image is found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image isn't found"
                    )
            })
    @GetMapping(value = "/images/{image}", produces = {MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable Integer image) {
        Image imageById = imageService.getImageById(image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageById.getMediaType()));
        headers.setContentLength(imageById.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(imageById.getData());
    }


    @Operation(summary = "Request to change image of one ad",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Change is completed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = byte[].class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You haven't access to this action"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image with this id doesn't exist"
                    )
            })
    @PatchMapping(value = "/{id}/image", produces = {MediaType.IMAGE_PNG_VALUE})
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestPart("image") @Valid @NotNull @NotBlank MultipartFile image) throws IOException {

        Image image1 = imageService.updateImage(id, image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image1.getMediaType()));
        headers.setContentLength(image1.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(image1.getData());
    }


    @Operation(summary = "Getting ads with title containing indicated text",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ads are found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Ads with this title don't exist"
                    )
            })
    @GetMapping("/adsTitleContains")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AdsDto>> getAdsWithTitleContainsText(@RequestParam("text") String text) {
        ResponseWrapper<AdsDto> result = adsService.getAdsWithTitleContainsText(text);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Getting comments containing indicated text",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments are found successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ResponseWrapper.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comments with this text don't exist"
                    )
            })
    @GetMapping("/commentContains")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<CommentDto>> getAdsCommentsWithText(@RequestParam("text") String text) {
        ResponseWrapper<CommentDto> result = commentService.getCommentWithText(text);
        return ResponseEntity.ok(result);
    }


}
