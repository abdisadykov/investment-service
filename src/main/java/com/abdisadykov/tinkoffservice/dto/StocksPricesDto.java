package com.abdisadykov.tinkoffservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StocksPricesDto {
    private List<StockPrice> prices;
}
