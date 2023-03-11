package com.abdisadykov.tinkoffservice.dto;

import lombok.*;
import ru.tinkoff.piapi.contract.v1.Quotation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockPrice {

    String figi;
    String price;

}
