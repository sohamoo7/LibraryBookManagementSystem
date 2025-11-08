package org.example.controller;

import org.example.dto.BorrowerRequest;
import org.example.dto.BorrowerResponse;
import org.example.dto.BorrowRecordResponse;
import org.example.service.IBorrowerService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private final IBorrowerService borrowerService;

    @Autowired
    public BorrowerController(IBorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    public ResponseEntity<BorrowerResponse> registerBorrower(@RequestBody BorrowerRequest request) {
        BorrowerResponse response = borrowerService.registerBorrower(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<List<BorrowRecordResponse>> getBorrowHistory(@PathVariable UUID id) {
        List<BorrowRecordResponse> borrowHistory = borrowerService.getBorrowHistory(id);
        return ResponseEntity.ok(borrowHistory);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowerResponse>> getBorrowersWithOverdueBooks(
            @RequestParam(required = false) LocalDate currentDate) {
        List<BorrowerResponse> response = borrowerService.getBorrowersWithOverdueBooks(
            currentDate != null ? currentDate : LocalDate.now());
        return ResponseEntity.ok(response);
    }


}
