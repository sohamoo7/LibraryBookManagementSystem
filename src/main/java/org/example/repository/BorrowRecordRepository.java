package org.example.repository;

import org.example.dto.BorrowRecordResponse;
import org.example.model.Book;
import org.example.model.BorrowRecord;
import org.example.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {
    
    @Query("SELECT COUNT(br) FROM BorrowRecord br " +
           "WHERE br.borrower.id = :borrowerId AND br.returnDate IS NULL")
    int countActiveBorrowsByBorrowerId(@Param("borrowerId") UUID borrowerId);
    
    @Query("SELECT br FROM BorrowRecord br " +
           "WHERE br.returnDate IS NULL")
    List<BorrowRecord> findAllActiveBorrows();
    
    @Query("SELECT COUNT(br) > 0 FROM BorrowRecord br " +
           "WHERE br.borrower.id = :borrowerId AND br.book.id = :bookId AND br.returnDate IS NULL")
    boolean isBookBorrowedByBorrower(@Param("bookId") UUID bookId, 
                                   @Param("borrowerId") UUID borrowerId);
    
    @Query("SELECT br FROM BorrowRecord br " +
           "LEFT JOIN FETCH br.book " +
           "LEFT JOIN FETCH br.borrower " +
           "WHERE br.borrower.id = :borrowerId " +
           "AND br.book.id = :bookId " +
           "AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveBorrowWithBookAndBorrower(
            @Param("borrowerId") UUID borrowerId,
            @Param("bookId") UUID bookId);
            
    /**
     * Get borrow history with DTO projection for better performance
     */
    @Query("""
        SELECT new org.example.dto.BorrowRecordResponse(
            br.id,
            br.book.id,
            br.book.title,
            br.borrower.id,
            br.borrowDate,
            br.dueDate,
            br.returnDate,
            br.fineAmount,
            CASE 
                WHEN br.returnDate IS NOT NULL THEN 'RETURNED'
                WHEN br.dueDate < CURRENT_DATE THEN 'OVERDUE'
                ELSE 'BORROWED'
            END
        )
        FROM BorrowRecord br
        WHERE br.borrower.id = :borrowerId
        ORDER BY br.borrowDate DESC
    """)
    List<BorrowRecordResponse> findBorrowHistoryByBorrowerId(@Param("borrowerId") UUID borrowerId);
}
