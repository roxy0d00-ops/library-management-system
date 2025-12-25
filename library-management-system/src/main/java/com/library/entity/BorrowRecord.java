package com.library.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "borrow_record")
@Data
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "reader_id", nullable = false)
    private Integer readerId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "borrow_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date borrowTime = new Date();

    @Column(name = "due_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueTime; // 应归还时间

    @Column(name = "return_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnTime; // 实际归还时间

    // 0-未归还, 1-已归还, 2-超期未归还 (仍在借阅中), 3-已归还且超期
    @Column(nullable = false)
    private Integer status = 0;

    // 可添加关联关系（可选，Service层使用 ID 更直接）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;
}