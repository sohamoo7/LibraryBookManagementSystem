package org.example.service;

import org.example.dto.BorrowRecordResponse;
import org.example.dto.BorrowerRequest;
import org.example.dto.BorrowerResponse;
import org.example.exception.ResourceAlreadyExistsException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Borrower;
import org.example.repository.BorrowerRepository;
import org.example.repository.BorrowRecordRepository;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BorrowerService(BorrowerRepository borrowerRepository, 
                         BorrowRecordRepository borrowRecordRepository,
                         ModelMapper modelMapper) {
        this.borrowerRepository = borrowerRepository;
        this.borrowRecordRepository = borrowRecordRepository;
        this.modelMapper = modelMapper;
    }

    public BorrowerResponse getBorrowerById(UUID id) {
        Borrower borrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
                
        return modelMapper.map(borrower, BorrowerResponse.class);
    }
    
    public BorrowerResponse registerBorrower(BorrowerRequest request) {
        // Check if email already exists
        Optional<Borrower> existingBorrower = borrowerRepository.findByEmail(request.getEmail());
        if (existingBorrower.isPresent()) {
            throw new ResourceAlreadyExistsException("Borrower with email " + request.getEmail() + " already exists with borrowerID " + existingBorrower.get().getId());
        }

        // Map request to entity
        Borrower borrower = modelMapper.map(request, Borrower.class);
        
        // Save the borrower (maxBorrowLimit will be set by @PrePersist)
        Borrower savedBorrower = borrowerRepository.save(borrower);
        
        // Map entity to response DTO
        return modelMapper.map(savedBorrower, BorrowerResponse.class);
    }
    

    public List<BorrowerResponse> getBorrowersWithOverdueBooks(LocalDate currentDate) {
        return borrowerRepository.findBorrowersWithOverdueBooks(currentDate).stream()
                .map(borrower -> modelMapper.map(borrower, BorrowerResponse.class))
                .collect(Collectors.toList());
    }
    
    public List<org.example.dto.BorrowRecordResponse> getBorrowHistory(UUID borrowerId) {
        // First verify borrower exists
        borrowerRepository.findById(borrowerId)
            .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + borrowerId));
            
        return borrowRecordRepository.findByBorrowerIdOrderByBorrowDateDesc(borrowerId).stream()
            .map(record -> {
                BorrowRecordResponse response = modelMapper.map(record, BorrowRecordResponse.class);
                response.setBookId(record.getBook().getId());
                response.setBookTitle(record.getBook().getTitle());
                response.setStatus(record.getReturnDate() != null ? "RETURNED" : 
                                 record.getDueDate().isBefore(LocalDate.now()) ? "OVERDUE" : "BORROWED");
                return response;
            })
            .collect(Collectors.toList());
    }
}
