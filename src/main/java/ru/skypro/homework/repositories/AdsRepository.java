package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.Ads;

import java.util.List;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Integer> {

    Ads findAdsById(Integer id);

    List<Ads> findAdsBySiteUserDetailsId(Integer siteUserId);

    List<Ads> findAllBySiteUserDetailsIdAndPrice(Integer id, Integer price);


    List<Ads> findAllBySiteUserDetailsIdAndTitleContains(Integer id, String text);


    List<Ads> findAllBySiteUserDetailsIdAndPriceAndTitleContains(Integer id, Integer price, String title);

    List<Ads> findAdsByTitleContains(String text);
}
