package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    // 模糊查询 (标题, 作者, ISBN) 且未删除
    List<Book> findByTitleContainingOrAuthorContainingOrIsbnContainingAndIsDeleted(
            String title, String author, String isbn, Integer isDeleted);

    List<Book> findByIsDeleted(Integer isDeleted);

    Optional<Book> findByIsbn(String isbn);

    // 用于故事线2：读者查询可借阅图书列表
    List<Book> findByStockGreaterThanAndIsDeleted(Integer stock, Integer isDeleted);
}