package org.example.service;

import org.example.dto.BorrowerRequest;
import org.example.dto.BorrowerResponse;
import org.example.exception.ResourceAlreadyExistsException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Borrower;
import org.example.repository.BorrowerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public BorrowerService(BorrowerRepository borrowerRepository, ModelMapper modelMapper) {
        this.borrowerRepository = borrowerRepository;
        this.modelMapper = modelMapper;
    }

    public BorrowerResponse registerBorrower(BorrowerRequest request) {
        // Check if email already exists
        if (borrowerRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Borrower with email " + request.getEmail() + " already exists");
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

    public Borrower getBorrowerById(UUID id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + id));
    }
}
