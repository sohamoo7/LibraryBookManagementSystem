package org.example.repository;

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
public interface BorrowerRepository extends JpaRepository<Borrower, UUID> {
    
    boolean existsByEmail(String email);
    
    @Query("SELECT DISTINCT b FROM Borrower b JOIN b.borrowRecords br " +
           "WHERE br.dueDate < :currentDate AND br.returnDate IS NULL")
    List<Borrower> findBorrowersWithOverdueBooks(@Param("currentDate") LocalDate currentDate);
    
    Optional<Borrower> findByEmail(String email);
}
