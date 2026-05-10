package com.commerce.inventory_service.config;

import com.commerce.inventory_service.models.entity.Inventory;
import com.commerce.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() == 0) {
            Inventory item1 = new Inventory();
            item1.setProductId(101L);
            item1.setStockQuantity(50);

            Inventory item2 = new Inventory();
            item2.setProductId(55L);
            item2.setStockQuantity(100);

            inventoryRepository.save(item1);
            inventoryRepository.save(item2);

            System.out.println("Dummy inventory loaded!");
        }
    }
}
