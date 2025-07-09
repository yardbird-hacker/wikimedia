package net.javaguides.springboot.web.repository;

import net.javaguides.springboot.web.model.WikimediaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WikimediaDataRepository extends JpaRepository<WikimediaData, Long> {
}
