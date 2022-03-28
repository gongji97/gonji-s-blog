package com.gongji.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.gongji.blog.dao.mapper.CategoryMapper;
import com.gongji.blog.dao.pojo.Category;
import com.gongji.blog.service.CategoryService;
import com.gongji.blog.vo.CategoryVo;
import com.gongji.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        CategoryVo categoryVo = new CategoryVo();
        Category category = categoryMapper.selectById(categoryId);
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }

    @Override
    public Result findAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Category::getId,Category::getCategoryName);
        List<Category> categories = categoryMapper.selectList(wrapper);
        //和页面交互的对象
        return Result.success(copyList(categories));
    }

    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        List<Category> categories = categoryMapper.selectList(wrapper);
        //和页面交互的对象
        return Result.success(copyList(categories));

    }

    @Override
    public Result findAllDetailById(Long id) {
        Category category = categoryMapper.selectById(id);

        return Result.success(copy(category));
    }

    private List<CategoryVo> copyList(List<Category> categories) {
        ArrayList<CategoryVo> categoryVos = new ArrayList<>();
        for (Category catgory:categories
             ) {
            categoryVos.add(copy(catgory));
        }
        return categoryVos;
    }

    private CategoryVo copy(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
}
