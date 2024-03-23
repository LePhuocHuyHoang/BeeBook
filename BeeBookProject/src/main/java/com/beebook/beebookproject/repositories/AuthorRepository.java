package com.beebook.beebookproject.repositories;

import com.beebook.beebookproject.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Modifying
    @Transactional
    @Query(value = "EXEC deleteAuthor @authorId = :authorId", nativeQuery = true)
    void deleteAuthor(@Param("authorId")Long authorId);
    @Query("SELECT COUNT(a) > 0 FROM Author a WHERE a.id = :authorId")
    boolean existsById(@Param("authorId") Long authorId);
    boolean existsByName(String name);
    @Query(value = "SELECT a FROM Author a " +
            "WHERE LOWER(a.name) LIKE %:keyword%")
    List<Author> searchAuthor(String keyword);
    @Query(value = "EXEC getTop3BestAuthors", nativeQuery = true)
    List<Author> getTop3BestAuthors();
    @Query(value = "EXEC filterAuthor @birthYear= :birthYear, @typeName= :typeName", nativeQuery = true)
    List<Author> filterAuthor(@Param("birthYear") Long birthYear, @Param("typeName") String typeName);

}
