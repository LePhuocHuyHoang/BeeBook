package com.example.sample.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @Modifying
    @Query(value = "select * from dbo.[user] where user_id = :userId", nativeQuery = true)
    List<Object[]> checkExistingUser(@Param("userId") Long userId);

    @Modifying
    @Query(value = "select COUNT(*) from rating where user_id =:userId and book_id =:bookId", nativeQuery = true)
    List<Integer> checkExistsRating(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Modifying
    @Transactional
    @Query(value= "exec insertRating :userId, :bookId, :rating", nativeQuery = true)
    void insertRating(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("rating") Long rating);

    @Modifying
    @Transactional
    @Query(value= "exec updateRating :userId, :bookId, :rating", nativeQuery = true)
    void updateRating(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("rating") Long rating);

    @Modifying
    @Transactional
    @Query(value= "exec insertComment :userId, :bookId, :comment", nativeQuery = true)
    void insertComment(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("comment") String comment);

    @Modifying
    @Transactional
    @Query(value= "exec getComment :bookId, :offset, :fetch", nativeQuery = true)
    List<Object[]> getComment(@Param("bookId") Long bookId, @Param("offset") Long offset, @Param("fetch") Long fetch);

    @Modifying
    @Transactional
    @Query(value= "exec reportBook :userId, :bookId, :reportContent", nativeQuery = true)
    void reportBook(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("reportContent") String reportContent);

    @Query(value="EXEC GetFeaturedBooks :top", nativeQuery = true)
    List<Map<String, Object>> getFeaturedBooks(@Param("top") int top);
    @Query(value="EXEC GETNEWBOOKS", nativeQuery = true)
    List<Map<String, Object>> getNewBooks();
}
