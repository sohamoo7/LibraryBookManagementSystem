package org.example.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id",
    scope = Borrower.class
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType membershipType = MembershipType.BASIC;
    
    @OneToMany(mappedBy = "borrower", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference("borrower-borrowRecords")
    private List<BorrowRecord> borrowRecords;  // Will be initialized in constructor
    
    public Borrower() {
        this.borrowRecords = new ArrayList<>();
    }
    
    /**
     * Adds a borrow record for this borrower and sets up the bidirectional relationship.
     * @param borrowRecord The borrow record to add
     */
    /**
     * Adds a borrow record for this borrower and sets up the bidirectional relationship.
     * @param borrowRecord The borrow record to add (must not be null)
     * @throws IllegalArgumentException if borrowRecord is null
     * @throws IllegalStateException if borrower has reached their borrow limit
     */
    /**
     * Adds a borrow record to this borrower's records.
     * Note: This method only manages the bidirectional relationship.
     * The service layer should handle business logic like borrow limits.
     * @param borrowRecord The borrow record to add (must not be null)
     * @throws IllegalArgumentException if borrowRecord is null
     */
    public void addBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            throw new IllegalArgumentException("Borrow record cannot be null");
        }
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        }
        if (!this.borrowRecords.contains(borrowRecord)) {
            this.borrowRecords.add(borrowRecord);
            // The BorrowRecord.setBorrower() method will handle the other side of the relationship
            borrowRecord.setBorrower(this);
        }
    }
    /**
     * Removes a borrow record from this borrower and clears the bidirectional relationship.
     * @param borrowRecord The borrow record to remove
     */
    /**
     * Removes a borrow record from this borrower's records.
     * @param borrowRecord The borrow record to remove
     */
    public void removeBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord != null && this.borrowRecords != null && this.borrowRecords.remove(borrowRecord)) {
            // The BorrowRecord.setBorrower(null) will handle the other side
            borrowRecord.setBorrower(null);
        }
    }
    
    @Column(nullable = false)
    private int maxBorrowLimit;
    
    @PrePersist
    @PreUpdate
    public void setMaxBorrowLimit() {
        this.maxBorrowLimit = (this.membershipType == MembershipType.PREMIUM) ? 5 : 2;
    }

    // Getters and Setters
//    public UUID getId() {
//        return id;
//    }
    public UUID getId() {
        return this.id;
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
    /**
     * Gets all borrow records for this borrower.
     * @return A list of borrow records
     */
    public List<BorrowRecord> getBorrowRecords() {
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        }
        return new ArrayList<>(borrowRecords);  // Return a copy to prevent direct modifications
    }
    
    /**
     * Gets the maximum number of books this borrower can borrow.
     * Premium members can borrow 5 books, basic members can borrow 2.
     * @return the maximum number of books that can be borrowed
     */
    public int getMaxBorrowLimit() {
        return this.membershipType == MembershipType.PREMIUM ? 5 : 2;
    }
    
    public MembershipType getMembershipType() {
        return membershipType;
    }
    
    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
    
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        if (borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        } else {
            this.borrowRecords = new ArrayList<>(borrowRecords);
        }
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
