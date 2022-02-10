package com.lyj.eblog.Vo;

import com.lyj.eblog.pojo.Comment;
import lombok.Data;


@Data
public class CommentVo extends Comment {

    private Long authorId;
    private String authorAvatar;
    private String authorName;

}
