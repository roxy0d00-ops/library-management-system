package com.library.controller;

import com.library.common.Result;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.service.BookService;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reader")
public class ReaderBorrowController {
    @Autowired
    private BorrowService borrowService;
    @Autowired
    private BookService bookService;

    // 模拟获取当前读者ID (实际应从会话/Token中获取)
    private Integer getCurrentReaderId() {
        // 假设 ID=1 是测试读者
        return 1;
    }

    // 1. 查询可借阅图书列表
    @GetMapping("/books")
    public Result<List<Book>> listAvailableBooks() {
        List<Book> books = bookService.listAvailableBooks();
        return Result.success(books);
    }

    // 2. 借阅图书
    @PostMapping("/borrow/{bookId}")
    public Result<BorrowRecord> borrowBook(@PathVariable Integer bookId) {
        Integer readerId = getCurrentReaderId();
        BorrowRecord record = borrowService.borrowBook(readerId, bookId);
        return Result.success(record);
    }

    // 3. 归还图书
    @PostMapping("/return/{recordId}")
    public Result<?> returnBook(@PathVariable Integer recordId) {
        Integer readerId = getCurrentReaderId();
        borrowService.returnBook(readerId, recordId);
        return Result.success("归还成功");
    }

    // 4. 查询个人借阅记录
    @GetMapping("/records")
    public Result<List<BorrowRecord>> getMyRecords() {
        Integer readerId = getCurrentReaderId();
        List<BorrowRecord> records = borrowService.getMyRecords(readerId);
        return Result.success(records);
    }
}