package com.library.controller;

import com.library.common.Result;
import com.library.dto.BookQueryDTO;
import com.library.dto.BookSaveDTO;
import com.library.entity.Book;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {
    @Autowired
    private BookService bookService;

    // 1. 新增
    @PostMapping
    public Result<Book> addBook(@RequestBody BookSaveDTO dto) {
        Book book = bookService.addBook(dto);
        return Result.success(book);
    }

    // 2. 查询 (列表/模糊)
    @GetMapping
    public Result<List<Book>> listBooks(@ModelAttribute BookQueryDTO query) {
        List<Book> books = bookService.listBooks(query);
        return Result.success(books);
    }

    // 3. 更新
    @PutMapping("/{id}")
    public Result<Book> updateBook(@PathVariable Integer id, @RequestBody BookSaveDTO dto) {
        Book book = bookService.updateBook(id, dto);
        return Result.success(book);
    }

    // 4. 删除
    @DeleteMapping("/{id}")
    public Result<?> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return Result.success("删除成功");
    }
}