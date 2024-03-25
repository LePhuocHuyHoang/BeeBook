package com.beebook.beebookproject.repositories;

import com.beebook.beebookproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    @Modifying
    @Query(value = "EXEC deleteComment @commentId = :commentId", nativeQuery = true)
    void deleteComment(@Param("commentId") Long commentId);
    @Procedure(name = "CheckCommentExists", outputParameterName = "Exists")
    boolean existsById(@Param("commentId") Long commentId);
    @Query(value = "EXEC getAllComment @offset= :offset, @fetch= :fetch ", nativeQuery = true)
    List<Object[]> getAllComment(@Param("offset")Long offset, @Param("fetch")Long fetch);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :userId")
    boolean existsUserById(@Param("userId") Long userId);
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :userName")
    boolean existsUserByUserName(@Param("userName") String userName);
    @Modifying
    @Transactional
    @Query(value = "EXEC deleteUser @userId = :userId", nativeQuery = true)
    void deleteUser(@Param("userId")Long userId);

    @Modifying
    @Transactional
    @Query(value = "EXEC deleteUserByUserName :userName", nativeQuery = true)
    void deleteUserByUserName(@Param("userName")String userName);

    @Query("SELECT u FROM User u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE %:keyword%")
    List<User> searchUser(@Param("keyword") String keyword);
    @Query(value = "EXEC filterUser @gender= :gender, @DOB= :DOB, @minPoint= :minPoint, @maxPoint= :maxPoint ", nativeQuery = true)
    List<User> filterUser(@Param("gender") String gender, @Param("DOB") Long DOB, @Param("minPoint") BigDecimal minPoint, @Param("maxPoint") BigDecimal maxPoint);
    @Query(value = "EXEC getTop3BestUsers", nativeQuery = true)
    List<User> getTop3BestUsers();
    @Modifying
    @Transactional
    @Query(value = "exec getRentedBook :userId, :month, :year, :offset, :fetch", nativeQuery = true)
    List<Object[]> getRentedBook(
            @Param("userId") Long userId,
            @Param("month") Long month,
            @Param("year") Long year,
            @Param("offset") Long offset,
            @Param("fetch") Long fetch
    );


    User findUserById(Long userId);

    User findByUsername(String userName);
    User findByEmail(String email);

    User findByUsernameAndIdNot(String brandName, Long brandId);

    User deleteUserByUsername(String username);
    @Transactional
    @Query(value = "EXEC getUserRating :userId, :bookId", nativeQuery = true)
    int getUserRating(@Param("userId")Long userId, @Param("bookId")Long bookId);

    @Query(value = "exec getBookmark :userId", nativeQuery = true)
    List<Map<String, Object>> getBookmark(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "select username, first_name, last_name, DOB, point, gender, email from [user] where username = :userName", nativeQuery = true)
    List<Object[]> getProfile(@Param("userName") String userName);

}
