package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.dto.AdsComment;

public interface AdsCommentRepository extends JpaRepository<AdsComment, Long> {
}
