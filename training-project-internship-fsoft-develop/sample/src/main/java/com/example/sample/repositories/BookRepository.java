package com.example.sample.repositories;

import java.math.BigDecimal;
import java.util.List;
import com.example.sample.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long>{
    @Modifying
    @Transactional
    @Query(value = "EXEC deleteBook @bookId = :bookId", nativeQuery = true)
    void deleteBook(@Param("bookId")Long bookId);
    @Query("SELECT COUNT(b) > 0 FROM Book b WHERE b.id = :bookId")
    boolean existsById(@Param("bookId") Long bookId);
    @Query(value = "SELECT b FROM Book b " +
                    "WHERE LOWER(b.name) LIKE %:keyword%")
    List<Book> searchBook(String keyword);
    @Query(value = "EXEC getTop3BestSellingBooks", nativeQuery = true)
    List<Book> getTop3BookSelling();
    @Query(value = "EXEC filterBook @typeName= :typeName, @authorName= :authorName, @minPrice= :minPrice, @maxPrice= :maxPrice ", nativeQuery = true)
    List<Book> filterBook(@Param("typeName") String typeName, @Param("authorName") String authorName, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    boolean existsByName(String name);
}
