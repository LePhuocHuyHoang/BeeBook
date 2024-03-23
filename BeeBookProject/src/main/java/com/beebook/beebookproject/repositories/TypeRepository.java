package com.beebook.beebookproject.repositories;

import com.beebook.beebookproject.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TypeRepository extends JpaRepository<Type,Long>{
    @Query("SELECT COUNT(t) > 0 FROM Type t WHERE t.id = :typeId")
    boolean existsById(@Param("typeId") Long typeId);
    boolean existsByName(String name);
    @Modifying
    @Transactional
    @Query(value = "EXEC deleteType @typeId = :typeId", nativeQuery = true)
    void deleteType(@Param("typeId")Long typeId);
    @Query(value = "SELECT t FROM Type t " +
            "WHERE LOWER(t.name) LIKE %:keyword%")
    List<Type> searchType(String keyword);
    @Query(value = "EXEC getTop3BestTypes", nativeQuery = true)
    List<Type> getTop3BestTypes();
}
