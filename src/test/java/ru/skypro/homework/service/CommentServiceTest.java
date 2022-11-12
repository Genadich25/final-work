package ru.skypro.homework.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.*;
import ru.skypro.homework.exceptionsHandler.exceptions.*;
import ru.skypro.homework.mappers.impl.CommentMapperImpl;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.repositories.AuthorityRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.repositories.SiteUserRepository;
import ru.skypro.homework.service.impl.CommentServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AdsRepository adsRepository;
    @Mock
    private SiteUserRepository siteUserRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Spy
    public CommentMapperImpl commentMapper;
    @Mock
    private Authentication auth;

    public SiteUser siteUser1;
    public SiteUserDetails siteUserDetails1;

    public Authority authorityRoleUser;

    public Ads ads1;
    public Optional<Ads> optionalAds1;
    public Optional<Ads> optionalAdsEmpty;

    public Comment commentAds;
    public Comment comment1;
    public Comment comment2;

    public Optional<Comment> optionalCommentAds;
    public Optional<Comment> optionalComment1;
    public Optional<Comment> optionalCommentEmpty;
    public List<Comment> commentList;

    public CommentDto commentDto1;
    public CommentDto commentDto2;
    public List<CommentDto> commentDtoList;

    ResponseWrapper<CommentDto> commentDtoResponseWrapper1;


    @BeforeEach
    public void init(){
        LocalDateTime fixeDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        SecurityContextHolder.getContext().setAuthentication(auth);

        siteUserDetails1 = new SiteUserDetails();
        siteUserDetails1.setId(1);

        siteUser1 = new SiteUser();
        siteUser1.setSiteUserDetails(siteUserDetails1);
        siteUser1.setPassword("1");
        siteUser1.setUsername("username");
        siteUser1.setEnabled(true);

        siteUserDetails1.setSiteUser(siteUser1);

        authorityRoleUser = new Authority();
        authorityRoleUser.setAuthority("ROLE_USER");

        ads1 = new Ads();

        commentAds = new Comment();
        commentAds.setAds(ads1);
        commentAds.setSiteUserDetails(siteUserDetails1);
        commentAds.setText("username");
        commentAds.setCreatedAt(fixeDateTime);
        commentAds.setIdAuthor(1);

        optionalCommentAds = Optional.of(commentAds);

        comment1 = new Comment();
        comment1.setCreatedAt(fixeDateTime);
        comment1.setText("text");
        comment1.setId(1);
        comment1.setIdAuthor(1);
        comment1.setSiteUserDetails(siteUserDetails1);

        optionalComment1 = Optional.of(comment1);

        comment2 = new Comment();
        comment2.setCreatedAt(fixeDateTime);
        comment2.setText("text2");
        comment2.setId(2);
        comment2.setIdAuthor(1);

        optionalCommentEmpty = Optional.empty();

        commentList = List.of(comment1, comment2);

        commentDto1 = new CommentDto();
        commentDto1.setPk(1);
        commentDto1.setText("text");
        commentDto1.setCreatedAt(fixeDateTime);
        commentDto1.setAuthor(1);

        commentDto2 = new CommentDto();
        commentDto2.setPk(2);
        commentDto2.setText("text2");
        commentDto2.setCreatedAt(fixeDateTime);
        commentDto2.setAuthor(1);

        commentDtoList = List.of(commentDto1, commentDto2);

        ads1.setSiteUserDetails(siteUserDetails1);
        ads1.setCommentList(commentList);
        optionalAds1 = Optional.of(ads1);
        optionalAdsEmpty = Optional.empty();

        commentDtoResponseWrapper1 = new ResponseWrapper<>();
        commentDtoResponseWrapper1.setCount(2);
        commentDtoResponseWrapper1.setResults(commentDtoList);

    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getListCommentDto() {
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentMapper.commentToCommentDto(comment1)).thenReturn(commentDto1);
        Mockito.when(commentMapper.commentToCommentDto(comment2)).thenReturn(commentDto2);

        Assertions.assertEquals(commentDtoResponseWrapper1, commentService.getListCommentDto("1"));
    }

    @Test
    public void getListCommentDtoExceptions(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAdsEmpty);

        Assertions.assertThrows(IncorrectAdsIdException.class, () -> commentService.getListCommentDto("1"));

        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);

        Assertions.assertThrows(EmptyListException.class, () -> commentService.getListCommentDto("1"));
    }

    @Test
    public void addCommentDto(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(siteUserRepository.findByUsername("username")).thenReturn(siteUser1);
        Mockito.when(commentRepository.save(commentAds)).thenReturn(commentAds);
        Mockito.when(commentMapper.commentToCommentDto(commentAds)).thenReturn(commentDto1);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");

        Assertions.assertEquals(commentDto1, commentService.addCommentDto("1", "username"));
    }

    @Test
    public void addCommentDtoException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAdsEmpty);

        Assertions.assertThrows(AdsNotFoundException.class,() -> commentService.addCommentDto("1", "username"));
    }

    @Test
    public void deleteCommentDto(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertEquals("SUCCESS", commentService.deleteCommentDto("1", 1));
    }

    @Test
    public void deleteCommentDtoIncorrectAdsIdException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAdsEmpty);

        Assertions.assertThrows(IncorrectAdsIdException.class, () -> commentService.deleteCommentDto("1", 1));
    }

    @Test
    public void deleteCommentDtoCommentNotFoundException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentEmpty);

        Assertions.assertThrows(CommentNotFoundException.class, () -> commentService.deleteCommentDto("1", 1));
    }

    @Test
    public void deleteCommentDtoCommentNotBelongAdException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentAds);

        Assertions.assertThrows(CommentNotBelongAdException.class, () -> commentService.deleteCommentDto("1", 1));

        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(Collections.emptyList());
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentAds);

        Assertions.assertThrows(CommentNotBelongAdException.class, () -> commentService.deleteCommentDto("1", 1));
    }

    @Test
    public void deleteCommentDtoNotAccessActionException(){
        siteUser1.setUsername("username1");
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertThrows(NotAccessActionException.class, () -> commentService.deleteCommentDto("1", 1));
    }

    @Test
    public void getCommentDto(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertEquals(commentDto1,commentService.getCommentDto("1", 1));
    }

    @Test
    public void getCommentDtoIncorrectAdsIdException(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAdsEmpty);

        Assertions.assertThrows(IncorrectAdsIdException.class, () -> commentService.getCommentDto("1", 1));
    }

    @Test
    public void getCommentDtoCommentNotFoundException(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentEmpty);

        Assertions.assertThrows(CommentNotFoundException.class, () -> commentService.getCommentDto("1", 1));
    }

    @Test
    public void getCommentDtoCommentNotBelongAdException(){
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(Collections.emptyList());
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertThrows(CommentNotBelongAdException.class, () -> commentService.getCommentDto("1", 1));

        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentAds);

        Assertions.assertThrows(CommentNotBelongAdException.class, () -> commentService.getCommentDto("1", 1));
    }

    @Test
    public void updateCommentDto(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);
        Mockito.when(commentRepository.save(optionalComment1.get())).thenReturn(optionalComment1.get());

        Assertions.assertEquals(commentDto1, commentService.updateCommentDto("1", 1, commentDto1));
    }

    @Test
    public void updateCommentDtoIncorrectAdsIdException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAdsEmpty);

        Assertions.assertThrows(IncorrectAdsIdException.class,() -> commentService.updateCommentDto("1", 1, commentDto1));
    }

    @Test
    public void updateCommentDtoCommentNotFoundException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentEmpty);

        Assertions.assertThrows(CommentNotFoundException.class,() -> commentService.updateCommentDto("1", 1, commentDto1));
    }

    @Test
    public void updateCommentDtoCommentNotBelongAdException(){
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(Collections.emptyList());
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertThrows(CommentNotBelongAdException.class,() -> commentService.updateCommentDto("1", 1, commentDto1));

        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalCommentAds);

        Assertions.assertThrows(CommentNotBelongAdException.class,() -> commentService.updateCommentDto("1", 1, commentDto1));
    }

    @Test
    public void updateCommentDtoNotAccessActionException() {
        siteUser1.setUsername("username1");
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("username");
        Mockito.when(authorityRepository.findAuthorityByUsername("username")).thenReturn(authorityRoleUser);
        Mockito.when(adsRepository.findById(1)).thenReturn(optionalAds1);
        Mockito.when(commentRepository.findCommentsByAds_Id(1)).thenReturn(commentList);
        Mockito.when(commentRepository.findById(1)).thenReturn(optionalComment1);

        Assertions.assertThrows(NotAccessActionException.class, () -> commentService.updateCommentDto("1", 1, commentDto1));
    }

    @Test
    public void  getCommentWithText(){
        Mockito.when(commentRepository.findCommentsByTextContains("1")).thenReturn(commentList);

        Assertions.assertEquals(commentDtoResponseWrapper1, commentService.getCommentWithText("1"));
    }

    @Test
    public void  getCommentWithTextEmptyListException(){
        Mockito.when(commentRepository.findCommentsByTextContains("1")).thenReturn(Collections.emptyList());

        Assertions.assertThrows(EmptyListException.class,() -> commentService.getCommentWithText("1"));
    }
}
