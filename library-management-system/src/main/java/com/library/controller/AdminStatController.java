package com.library.controller;

import com.library.common.Result;
import com.library.entity.BorrowRecord;
import com.library.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/admin/stats")
public class AdminStatController {
    @Autowired
    private StatService statService;

    // 1. 查询所有借阅记录 (支持筛选)
    @GetMapping("/records")
    public Result<List<BorrowRecord>> listAllRecords(
            @RequestParam(required = false) Integer readerId,
            @RequestParam(required = false) Integer bookId,
            @RequestParam(required = false) Integer status) {

        List<BorrowRecord> records = statService.listAllRecords(readerId, bookId, status);
        return Result.success(records);
    }

    // 2. 启动异步统计借阅排名
    @PostMapping("/rank/start")
    public Result<String> startBorrowRankStat(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) throws ExecutionException, InterruptedException {

        // 启动异步任务并获取任务ID
        String taskId = statService.startBorrowRankStat(startTime, endTime).get();
        return Result.success(taskId);
    }

    // 3. 获取统计结果
    @GetMapping("/rank/result/{taskId}")
    public Result<?> getStatResult(@PathVariable String taskId) {
        // 返回结果可能是 '统计中' 或 实际数据
        Object result = statService.getStatResult(taskId);
        return Result.success(result);
    }
}