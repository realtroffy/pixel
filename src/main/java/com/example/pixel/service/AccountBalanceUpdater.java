package com.example.pixel.service;

import com.example.pixel.exception.BatchProcessingInterruptedException;
import com.example.pixel.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AccountBalanceUpdater {

    private final AccountRepository accountRepository;
    private final AccountBatchProcessor batchProcessor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedRate = 30000)
    public void updateBalances() {
        log.info("Starting balance update job");

        int page = 0;
        Queue<Long> failedAccounts = new ConcurrentLinkedQueue<>();
        List<List<Long>> allBatches = new ArrayList<>();

        while (true) {
            List<Long> accountIds = accountRepository.findAccountIdsPaged(PageRequest.of(page, BATCH_SIZE));
            if (accountIds.isEmpty()) break;
            allBatches.add(List.copyOf(accountIds));
            page++;
        }

        CountDownLatch latch = new CountDownLatch(allBatches.size());

        for (int i = 0; i < allBatches.size(); i++) {
            int finalPage = i;
            List<Long> batch = allBatches.get(i);
            executorService.submit(() -> {
                try {
                    batchProcessor.processBatch(batch, failedAccounts, finalPage);
                } finally {
                    latch.countDown();
                }
            });
        }

        executorService.submit(() -> {
            try {
                latch.await();
                if (!failedAccounts.isEmpty()) {
                    log.warn("Failed to update accounts due to locking: {}", failedAccounts);
                } else {
                    log.info("All account balances updated successfully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BatchProcessingInterruptedException("Interrupted while waiting for batch completion", e);
            }
        });
    }

}

