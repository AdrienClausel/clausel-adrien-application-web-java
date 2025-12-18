package com.payMyBuddy.app.repository;

import com.payMyBuddy.app.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {

    List<Transaction> findBySenderIdOrderByIdDesc(Long senderId);

}
