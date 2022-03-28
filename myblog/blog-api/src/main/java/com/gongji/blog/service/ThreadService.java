package com.gongji.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gongji.blog.dao.mapper.ArticleMapper;
import com.gongji.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {
    //期望此操作在线程池中执行
    @Async("taskExcutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        Article article1 = new Article();
        article1.setViewCounts(viewCounts + 1);
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getViewCounts,viewCounts);
        articleMapper.update(article1,articleLambdaUpdateWrapper);
        try {
            Thread.sleep(5000);
            System.out.println("更新完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
