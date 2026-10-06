package com.pwioi.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pwioi.app.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}