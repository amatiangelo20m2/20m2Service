package com.ventimetriconsulting.storage.entity.dto;


import com.ventimetriconsulting.storage.entity.extra.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInventoryRequest {

    private String user;
    private long storageId;
    List<TransactionItem> transactionItemList;
    private OperationType operationType;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionItem {
        private long productId;
        private long amount;
    }
}
