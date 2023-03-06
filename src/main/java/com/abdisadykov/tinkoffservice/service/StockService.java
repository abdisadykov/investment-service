package com.abdisadykov.tinkoffservice.service;

import com.abdisadykov.tinkoffservice.dto.StocksDto;
import com.abdisadykov.tinkoffservice.dto.TickersDto;
import com.abdisadykov.tinkoffservice.model.Stock;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface StockService {

    List<Stock>  getStockByTicker(String ticker) throws ExecutionException, InterruptedException;
    List<Stock> getAllTradableStocks() throws ExecutionException, InterruptedException;
    StocksDto getSeveralStocksByTickers(TickersDto tickers);
}
