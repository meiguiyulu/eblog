package com.lyj.eblog.Vo;

import com.lyj.eblog.pojo.Post;
import lombok.Data;

@Data
public class PostVo extends Post {

    /*PostVo在Post类属性的基础上还有下列属性*/

    /*作者id、作者名字、作者头像*/
    private Long authorId;
    private String authorName;
    private String authorAvatar;

    /*类别名字*/
    private String categoryName;
}
