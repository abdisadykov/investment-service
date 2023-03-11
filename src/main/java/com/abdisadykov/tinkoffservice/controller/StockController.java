package com.abdisadykov.tinkoffservice.controller;

import com.abdisadykov.tinkoffservice.dto.FigiesDto;
import com.abdisadykov.tinkoffservice.dto.StocksDto;
import com.abdisadykov.tinkoffservice.dto.StocksPricesDto;
import com.abdisadykov.tinkoffservice.dto.TickersDto;
import com.abdisadykov.tinkoffservice.model.Stock;
import com.abdisadykov.tinkoffservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StockController {

    private final StockService stockService;

    @GetMapping("/stocks/{ticker}")
    public List<Stock> getStock(@PathVariable String ticker) throws ExecutionException, InterruptedException {
        return stockService.getStockByTicker(ticker);
    }

    @PostMapping("/stocks/getStocksByTickers")
    public StocksDto getSeveralStocksByTickers(@RequestBody TickersDto tickers) {
        return stockService.getSeveralStocksByTickers(tickers);
    }

    @GetMapping("/stocks")
    public List<Stock> getAllTradableStocks() throws ExecutionException, InterruptedException {
        return stockService.getAllTradableStocks();
    }

    @PostMapping("/prices")
    public StocksPricesDto  getPrices(@RequestBody FigiesDto figiesDto) {
        return stockService.getPrices(figiesDto);
    }

}
