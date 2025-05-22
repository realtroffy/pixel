package com.example.pixel.unit.service;

import com.example.pixel.dto.TransferRequestDto;
import com.example.pixel.exception.ValidationException;
import com.example.pixel.model.Account;
import com.example.pixel.repository.AccountRepository;
import com.example.pixel.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @InjectMocks private AccountServiceImpl accountService;

    @Test
    void transfer_successful() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        BigDecimal amount = new BigDecimal("50.00");

        Account sender = Account.builder()
                .id(senderId)
                .currentBalance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .build();

        Account receiver = Account.builder()
                .id(receiverId)
                .currentBalance(new BigDecimal("10.00"))
                .initialBalance(new BigDecimal("10.00"))
                .build();

        TransferRequestDto dto = new TransferRequestDto(receiverId, amount);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(senderId.toString());

        when(accountRepository.findWithDelayOneSecondByIdWithLock(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findWithDelayOneSecondByIdWithLock(receiverId)).thenReturn(Optional.of(receiver));

        // when
        accountService.transfer(auth, dto);

        // then
        assertThat(sender.getCurrentBalance()).isEqualByComparingTo("50.00");
        assertThat(receiver.getCurrentBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void transfer_insufficientFunds_shouldThrow() {
        Long senderId = 1L;
        Long receiverId = 2L;
        BigDecimal amount = new BigDecimal("200.00");

        Account sender = Account.builder()
                .id(senderId)
                .currentBalance(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("100.00"))
                .build();

        Account receiver = Account.builder()
                .id(receiverId)
                .currentBalance(new BigDecimal("0.00"))
                .initialBalance(new BigDecimal("0.00"))
                .build();

        TransferRequestDto dto = new TransferRequestDto(receiverId, amount);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(senderId.toString());

        when(accountRepository.findWithDelayOneSecondByIdWithLock(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findWithDelayOneSecondByIdWithLock(receiverId)).thenReturn(Optional.of(receiver));

        assertThatThrownBy(() -> accountService.transfer(auth, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Insufficient funds");
    }
}
