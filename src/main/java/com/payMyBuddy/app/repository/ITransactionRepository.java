package com.payMyBuddy.app.repository;

import com.payMyBuddy.app.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {
}
