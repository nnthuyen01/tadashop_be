package com.tadashop.nnt.service;

import java.util.List;

import com.tadashop.nnt.model.Payment;

public interface PaymentService {
    Payment findById(Long id);
    List<Payment> findAll();
    Payment save(Payment payment);
    Payment updatePayment(Payment payment);
    Boolean deletePayment(Long paymentId);
}
