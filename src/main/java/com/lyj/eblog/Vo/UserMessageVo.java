package com.lyj.eblog.Vo;

import com.lyj.eblog.pojo.UserMessage;
import lombok.Data;

@Data
public class UserMessageVo extends UserMessage {
    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;
}
