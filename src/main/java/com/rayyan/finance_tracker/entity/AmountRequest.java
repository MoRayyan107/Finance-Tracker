package com.rayyan.finance_tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request object for amount-based operations (deposit, withdraw)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountRequest {
    private BigDecimal amount;
}

