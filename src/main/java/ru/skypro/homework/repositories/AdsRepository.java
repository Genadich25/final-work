package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entities.Ads;

import java.util.List;

public interface AdsRepository extends JpaRepository<Ads, Integer> {
    Ads findAdsById(Integer id);
    List<Ads> findByAuthorAndPriceAndTitle(Integer author, Integer price, String title);

    List<Ads> findAdsByTitleContains(String text);
}
