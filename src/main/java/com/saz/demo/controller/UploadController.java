package com.saz.demo.controller;

import java.util.List;

import com.saz.demo.dto.ItemResponseDto;
import com.saz.demo.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UploadController {
	
	private final UploadService uploadService;
	
	public UploadController(UploadService uploadService) {
		this.uploadService = uploadService;
	}

	@PostMapping("/upload")
	public List<ItemResponseDto> upload(@RequestParam("file") MultipartFile file) throws Exception{
		return uploadService.upload(file);
	}
}
