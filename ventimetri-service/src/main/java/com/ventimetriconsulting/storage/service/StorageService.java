package com.ventimetriconsulting.storage.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.InventarioNotFoundException;
import com.ventimetriconsulting.branch.exception.customexceptions.StorageNotFoundException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.notification.entity.NotificationEntity;
import com.ventimetriconsulting.notification.entity.RedirectPage;
import com.ventimetriconsulting.notification.service.MessageSender;
import com.ventimetriconsulting.storage.entity.Inventario;
import com.ventimetriconsulting.storage.entity.Storage;
import com.ventimetriconsulting.storage.entity.dto.InventarioDTO;
import com.ventimetriconsulting.storage.entity.dto.StorageDTO;
import com.ventimetriconsulting.storage.entity.dto.TransactionInventoryRequest;
import com.ventimetriconsulting.storage.entity.extra.InventoryAction;
import com.ventimetriconsulting.storage.entity.extra.OperationType;
import com.ventimetriconsulting.storage.repository.InventarioRepository;
import com.ventimetriconsulting.storage.repository.StorageRepository;
import com.ventimetriconsulting.supplier.entity.Product;
import com.ventimetriconsulting.supplier.entity.Supplier;
import com.ventimetriconsulting.supplier.repository.ProductRepository;
import com.ventimetriconsulting.supplier.repository.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;

    private final InventarioRepository inventarioRepository;
    private final ProductRepository productRepository;
    private final MessageSender messageSender;
    private final BranchUserRepository branchUserRepository;

    @Transactional
    public StorageDTO createStorage(StorageDTO storageDTO,
                                    String branchCode) {

        log.info("Crete storage {}. Associate it with branch with code {}", storageDTO, branchCode);

        if(storageDTO.getInventarioDTOS() == null){
            storageDTO.setInventarioDTOS(new HashSet<>());
        }

        Storage storage = StorageDTO.toEntity(storageDTO);

        Branch branch = branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with code: " + branchCode + ". Cannot associate the storage" ));

        Storage savedStorage = storageRepository.save(storage);

        branch.getStorages().add(savedStorage);

        return StorageDTO.fromEntity(savedStorage);
    }

    @Transactional
    public InventarioDTO insertProductToStorage(long productId,
                                                long storageId,
                                                String userName,
                                                double amount) {


        Optional<Inventario> existingInventory =
                inventarioRepository.findByProduct_ProductIdAndStorage_StorageId(productId, storageId);

        if (existingInventory.isPresent()) {
            log.info("Inventario is already present for the product selected. I will update the existing row: {}", existingInventory);
            // Update existing inventory
            Inventario inventario = existingInventory.get();
            double newStock = inventario.getStock() + amount;
            inventario.setStock(newStock);

            // Add new inventory action
            InventoryAction newAction = InventoryAction.builder()
                    .insertionDate(LocalDate.now())
                    .modifiedByUser(userName)
                    .amount(amount)
                    .operationType(OperationType.INSERTION)
                    .build();
            inventario.getInventoryActions().add(newAction);

            return InventarioDTO.fromEntity(inventarioRepository.save(inventario));

        } else {
            Storage storage = storageRepository.findById(storageId)
                    .orElseThrow(() -> new BranchNotFoundException("Storage not found with id: " + storageId + ". Cannot put the product"));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BranchNotFoundException("Product not found with id: " + productId + ". Cannot update the storage"));

            log.info("Adding product id {} to the storage with id {} - User ({})", product, storageId, userName);

            for(Inventario inventario : storage.getInventario()){

                if(inventario.getStorage().getStorageId() == storageId
                        && inventario.getProduct().getProductId() == productId){

                }
            }

            Inventario inventario = Inventario
                    .builder()
                    .inventarioId(0)
                    .product(product)
                    .storage(storage)
                    .insertionDate(LocalDate.now())
                    .stock(amount)
                    .deletionDate(null)
                    .inventoryActions(new HashSet<>(Collections.singletonList(InventoryAction.builder()
                            .insertionDate(LocalDate.now())
                            .modifiedByUser(userName)
                            .amount(amount)
                            .operationType(OperationType.CREATION)
                            .build())))
                    .build();

            Inventario inventarioSaved = inventarioRepository.save(inventario);
            storage.getInventario().add(inventarioSaved);

            return InventarioDTO.fromEntity(inventarioSaved);
        }
    }

    @Transactional
    public List<InventarioDTO> insertSupplierProductsintoStorage(
            long supplierId,
            long storageId,
            String userName) {

        List<InventarioDTO> inventarioDTOS = new ArrayList<>();

        log.info("Adding products of the supplier with id {} to the storage with id {} - User ({})", supplierId, storageId, userName);
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new BranchNotFoundException("Storage not found with id: " + storageId + ". Cannot put the product"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new BranchNotFoundException("Supplier not found with id: " + supplierId + ". Cannot retrieve products to insert into storage"));;

        for(Product product : supplier.getProducts()){
            boolean exists = inventarioRepository.existsByProductAndStorage(product, storage);
            if(!exists){
                Inventario inventario = Inventario
                        .builder()
                        .inventarioId(0)
                        .product(product)
                        .storage(storage)
                        .stock(0L)
                        .insertionDate(LocalDate.now())
                        .deletionDate(null)
                        .inventoryActions(new HashSet<>(Collections.singletonList(InventoryAction.builder()
                                .insertionDate(LocalDate.now())
                                .modifiedByUser(userName)
                                .amount(0)
                                .operationType(OperationType.CREATION)
                                .build())))
                        .build();


                Inventario inventarioSaved = inventarioRepository.save(inventario);
                storage.getInventario().add(inventarioSaved);
                inventarioDTOS.add(InventarioDTO.fromEntity(inventarioSaved));
            }
        }
        return inventarioDTOS;
    }

    @Transactional
    @Modifying
    public InventarioDTO putDataIntoInventario(long inventarioId,
                                               long insertedAmount,
                                               long removedAmount,
                                               String userName) {

        log.info("Insert data into inventario with id {}. Amount to add {}, " +
                "amount to remove {}, " +
                "user {}", inventarioId, insertedAmount, removedAmount, userName);


        Inventario inventario = inventarioRepository.findById(inventarioId).orElseThrow(()
                -> new InventarioNotFoundException("Inventario item not found with id: " + inventarioId + ". Cannot retrieve inventario to update storage data"));;;


        inventario.getInventoryActions().add(InventoryAction.builder()
                .amount(removedAmount)
                .modifiedByUser(userName)
                .insertionDate(LocalDate.now())
                .operationType(OperationType.CREATION)
                .build());


        inventarioRepository.save(inventario);
        return InventarioDTO.fromEntity(inventario);

    }

    @Transactional
    public void deleteProductFromStorage(long inventarioId) {

        log.info("Delete inventario by id {}", inventarioId);
        inventarioRepository.deleteById(inventarioId);

    }

    @Transactional
    public StorageDTO insertDataIntoInventario(TransactionInventoryRequest transactionInventoryRequest) {

        Storage storage = storageRepository.findById(transactionInventoryRequest.getStorageId())
                .orElseThrow(() -> new StorageNotFoundException("Storage not found with id: " + transactionInventoryRequest.getStorageId() + ". Cannot update inventario"));

        log.info("Updating inventario with request: " + transactionInventoryRequest);
        Set<Inventario> inventarioSet = storage.getInventario();

        Map<Long, Inventario> inventarioMap = inventarioSet.stream()
                .collect(Collectors.toMap(inventarios -> inventarios.getProduct().getProductId(), Function.identity()));

        for (TransactionInventoryRequest.TransactionItem transactionItem : transactionInventoryRequest.getTransactionItemList()) {
            Inventario inventario = inventarioMap.get(transactionItem.getProductId());


            if (inventario != null) {
                if(transactionInventoryRequest.getOperationType() == OperationType.INSERTION){
                    inventario.setStock(inventario.getStock() + transactionItem.getAmount());
                }else{
                    inventario.setStock(inventario.getStock() - transactionItem.getAmount());
                }

                inventario.getInventoryActions().add(InventoryAction
                        .builder()
                        .operationType(transactionInventoryRequest.getOperationType())
                        .amount(transactionItem.getAmount())
                        .modifiedByUser(transactionInventoryRequest.getUser())
                        .insertionDate(LocalDate.now())
                        .build());
            }
        }
        return StorageDTO.fromEntity(storage);
    }

    @Transactional
    @Modifying
    public StorageDTO updateStockValueInventario(long storageId,
                                                 Map<Long, Double> stockValues,
                                                 String userName,
                                                 String branchCode) {

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException("Storage not found with id: " + storageId + ". Cannot update inventario"));

        log.info("Updating inventario. News values are (inventario id - stock value) {} ", stockValues);

        StringBuilder messageBuilder = new StringBuilder();

        for(Inventario inventario : storage.getInventario()){
            if(stockValues.containsKey(inventario.getInventarioId())){

                messageBuilder.append("\n - ")
                        .append(inventario.getProduct().getName())
                        .append(" da ")
                        .append(inventario.getStock())
                        .append(" a ")
                        .append(stockValues
                                .get(inventario.getInventarioId())).append(" ").append(inventario.getProduct().getUnitMeasure().name());
                inventario.setStock(stockValues.get(inventario.getInventarioId()));




            }
        }

        List<String> fmcTokensByBranchCodeAndRole
                = branchUserRepository.findFMCTokensByBranchCodeAndRole(branchCode, Role.AMMINISTRATORE);

        String branchName = branchRepository.findBranchNameByBranchCode(branchCode)
                .orElseThrow(() -> new BranchNotFoundException("Branch name not found with code: " + branchCode + "."));;


        messageSender.enqueMessage(
                NotificationEntity.builder()
                        .title("\uD83D\uDDD2" + userName + " ha modificato magazzino " + storage.getName() + " per branch " + branchName)
                        .message(messageBuilder.toString())
                        .redirectPage(RedirectPage.DASHBOARD)
                        .fmcToken(fmcTokensByBranchCodeAndRole)
                        .build());

        return StorageDTO.fromEntity(storage);
    }


    @Transactional
    @Modifying
    public void removeProductAmountFromStorage(Map<Long, Double> removeProdMap,
                                               long storageId,
                                               String userName) {

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException("Storage not found with id: " + storageId + ". Cannot update inventario"));

        log.info("Updating inventario. Remove products from inventario with id {}. Map of prodIt-amountToRemove {} - by user {}",
                storageId,
                removeProdMap,
                userName);

        storage.getInventario().forEach(inventario -> {
            if(removeProdMap.containsKey(inventario.getProduct().getProductId())){
                inventario.setStock(inventario.getStock() - removeProdMap.get(inventario.getProduct().getProductId()));
            }
        });
    }
}
