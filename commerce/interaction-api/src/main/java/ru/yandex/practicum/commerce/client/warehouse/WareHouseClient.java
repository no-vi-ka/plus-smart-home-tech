package ru.yandex.practicum.commerce.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperation;

@FeignClient(name = "warehouse",
        path = "/api/v1/warehouse",
        configuration = FeignConfig.class)
public interface WareHouseClient extends WarehouseOperation {
}
