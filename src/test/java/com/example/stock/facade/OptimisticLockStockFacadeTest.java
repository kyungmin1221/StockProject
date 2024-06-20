package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import com.example.stock.service.PessimisticLockStockService;
import com.example.stock.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockStockFacadeTest {


    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }


    // 한 아이디를 대상으로 동시에 N 개의 요청을 보낸다면?
    @Test
    @DisplayName("낙관적 락을 걸었을 경우")
    public void sameRequest() throws InterruptedException {
        int threadCount = 100;

        // ExecutorService : 비동기로 실행하는 작업을 단순화하여 사용할 수 있도록 도와주는 자바의 API
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행중인 작업이 완료될 때 까지 대기할 수 있도록 도와주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i < threadCount; i++) {
            executorService.submit( () -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0, stock.getQuantity(),"테스트 미통과");
        System.out.println("테스트 통과");
    }
    
}