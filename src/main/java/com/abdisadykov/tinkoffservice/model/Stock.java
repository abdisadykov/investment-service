package com.abdisadykov.tinkoffservice.model;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    String ticker;
    String figi;
    String name;
    String type;
    String currency;
    String source;


}
