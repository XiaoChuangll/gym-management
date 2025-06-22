package com.gym.management.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file, HttpSession session) {
        // 检查用户是否登录
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("请先登录");
        }

        try {
            // 创建上传目录
            File directory = new File(uploadDir + "/avatars");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成文件名
            String newFilename = username + "_" + System.currentTimeMillis() + fileExtension;
            Path path = Paths.get(directory.getAbsolutePath(), newFilename);

            // 保存文件
            Files.write(path, file.getBytes());

            // 返回文件路径
            Map<String, String> response = new HashMap<>();
            response.put("url", "/uploads/avatars/" + newFilename);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("文件上传失败: " + e.getMessage());
        }
    }
}