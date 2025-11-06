package org.example.controller;

import org.example.dto.BookRequest;
import org.example.dto.BookResponse;
import org.example.service.BookService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping(value = "/add", 
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.addOrUpdateBook(bookRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.updateBook(id, bookRequest));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title,asc") String[] sort) {
        
        Pageable pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(createSortOrder(sort))
        );
        
        if (category != null && available != null) {
            return ResponseEntity.ok(bookService.getBooksByCategoryAndAvailable(category, pageable));
        } else if (category != null) {
            return ResponseEntity.ok(bookService.getBooksByCategory(category, pageable));
        } else if (available != null && available) {
            return ResponseEntity.ok(bookService.getAvailableBooks(pageable));
        }
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }
    
    private List<Sort.Order> createSortOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Will sort more than 2 fields
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[field, direction]
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }
    
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(java.util.UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }
}
