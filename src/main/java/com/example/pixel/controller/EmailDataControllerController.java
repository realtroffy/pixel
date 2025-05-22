package com.example.pixel.controller;

import com.example.pixel.dto.EmailDataRequestDto;
import com.example.pixel.dto.EmailDataResponseDto;
import com.example.pixel.service.EmailDataService;
import com.example.pixel.swagger.EmailDataControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/emails")
public class EmailDataControllerController implements EmailDataControllerSwagger {

    private final EmailDataService emailDataService;

    @PostMapping
    public ResponseEntity<EmailDataResponseDto> create(@RequestBody @Valid EmailDataRequestDto emailDataRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailDataService.create(emailDataRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailDataResponseDto> update(@PathVariable Long id,
                                                       @RequestBody @Valid EmailDataRequestDto emailDataRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailDataService.update(id, emailDataRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emailDataService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
