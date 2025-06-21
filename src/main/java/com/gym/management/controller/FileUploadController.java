package com.gym.management.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传诊断控制器，用于检查文件上传配置是否正确
 */
@Controller
@RequestMapping("/api/file-upload")
public class FileUploadController {

    @Value("${app.upload.dir:/www/wwwroot/gym/uploads/avatars}")
    private String uploadDir;
    
    @Value("${app.upload.url:/uploads/avatars}")
    private String uploadUrl;

    /**
     * 检查文件上传目录是否存在且可写
     */
    @GetMapping("/check-dir")
    @ResponseBody
    public ResponseEntity<?> checkUploadDirectory() {
        Map<String, Object> result = new HashMap<>();
        result.put("uploadDir", uploadDir);
        result.put("uploadUrl", uploadUrl);
        
        File directory = new File(uploadDir);
        boolean exists = directory.exists();
        boolean canWrite = exists && directory.canWrite();
        
        result.put("directoryExists", exists);
        result.put("directoryCanWrite", canWrite);
        
        if (!exists) {
            boolean created = directory.mkdirs();
            result.put("directoryCreated", created);
            if (created) {
                result.put("directoryCanWrite", directory.canWrite());
            }
        }
        
        // 尝试创建一个测试文件
        if (canWrite || (exists && directory.canWrite())) {
            try {
                File testFile = new File(directory, "test_write_" + System.currentTimeMillis() + ".txt");
                boolean fileCreated = testFile.createNewFile();
                result.put("testFileCreated", fileCreated);
                if (fileCreated) {
                    boolean deleted = testFile.delete();
                    result.put("testFileDeleted", deleted);
                }
            } catch (Exception e) {
                result.put("error", e.getMessage());
            }
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 测试基本的文件写入和URL访问
     */
    @GetMapping("/test-write")
    @ResponseBody
    public ResponseEntity<?> testFileWrite() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建目录
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                result.put("directoryCreated", created);
            }
            
            // 创建测试文件
            String fileName = "test_file_" + System.currentTimeMillis() + ".txt";
            File testFile = new File(directory, fileName);
            boolean fileCreated = testFile.createNewFile();
            result.put("fileCreated", fileCreated);
            
            if (fileCreated) {
                // 记录文件路径和访问URL
                result.put("filePath", testFile.getAbsolutePath());
                result.put("fileUrl", uploadUrl + "/" + fileName);
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getName());
            result.put("stackTrace", e.getStackTrace()[0].toString());
        }
        
        return ResponseEntity.ok(result);
    }
} 