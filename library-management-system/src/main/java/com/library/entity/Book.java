package com.library.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String category;

    @Column(nullable = false)
    private Integer stock = 0;

    private String publisher;

    @Column(name = "publish_date")
    @Temporal(TemporalType.DATE)
    private Date publishDate;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0; // 逻辑删除: 0-未删除, 1-已删除
}