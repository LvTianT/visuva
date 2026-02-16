package com.example.picturemaster.controller;

import com.example.picturemaster.annotation.AuthCheck;
import com.example.picturemaster.common.BaseResponse;
import com.example.picturemaster.common.ResultUtils;
import com.example.picturemaster.constant.UserConstant;
import com.example.picturemaster.exception.BusinessException;
import com.example.picturemaster.exception.ErrorCode;
import com.example.picturemaster.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.transfer.MultipleFileUpload;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

  @Resource
  private CosManager cosManager;
    /*
       测试文件上传
    */
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile){
       String filename =  multipartFile.getOriginalFilename();
       String filepath = String.format("/test/%s",filename);
       File file = null;
        try {
            if (filepath == null || filepath.isEmpty()) {
                throw new IllegalArgumentException("Filepath cannot be null or empty");
            }

            // 创建临时文件
            file = File.createTempFile("temp", null);
            // 将multipartFile内容复制到临时文件
            Files.copy(multipartFile.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 上传文件
            cosManager.putObject(filepath, file);

            // 返回可访问的地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
          log.error("file upload error,filepath = " + filepath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        }finally {
          if(file != null){
            //删除临时文件
            boolean delete = file.delete();
            if(!delete){
              log.error("file delete error,filepath = {}" , filepath);
            }
          }
        }
    }

    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
   @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 释放流
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
        }
    }


}
