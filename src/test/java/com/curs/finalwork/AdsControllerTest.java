package com.curs.finalwork;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.controller.AdsController;
import ru.skypro.homework.controller.AuthController;
import ru.skypro.homework.controller.UserController;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapper;
import ru.skypro.homework.entities.Ads;
import ru.skypro.homework.mappers.AdsMapper;
import ru.skypro.homework.repositories.AdsRepository;
import ru.skypro.homework.service.impl.AdsServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(AdsController.class)
@DataJpaTest
public class AdsControllerTest {

    private Ads ads;
    private AdsMapper adsMapper;
    private MockMvc mockMvc;

    @MockBean
    private AuthController authController;

    @MockBean
    private UserController userController;

    @MockBean
    private AdsRepository adsRepository;

    @SpyBean
    private AdsServiceImpl adsService;

    @InjectMocks
    private AdsController adsController;

    @BeforeEach
    public void setUp() {
        
        Integer id = 1;
        Integer author = 1;
        String image = "image";
        Integer price = 120;
        String title = "круглое";
        
        ads.setId(id);
        ads.setAuthor(author);
        ads.setImage(image);
        ads.setPrice(price);
        ads.setTitle(title);
    }

    @Test
    public void getAllAdsTest() throws Exception {

        Integer id = 1;
        Integer author = 1;
        String image = "image";
        Integer price = 120;
        String title = "круглое";


        Ads ads = new Ads();
        ads.setId(id);
        ads.setAuthor(author);
        ads.setImage(image);
        ads.setPrice(price);
        ads.setTitle(title);

        adsRepository.save(ads);

        AdsDto adsDto = adsMapper.adsToAdsDto(ads);
        List<Ads> list = adsRepository.findAll();
        List<AdsDto> list1 = list.stream()
                .map(s -> adsMapper.adsToAdsDto(s))
                .collect(Collectors.toList());
        ResponseWrapper<AdsDto> responseWrapper = new ResponseWrapper<>();
        responseWrapper.setResults(list1);
        responseWrapper.setCount(list1.size());

        when(adsRepository.save(any(Ads.class))).thenReturn(ads);
        when(adsRepository.findAll()).thenReturn(list);
        when(adsService.getAllAds()).thenReturn(responseWrapper);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseWrapper").value(responseWrapper));
    }

    @Test
    public void updateAdsTest() throws Exception{

        Ads editAds = ads;
        AdsDto adsDto = adsMapper.adsToAdsDto(ads);


        when(adsRepository.save(any(Ads.class))).thenReturn(editAds);
        when(adsService.updateAds(any(Integer.class), any(AdsDto.class))).thenReturn(adsDto);


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adsDto").value(adsDto));
    }

    @Test
    public void removeAdsTest() throws Exception {

        Ads ads1 = ads;

        adsRepository.save(ads1);
        doNothing().when(adsRepository).deleteById(ads1.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/" + ads1.getId()))
                .andExpect(status().isOk());
    }
}
