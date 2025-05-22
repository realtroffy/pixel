package com.example.pixel.controller;

import com.example.pixel.dto.PhoneDataRequestDto;
import com.example.pixel.dto.PhoneDataResponseDto;
import com.example.pixel.service.PhoneDataService;
import com.example.pixel.swagger.PhoneDataControllerSwagger;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/phones")
public class PhoneDataController implements PhoneDataControllerSwagger {

    private final PhoneDataService phoneDataService;

    @PostMapping
    public ResponseEntity<PhoneDataResponseDto> create(@RequestBody @Valid PhoneDataRequestDto phoneDataRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phoneDataService
                .create(phoneDataRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhoneDataResponseDto> update(@PathVariable Long id,
                                                @RequestBody @Valid PhoneDataRequestDto phoneDataRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phoneDataService
                .update(id, phoneDataRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        phoneDataService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
