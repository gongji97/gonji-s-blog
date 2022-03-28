package com.gongji.blog.controller;

import com.gongji.blog.service.CategoryService;
import com.gongji.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryCotroller {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
     private Result Categorise(){
        return categoryService.findAll();
    }
    @GetMapping("detail")
    public Result categoriesDetail(){
        return categoryService.findAllDetail();
    }
    @GetMapping("detail/{id}")
    public Result categoriesDetailById(@PathVariable("id") Long id){
        return categoryService.findAllDetailById(id);
    }
}
