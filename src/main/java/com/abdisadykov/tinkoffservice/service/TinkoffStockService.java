package com.abdisadykov.tinkoffservice.service;

import com.abdisadykov.tinkoffservice.dto.*;
import com.abdisadykov.tinkoffservice.exception.StockNotFoundException;
import com.abdisadykov.tinkoffservice.model.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TinkoffStockService implements StockService {

    private final InvestApi api;

    @Async
    public CompletableFuture<List<InstrumentShort>> getInstrumentShort(String ticker) {
        return api.getInstrumentsService().findInstrument(ticker);
    }

    @Override
    public List<Stock> getStockByTicker(String ticker) {
        var instrumentsService = api.getInstrumentsService();
        var instrumentShorts =
                instrumentsService.findInstrumentSync(ticker);
        if (instrumentShorts.isEmpty()) {
            throw new StockNotFoundException("Stock " + ticker + " not found");
        }
        List<Stock> stockList = instrumentShorts.stream()
                .map(s ->
                        Stock.builder()
                                .ticker(s.getTicker())
                                .figi(s.getFigi())
                                .name(s.getName())
                                .type(s.getInstrumentType())
                                .source("TINKOFF")
                                .build()
                ).toList();

        return stockList.stream()
                .filter(stock -> stock.getTicker().equals(ticker) && stock.getType().equals("share"))
                .toList();
    }

    @Override
    public StocksDto getSeveralStocksByTickers(TickersDto tickers)  {
        List<CompletableFuture<List<InstrumentShort>>> cfList = new ArrayList<>();
        tickers.getTickers().forEach(ticker-> cfList.add(getInstrumentShort(ticker)));
        List<StockDto> stocks = cfList.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .filter(s -> s.getInstrumentType().equals("share"))
                .map(e -> StockDto.builder()
                        .ticker(e.getTicker())
                        .figi(e.getFigi())
                        .name(e.getName())
                        .type(e.getInstrumentType())
                        .source("TINKOFF")
                        .build())
                .toList();

        return new StocksDto(stocks);
    }

    @Override
    public List<Stock> getAllTradableStocks() throws ExecutionException, InterruptedException {
        var instrumentService = api.getInstrumentsService();
        return instrumentService.getTradableShares().join().stream().map(s ->
                        Stock.builder()
                            .ticker(s.getTicker())
                            .figi(s.getFigi())
                            .name(s.getName())
                            .type("Share")
                            .currency(s.getCurrency().toUpperCase(Locale.ROOT))
                            .source("TINKOFF")
                            .build())
                .toList();
    }

    @Async
    public CompletableFuture<GetOrderBookResponse> getOrderBookByFigi(String figi) {
        var orderbook = api.getMarketDataService().getOrderBook(figi, 1);
        //log.info("Getting price {} from", figi);
        return orderbook;
    }

    @Override
    public StocksPricesDto getPrices(FigiesDto figiesDto){
        long start = System.currentTimeMillis();
        List<CompletableFuture<GetOrderBookResponse>> orderBooks = new ArrayList<>();
        figiesDto.getFigies().forEach(figi -> orderBooks.add(getOrderBookByFigi(figi)));
        var stocksPrices = orderBooks.stream()
                .map(CompletableFuture::join)
                //.map(o -> (o==null) throw new StockNotFoundException("Stock not found"))
                .map(ob -> new StockPrice
                        (ob.getFigi(), ob.getLastPrice().getUnits()+ "."+ ob.getLastPrice().getNano()))
                .toList();

        log.info("Time: {}", System.currentTimeMillis()-start);
        return new StocksPricesDto(stocksPrices);
    }


}
