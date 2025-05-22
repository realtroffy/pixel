package com.example.pixel.it.controller;

import com.example.pixel.exception.EntityNotFoundException;
import com.example.pixel.exception.ValidationException;
import com.example.pixel.it.PostgresTestContainer;
import com.example.pixel.model.Account;
import com.example.pixel.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integrationTest")
class AccountControllerIT extends PostgresTestContainer {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long senderId;
    private Long receiverId;
    private String transferRequestDtoJson;

    @BeforeEach
    void setup() {

        transferRequestDtoJson = """
                {
                  "receiverId": 2,
                  "amount": 25.00
                }
                """;

        senderId = 1L;
        receiverId = 2L;
    }

    @Test
    @WithMockUser(username = "1")
    @Transactional
    void testTransfer() throws Exception {
        mockMvc.perform(post("/api/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequestDtoJson))
                .andExpect(status().isOk());

        Account senderAcc = accountRepository.findWithDelayOneSecondByIdWithLock(senderId).orElseThrow();
        Account receiverAcc = accountRepository.findWithDelayOneSecondByIdWithLock(receiverId).orElseThrow();

        assertThat(senderAcc.getCurrentBalance()).isEqualByComparingTo("75.00");
        assertThat(receiverAcc.getCurrentBalance()).isEqualByComparingTo("35.00");
    }

    @Test
    @WithMockUser(username = "1")
    void testTransferFailsWhenAccountIsLocked() throws Exception {

        Thread lockerThread = new Thread(() -> transactionTemplate.executeWithoutResult(status -> {
            accountRepository.findWithDelayOneSecondByIdWithLock(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

        lockerThread.start();
        Thread.sleep(1000);

        mockMvc.perform(post("/api/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferRequestDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException())
                                .isInstanceOf(ValidationException.class)
                                .hasMessageContaining("Transfer failed because one of the accounts is currently locked.")
                );

        lockerThread.join();
    }
}