package com.gongji.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gongji.blog.dao.dos.Archives;
import com.gongji.blog.dao.pojo.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    List<Archives> listArchices();

    IPage<Article> listArticle(
            Page<Article> page,
            Long categoryId,
            Long tagId,
            String year,
            String month
    );
}
