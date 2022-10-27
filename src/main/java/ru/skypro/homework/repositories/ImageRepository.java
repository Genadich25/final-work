package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.Image;

/**
 * Repository for interaction with database and table stored data about images
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

}
