package com.library.service;

import com.library.dto.BorrowCountProjection;
import com.library.entity.BorrowRecord;
import com.library.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatService {
    @Autowired
    private BorrowRecordRepository recordRepository;
    @Autowired
    private AsyncTaskResultManager resultManager;

    // 1. 查询所有借阅记录 (支持筛选)
    public List<BorrowRecord> listAllRecords(Integer readerId, Integer bookId, Integer status) {
        return recordRepository.findFilteredRecords(readerId, bookId, status);
    }

    // 2. 异步统计借阅量排名
    @Async // 异步执行
    public CompletableFuture<String> startBorrowRankStat(Date startTime, Date endTime) {
        String taskId = UUID.randomUUID().toString();
        resultManager.putTaskStatus(taskId, "PROCESSING");

        try {
            // 执行耗时的统计查询
            List<BorrowCountProjection> rankList = recordRepository.countBorrowRank(startTime, endTime);

            // 存储结果
            resultManager.putTaskResult(taskId, rankList);
            resultManager.putTaskStatus(taskId, "SUCCESS");

        } catch (Exception e) {
            resultManager.putTaskStatus(taskId, "FAILED: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(taskId);
    }

    // 3. 获取统计结果
    public Object getStatResult(String taskId) {
        return resultManager.getTaskResult(taskId);
    }
}

// 辅助类：用于存储异步任务结果
@Service
class AsyncTaskResultManager {
    // 存储任务状态：PROCESSING, SUCCESS, FAILED
    private final Map<String, String> taskStatus = new ConcurrentHashMap<>();
    // 存储任务结果
    private final Map<String, Object> taskResults = new ConcurrentHashMap<>();

    public void putTaskStatus(String taskId, String status) {
        taskStatus.put(taskId, status);
        // 可以在这里设置过期时间
    }

    public String getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, "NOT_FOUND");
    }

    // 封装查询状态和结果
    public Object getTaskResult(String taskId) {
        String status = getTaskStatus(taskId);
        if ("PROCESSING".equals(status)) {
            return "统计中，请稍后查询";
        } else if ("SUCCESS".equals(status)) {
            return taskResults.get(taskId);
        } else if (status.startsWith("FAILED")) {
            return "统计失败: " + status.substring(8);
        } else {
            return "任务ID不存在";
        }
    }

    public void putTaskResult(String taskId, Object result) {
        taskResults.put(taskId, result);
    }
}