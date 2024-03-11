package com.example.sample.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
@Entity
@Table(name = "point_transaction")
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private Long idTransaction;

    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "points_added")
    private Long pointsAdded;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "transaction_type")
    private TransactionType transactionType;



    // Constructors, Getters, and Setters
}