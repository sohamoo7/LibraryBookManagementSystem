package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Borrower {
    public enum MembershipType {
        BASIC, PREMIUM
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @OneToMany(mappedBy = "borrower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    
    /**
     * Adds a borrow record for this borrower and sets up the bidirectional relationship.
     * @param borrowRecord The borrow record to add
     */
    public void addBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecords.size() >= maxBorrowLimit) {
            throw new IllegalStateException("Borrow limit reached for this member");
        }
        borrowRecords.add(borrowRecord);
        borrowRecord.setBorrower(this);
    }
    
    /**
     * Removes a borrow record from this borrower and clears the bidirectional relationship.
     * @param borrowRecord The borrow record to remove
     */
    public void removeBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecords.remove(borrowRecord);
        borrowRecord.setBorrower(null);
    }
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType;
    
    @Column(nullable = false)
    private int maxBorrowLimit;
    
    @PrePersist
    @PreUpdate
    private void setMaxBorrowLimit() {
        this.maxBorrowLimit = (this.membershipType == MembershipType.PREMIUM) ? 5 : 2;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
        // Update maxBorrowLimit when membership type changes
        setMaxBorrowLimit();
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }
    
    /**
     * Gets all borrow records for this borrower.
     * @return An unmodifiable list of borrow records
     */
    public List<BorrowRecord> getBorrowRecords() {
        return List.copyOf(borrowRecords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Borrower borrower = (Borrower) o;
        return maxBorrowLimit == borrower.maxBorrowLimit &&
               Objects.equals(id, borrower.id) &&
               Objects.equals(name, borrower.name) &&
               Objects.equals(email, borrower.email) &&
               membershipType == borrower.membershipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, membershipType, maxBorrowLimit);
    }

    @Override
    public String toString() {
        return "Borrower{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", membershipType=" + membershipType +
               ", maxBorrowLimit=" + maxBorrowLimit +
               '}';
    }
}
