package com.example.springBatchTutorial.core.domain.accounts;

import com.example.springBatchTutorial.core.domain.orders.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountsRepository extends JpaRepository<Accounts, Integer> {

}
