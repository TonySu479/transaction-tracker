package com.example.transactiontracker.services.transactionService;

import com.example.transactiontracker.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction save(Transaction transaction);
    void deleteAll();
    void deleteById(long id);
    Optional<Transaction> findById(long id);
    List<Transaction> findByNameContaining(String name);
    List<Transaction> findAll();
    Transaction getTransaction(Transaction transaction, Optional<Transaction> transactionData);
}
