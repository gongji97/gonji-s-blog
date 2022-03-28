package com.gongji.blog.service;

import com.gongji.blog.vo.Result;
import com.gongji.blog.vo.params.ArticleParam;
import com.gongji.blog.vo.params.PageParams;

public interface ArticleService {
    Result findArticleById(Long id);

    Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    Result newArticle(int limit);

    Result listArchives();

    /**
     * 文章发表服务
     * @param articleParam
     * @return
     */
    Result publish(ArticleParam articleParam);
}
