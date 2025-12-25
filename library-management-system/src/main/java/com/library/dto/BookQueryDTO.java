package com.library.dto;

import lombok.Data;

@Data
public class BookQueryDTO {
    private String keyword; // 模糊查询关键字
}