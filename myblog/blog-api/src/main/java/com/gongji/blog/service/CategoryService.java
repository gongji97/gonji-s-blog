package com.gongji.blog.service;

import com.gongji.blog.vo.CategoryVo;
import com.gongji.blog.vo.Result;

import java.util.List;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result findAllDetailById(Long id);
}
