package com.library.repository;

import com.library.dto.BorrowCountProjection;
import com.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {

    // 故事线1：删除图书约束
    boolean existsByBookIdAndStatusIn(Integer bookId, List<Integer> statuses);

    // 故事线2：校验读者是否有超期未归还
    boolean existsByReaderIdAndStatus(Integer readerId, Integer status);

    // 故事线2：校验单本图书是否已借阅且未归还
    boolean existsByReaderIdAndBookIdAndStatusIn(Integer readerId, Integer bookId, List<Integer> statuses);

    // 故事线2：查询个人借阅记录
    List<BorrowRecord> findByReaderIdOrderByBorrowTimeDesc(Integer readerId);

    // 故事线3：查询所有借阅记录 (动态筛选)
    @Query("SELECT r FROM BorrowRecord r WHERE " +
            "(:readerId IS NULL OR r.readerId = :readerId) AND " +
            "(:bookId IS NULL OR r.bookId = :bookId) AND " +
            "(:status IS NULL OR r.status = :status)")
    List<BorrowRecord> findFilteredRecords(@Param("readerId") Integer readerId,
                                           @Param("bookId") Integer bookId,
                                           @Param("status") Integer status);

    // 故事线3：统计指定时间段内的图书借阅量排名 (使用 Native SQL 优化 JOIN 和 COUNT)
    @Query(value = """
        SELECT
            br.book_id AS bookId,
            b.title AS title,
            COUNT(br.id) AS borrowCount
        FROM borrow_record br
        JOIN book b ON br.book_id = b.id
        WHERE br.borrow_time BETWEEN :startTime AND :endTime
        GROUP BY br.book_id, b.title
        ORDER BY borrowCount DESC
    """, nativeQuery = true)
    List<BorrowCountProjection> countBorrowRank(@Param("startTime") Date startTime,
                                                @Param("endTime") Date endTime);
}