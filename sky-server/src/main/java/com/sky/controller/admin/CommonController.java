package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

	@Autowired
	private AliOssUtil aliOssUtil;

	/**
	 * 文件上传
	 *
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	@ApiOperation("文件上传")
	public Result<String> upload(MultipartFile file) {  // 参数名与前端提交的参数名一致
		log.info("文件上传：{}", file);

		try {
			// 生成 UUID 的唯一文件名: 原文件名 -> 获取后缀 -> UUID + 文件后缀
			String originalFilename = file.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			String name = UUID.randomUUID().toString() + extension;  // 随机生成文件名

			// 返回文件在oss中的位置
			String filePath = aliOssUtil.upload(file.getBytes(), name);  // 文件上传: 文件字节数组, 文件名

			return Result.success(filePath);
		} catch (IOException e) {
			log.error("文件上传失败：{}", e.getMessage());
		}
		return Result.error(MessageConstant.UPLOAD_FAILED);
	}
}
