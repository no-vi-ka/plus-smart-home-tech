package ru.yandex.practicum.interaction.client.feign.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.warehouse.WarehouseApi;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClientFeign extends WarehouseApi {

}
