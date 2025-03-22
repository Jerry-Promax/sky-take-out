package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    // 从配置文件中读取图片保存的路径
    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件为空");
        }
        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + ext;
        try {
            // 保存文件到指定路径
            Path filePath = Paths.get(uploadPath, newFileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            log.info("文件上传成功：{}", filePath);
            return Result.success(newFileName);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }

    @GetMapping("/display/{fileName}")
    public ResponseEntity<Resource> display(@PathVariable String fileName) {
        try {
            // 根据文件名获取文件
            Path filePath = Paths.get(uploadPath, fileName);
            File file = filePath.toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            // 读取文件并返回
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            log.error("文件读取失败", e);
            return ResponseEntity.status(500).build();
        }
    }
}