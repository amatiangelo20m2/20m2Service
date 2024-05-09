package com.ventimetriquadriconsulting.event.entity.dto;

import com.ventimetriquadriconsulting.event.entity.ExpenseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseEventDTO implements Serializable {

    private String expenseId;
    private String description;
    private double price;
    private double amount;
    private LocalDate dateInsert;

    public static ExpenseEventDTO fromEntity(ExpenseEvent expenseEvent) {
        return ExpenseEventDTO.builder()
                .expenseId(expenseEvent.getExpenseId())
                .description(expenseEvent.getDescription())
                .price(expenseEvent.getPrice())
                .amount(expenseEvent.getAmount())
                .dateInsert(expenseEvent.getDateInsert())
                .build();
    }

    // Method to convert from DTO to Entity
    public ExpenseEvent toEntity() {
        return ExpenseEvent.builder()
                .expenseId(this.getExpenseId())
                .description(this.getDescription())
                .price(this.getPrice())
                .amount(this.getAmount())
                .dateInsert(this.getDateInsert())
                .build();
    }

    public static Set<ExpenseEvent> fromDTOList(List<ExpenseEventDTO> expenseEventDTOS) {
        return expenseEventDTOS.stream()
                .map(ExpenseEventDTO::toEntity)
                .collect(Collectors.toSet());
    }

    public static List<ExpenseEventDTO> toDTOList(List<ExpenseEvent> expenseEventList) {
        return expenseEventList.stream()
                .map(ExpenseEventDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
