package com.ventimetriquadriconsulting.event.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseEvent {

    private String expenseId;
    private String description;
    private double price;
    private double amount;
    private LocalDate dateInsert;
    private boolean isEmployeeExpense;

}
