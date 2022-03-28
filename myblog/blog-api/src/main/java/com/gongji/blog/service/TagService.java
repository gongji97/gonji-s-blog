package com.gongji.blog.service;

import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.TagVo;

import java.util.List;

public interface TagService {
    List<TagVo> findTagsByArticleId(long articleId);

    Result hots(int limit);

    Result findAll();

    Result findAllDetail();
}
