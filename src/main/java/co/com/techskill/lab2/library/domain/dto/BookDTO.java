package co.com.techskill.lab2.library.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDTO {
    private String bookId;
    private String isbn;
    private String name;
    private Integer amount;
    private Boolean available;

    public BookDTO(String name, String isbn, Integer amount){
        this.name = name;
        this.isbn = isbn;
        this.amount = amount;

    }

    public BookDTO(String bookId, String isbn, String name, Integer amount, Boolean available) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.name = name;
        this.amount = amount;
        this.available = available;

    }

    public BookDTO(String bookId){
        this.bookId = bookId;
    }

    public BookDTO(){

    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
