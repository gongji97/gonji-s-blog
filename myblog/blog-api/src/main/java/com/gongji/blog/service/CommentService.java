package com.gongji.blog.service;

import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.CommentParams;

public interface CommentService {
    Result commentsByArticleId(Long articleId);

    Result addcomment(CommentParams commentParams);
}
