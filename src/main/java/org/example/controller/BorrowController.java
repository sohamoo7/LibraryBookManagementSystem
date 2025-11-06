package org.example.controller;

import org.example.dto.BorrowRequest;
import org.example.dto.BorrowResponse;
import org.example.dto.ReturnBookResponse;
import org.example.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<BorrowResponse> borrowBook(@RequestBody BorrowRequest request) {
        BorrowResponse response = borrowService.borrowBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/return")
    public ResponseEntity<ReturnBookResponse> returnBook(@RequestBody BorrowRequest request) {
        ReturnBookResponse response = borrowService.returnBook(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/records/active")
    public ResponseEntity<List<BorrowResponse>> getActiveBorrows() {
        List<BorrowResponse> response = borrowService.getActiveBorrows();
        return ResponseEntity.ok(response);
    }
}
