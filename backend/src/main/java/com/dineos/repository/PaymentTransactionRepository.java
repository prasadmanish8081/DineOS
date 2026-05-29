package com.dineos.repository;

import com.dineos.entity.PaymentTransaction;
import com.dineos.enums.PaymentGatewayStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findFirstByOrder_IdOrderByCreatedAtDesc(Long orderId);

    Optional<PaymentTransaction> findByRazorpayOrderId(String razorpayOrderId);

    Optional<PaymentTransaction> findByRazorpayPaymentId(String razorpayPaymentId);

    List<PaymentTransaction> findByOrder_IdAndGatewayStatus(Long orderId, PaymentGatewayStatus gatewayStatus);
}
