package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.SiteUserDetails;

@Repository
public interface UserDetailsRepository extends JpaRepository<SiteUserDetails, Integer> {

}
