package com.abdisadykov.tinkoffservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockDto {
    String ticker;
    String figi;
    String name;
    String type;
    String source;
}
