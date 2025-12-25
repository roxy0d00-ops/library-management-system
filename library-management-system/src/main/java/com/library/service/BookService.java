package com.library.service;

import com.library.dto.BookQueryDTO;
import com.library.dto.BookSaveDTO;
import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowRecordRepository recordRepository;

    // 1. 新增图书
    public Book addBook(BookSaveDTO dto) {
        if (bookRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            throw new RuntimeException("ISBN已存在");
        }
        Book book = new Book();
        BeanUtils.copyProperties(dto, book);
        return bookRepository.save(book);
    }

    // 2. 查询图书 (列表/模糊查询)
    public List<Book> listBooks(BookQueryDTO query) {
        if (query == null || query.getKeyword() == null || query.getKeyword().isEmpty()) {
            return bookRepository.findByIsDeleted(0);
        } else {
            String keyword = query.getKeyword();
            return bookRepository.findByTitleContainingOrAuthorContainingOrIsbnContainingAndIsDeleted(
                    keyword, keyword, keyword, 0);
        }
    }

    // 3. 更新图书信息
    public Book updateBook(Integer id, BookSaveDTO dto) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("图书不存在"));

        // 校验更新后的 ISBN
        if (!book.getIsbn().equals(dto.getIsbn()) && bookRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            throw new RuntimeException("新ISBN已被占用");
        }

        BeanUtils.copyProperties(dto, book, "id", "isDeleted"); // 忽略ID和删除标记
        return bookRepository.save(book);
    }

    // 4. 删除图书 (逻辑删除)
    public void deleteBook(Integer bookId) {
        // 约束：状态 0 (未归还) 或 2 (超期未归还) 不允许删除
        if (recordRepository.existsByBookIdAndStatusIn(bookId, List.of(0, 2))) {
            throw new RuntimeException("该图书存在未归还的借阅记录，不允许删除");
        }

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("图书不存在"));
        book.setIsDeleted(1);
        bookRepository.save(book);
    }

    // 5. 查询可借阅图书列表 (供读者端使用)
    public List<Book> listAvailableBooks() {
        return bookRepository.findByStockGreaterThanAndIsDeleted(0, 0);
    }
}