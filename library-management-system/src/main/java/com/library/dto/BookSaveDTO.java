package com.library.dto;

import lombok.Data;
import java.util.Date;

@Data
public class BookSaveDTO {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer stock;
    private String publisher;
    private Date publishDate;
}