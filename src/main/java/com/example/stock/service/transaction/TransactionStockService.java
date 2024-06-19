package com.example.stock.service.transaction;

import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransactionStockService {

    private final StockService stockService;

    public void decrease(Long id, Long quantity) {
        startTransaction();

        stockService.decrease(id, quantity);

        endTransaction();
    }

    private void startTransaction() {
        System.out.println("트랜잭션 시작");
    }

    private void endTransaction() {
        System.out.println("트랜잭션 종료");
    }
}
