package com.beebook.beebookproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class CommentDTO {
//    private Long comment_id;
//    private Long book_id;
//    private Long user_id;
    private String user_name;
    private String comment;
    private String created_at;

    public CommentDTO(String userName,String comment, String createdAt) {
        this.user_name = userName;
        this.comment = comment;
        this.created_at = createdAt;
    }
}
