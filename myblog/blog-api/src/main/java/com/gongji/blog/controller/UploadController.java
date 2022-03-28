package com.gongji.blog.controller;

import com.gongji.blog.utils.QiniuUtils;
import com.gongji.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping
    public Result upLoad(@RequestParam("image") MultipartFile file) {
        //原始文件名称
        String originalFilename = file.getOriginalFilename();
        //唯一的文件名称
        String finalName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename,".");
        //降低自身服务器的带宽消耗
        boolean upload = qiniuUtils.upload(file, finalName);
        if (upload){
            return Result.success(QiniuUtils.url + finalName);
        }
        return Result.fail(20001,"上传失败");


    }

}
