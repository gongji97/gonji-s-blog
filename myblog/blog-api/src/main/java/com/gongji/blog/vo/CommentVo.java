package com.gongji.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo {

        //防止前端精度损失
//        @JsonSerialize(using = ToStringSerializer.class)
        private String id;

        private UserVo author;

        private String content;

        private List<CommentVo> childrens;

        private Long createDate;

        private Integer level;

        private UserVo toUser;

        //总评论数
        private Integer commentCounts;

}
