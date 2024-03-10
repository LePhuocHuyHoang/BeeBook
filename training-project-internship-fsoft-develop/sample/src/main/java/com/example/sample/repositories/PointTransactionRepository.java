package com.example.sample.repositories;

import com.example.sample.entities.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction,Long> {
    @Modifying
    @Query(value = "select * from dbo.[user] where user_id = :userId", nativeQuery = true)
    List<Object[]> checkExistingUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "exec getAllPointTransaction :userId, :offset, :fetch", nativeQuery = true)
    List<Object[]> getAllPointTransaction(
            @Param("userId") Long userId,
            @Param("offset") Long offset,
            @Param("fetch") Long fetch
    );
}
