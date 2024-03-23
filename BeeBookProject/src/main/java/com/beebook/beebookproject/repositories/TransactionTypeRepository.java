package com.beebook.beebookproject.repositories;

import com.beebook.beebookproject.entities.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType,Long>{
    
}
