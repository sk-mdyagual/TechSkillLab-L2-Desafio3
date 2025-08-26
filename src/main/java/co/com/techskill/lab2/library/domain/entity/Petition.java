package co.com.techskill.lab2.library.domain.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Document(collection = "petitions")
public class Petition {
    private String petitionId;
    private String type; //LEND - RETURN - INSPECT
    private Integer priority;
    private String bookId;
    private LocalDate sentAt;

    public Petition(String petitionId, String type, Integer priority, String bookId, LocalDate sentAt) {
        this.petitionId = petitionId;
        this.type = type;
        this.priority = priority;
        this.bookId = bookId;
        this.sentAt = sentAt;
    }

    public String getPetitionId() {
        return petitionId;
    }

    public void setPetitionId(String petitionId) {
        this.petitionId = petitionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDate getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDate sentAt) {
        this.sentAt = sentAt;
    }
}
