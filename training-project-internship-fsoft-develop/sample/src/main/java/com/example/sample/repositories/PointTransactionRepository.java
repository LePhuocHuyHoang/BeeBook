package com.example.sample.repositories;

import com.example.sample.entities.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction,Long> {

}
