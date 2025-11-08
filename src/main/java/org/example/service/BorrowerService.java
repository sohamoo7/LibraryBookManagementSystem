package org.example.service;

import java.time.LocalDate;

import org.example.dto.BorrowRecordResponse;
import org.example.dto.BorrowerRequest;
import org.example.dto.BorrowerResponse;
import org.example.exception.ResourceAlreadyExistsException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Borrower;
import org.example.repository.BorrowerRepository;
import org.example.repository.BorrowRecordRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Transactional
@Service
public class BorrowerService implements IBorrowerService {

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

    @Override
    public BorrowerResponse registerBorrower(BorrowerRequest request) {
        // Check if email already exists
        Optional<Borrower> existingBorrower = borrowerRepository.findByEmail(request.getEmail());
        if (existingBorrower.isPresent()) {
            throw new ResourceAlreadyExistsException("Borrower with email " + request.getEmail() + 
                " already exists with borrowerID " + existingBorrower.get().getId());
        }

        // Map request to entity
        Borrower borrower = modelMapper.map(request, Borrower.class);
        
        // Save the borrower (maxBorrowLimit will be set by @PrePersist)
        Borrower savedBorrower = borrowerRepository.save(borrower);
        
        // Map entity to response DTO
        return modelMapper.map(savedBorrower, BorrowerResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowerResponse> getBorrowersWithOverdueBooks(LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
        
        return borrowerRepository.findBorrowersWithOverdueBooks(currentDate).stream()
                .map(borrower -> modelMapper.map(borrower, BorrowerResponse.class))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponse> getBorrowHistory(UUID borrowerId) {
        // Verify borrower exists
        if (!borrowerRepository.existsById(borrowerId)) {
            throw new ResourceNotFoundException("Borrower not found with id: " + borrowerId);
        }
            
        // Use DTO projection for better performance
        return borrowRecordRepository.findBorrowHistoryByBorrowerId(borrowerId);
    }
}
