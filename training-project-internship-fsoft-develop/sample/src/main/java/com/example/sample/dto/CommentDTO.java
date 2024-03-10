package com.example.sample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long comment_id;
    private Long book_id;
    private Long user_id;
    private String comment;
    private Date created_at;

    public CommentDTO(Long userId, Long bookid, String comment, String createdAt) {
    }
}
