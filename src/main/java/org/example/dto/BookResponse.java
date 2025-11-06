
package org.example.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BookResponse {
    private UUID id;
    private String title;
    private String author;
    private String category;
    private boolean available;
    private int totalCopies;
    private int availableCopies;

}