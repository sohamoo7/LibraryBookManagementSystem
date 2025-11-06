package org.example.repository;

import org.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    // Paginated queries
    Page<Book> findByCategoryAndDeletedFalse(String category, Pageable pageable);
    Page<Book> findByIsAvailableTrueAndDeletedFalse(Pageable pageable);
    Page<Book> findByCategoryAndAvailableCopiesGreaterThan(String category, int count, Pageable pageable);
    
    // Non-paginated queries (kept for backward compatibility if needed)
    List<Book> findByCategoryAndDeletedFalse(String category);
    List<Book> findByIsAvailableTrueAndDeletedFalse();
    List<Book> findByCategoryAndAvailableCopiesGreaterThan(String category, int count);
    
    // Common queries
    List<Book> findByDeletedFalse();
    Optional<Book> findByTitleAndAuthorAndDeletedFalse(String title, String author);
    Page<Book> findByDeletedFalse(Pageable pageable);
    boolean existsByTitleAndAuthorAndDeletedFalse(String title, String author);
    Optional<Book> findByIdAndDeletedFalse(UUID id);
}
