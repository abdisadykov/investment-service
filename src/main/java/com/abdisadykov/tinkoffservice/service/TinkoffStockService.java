package com.abdisadykov.tinkoffservice.service;

import com.abdisadykov.tinkoffservice.dto.StockDto;
import com.abdisadykov.tinkoffservice.dto.StocksDto;
import com.abdisadykov.tinkoffservice.dto.TickersDto;
import com.abdisadykov.tinkoffservice.exception.StockNotFoundException;
import com.abdisadykov.tinkoffservice.model.Currency;
import com.abdisadykov.tinkoffservice.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        List<Stock> stockList = instrumentShorts.stream()
                .map(s ->
                        Stock.builder()
                                .ticker(s.getTicker())
                                .figi(s.getFigi())
                                .name(s.getName())
                                .type(s.getInstrumentType())
                                .currency("")
                                .source("TINKOFF")
                                .build()
                ).toList();
        if (stockList.isEmpty()) {
            throw new StockNotFoundException("Stock " + ticker + " not found");
        }
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
        var instrumentsService = api.getInstrumentsService();
        var completableFuture =
                instrumentsService.getTradableShares();

        return completableFuture.get().stream().map(s ->
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
}
