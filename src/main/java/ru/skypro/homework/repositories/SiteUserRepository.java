package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.SiteUser;

import java.util.Optional;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, String> {

    Optional<SiteUser> findSiteUserByUsername(String email);

}
