package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.SiteUserDetails;

import java.util.List;

/**
 * Repository for interaction with database and table stored data about site's users (name, lastname, phone)
 */
@Repository
public interface UserDetailsRepository extends JpaRepository<SiteUserDetails, Integer> {

    /**
     * Method for getting users by username
     * @return List<SiteUserDetails></SiteUserDetails>
     */
    List<SiteUserDetails> findSiteUserDetailsBySiteUserUsername(String email);

}
