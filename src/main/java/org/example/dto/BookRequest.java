package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BookRequest {
    @NotBlank(message = "Title is required")
    @JsonProperty("title")
    private String title;
    
    @NotBlank(message = "Author is required")
    @JsonProperty("author")
    private String author;
    
    @NotBlank(message = "Category is required")
    @JsonProperty("category")
    private String category;
    
    @Positive(message = "Total copies must be a positive number")
    @JsonProperty("totalCopies")
    private int totalCopies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}
