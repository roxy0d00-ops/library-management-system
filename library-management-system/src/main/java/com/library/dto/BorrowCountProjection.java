package com.library.dto;

public interface BorrowCountProjection {
    Integer getBookId();
    String getTitle();
    Long getBorrowCount();
}