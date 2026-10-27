package io.github.listerstellar.fundrising.controller;

import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.service.FundraisingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fundraisings")
@RequiredArgsConstructor
public class FundraisingController {

    private final FundraisingService fundraisingService;

    @GetMapping
    public ResponseEntity<List<FundraisingResponseDto>> getFundraisings() {
        return ResponseEntity.ok(fundraisingService.getFundraisings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundraisingResponseDto> getFundraising(@PathVariable UUID id) {
        return ResponseEntity.ok(fundraisingService.getFundraisingById(id));
    }

    @PostMapping
    public ResponseEntity<FundraisingResponseDto> createFundraising(@Valid @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fundraisingService.createFundraising(fundraisingRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FundraisingResponseDto> updateFundraising(@PathVariable UUID id,
                                                                    @Valid @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        return ResponseEntity.ok(fundraisingService.updateFundraising(id, fundraisingRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FundraisingResponseDto> partialUpdateFundraising(@PathVariable UUID id,
                                                                           @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        return ResponseEntity.ok(fundraisingService.partialUpdateFundraising(id, fundraisingRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFundraising(@PathVariable UUID id) {
        fundraisingService.deleteFundraisingById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/favoured")
    public ResponseEntity<List<CustomerResponseDto>> getFavouredByCustomersById(@PathVariable UUID id) {
        return ResponseEntity.ok(fundraisingService.getFavouredByCustomersById(id));
    }
}
