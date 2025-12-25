package com.library.service;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BorrowService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BorrowRecordRepository recordRepository;

    private final int BORROW_DAYS = 30;

    // 1. 读者借阅图书
    public BorrowRecord borrowBook(Integer readerId, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .filter(b -> b.getIsDeleted() == 0)
                .orElseThrow(() -> new RuntimeException("图书不存在或已删除"));

        if (book.getStock() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 校验读者是否有超期未归还 (状态 2)
        if (recordRepository.existsByReaderIdAndStatus(readerId, 2)) {
            throw new RuntimeException("您有超期未归还的图书（状态：超期未归还），请先归还");
        }

        // 校验单本图书是否已借阅且未归还 (状态 0 或 2)
        if (recordRepository.existsByReaderIdAndBookIdAndStatusIn(readerId, bookId, List.of(0, 2))) {
            throw new RuntimeException("您已借阅过此本图书且未归还");
        }

        // 减库存
        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setReaderId(readerId);
        record.setBookId(bookId);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, BORROW_DAYS);
        record.setDueTime(cal.getTime()); // 应归还时间

        record.setStatus(0); // 0-未归还
        return recordRepository.save(record);
    }

    // 2. 读者归还图书
    public void returnBook(Integer readerId, Integer recordId) {
        BorrowRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if (!record.getReaderId().equals(readerId)) {
            throw new RuntimeException("无权操作此借阅记录");
        }

        // 状态 0 或 2 才能归还
        if (record.getStatus() != 0 && record.getStatus() != 2) {
            throw new RuntimeException("此记录已归还或状态异常");
        }

        // 更新记录
        Date now = new Date();
        record.setReturnTime(now);

        // 判断是否超期
        if (now.after(record.getDueTime())) {
            record.setStatus(3); // 3-已归还且超期
        } else {
            record.setStatus(1); // 1-已归还
        }
        recordRepository.save(record);

        // 增库存
        Book book = bookRepository.findById(record.getBookId())
                .orElseThrow(() -> new RuntimeException("图书信息异常"));
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);
    }

    // 3. 查询个人借阅记录
    public List<BorrowRecord> getMyRecords(Integer readerId) {
        return recordRepository.findByReaderIdOrderByBorrowTimeDesc(readerId);
    }

    // 4. 辅助方法：定时任务或手动调用，检查并更新超期状态 (0 -> 2)
    public void checkAndUpdateOverdue() {
        // 实际操作会涉及大量查询和更新，这里只提供思路
        // SQL: UPDATE borrow_record SET status = 2 WHERE status = 0 AND due_time < NOW()
        // JPA/HQL 实现：
        // @Modifying @Query("UPDATE BorrowRecord r SET r.status = 2 WHERE r.status = 0 AND r.dueTime < CURRENT_TIMESTAMP")
        // recordRepository.updateOverdueStatus();
    }
}