package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.Ads;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdsRepository extends JpaRepository<Ads, Integer> {

    Ads findAdsById(Integer id);

    List<Ads> findByAuthorAndPriceAndTitle(Integer author, Integer price, String title);

    List<Ads> findAdsByTitleContains(String text);
}
