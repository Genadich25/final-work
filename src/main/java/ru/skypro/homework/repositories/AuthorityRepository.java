package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.Authority;

/**
 * Repository for interaction with database and table stored data about user's authorities
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    /**
     * search authority by user's email
     *
     * @return Authority
     */
    Authority findAuthorityByUsername(String username);

}
