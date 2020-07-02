package com.s3.prac;

import java.util.Locale;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Inject
	private	AmazonS3Service awsService;
	
	@GetMapping("/")
	public String home(Locale locale, Model model) {
		return "home";
	}
	
	@PostMapping("/upload")
	public String upload(MultipartFile file) {		
		if(file == null) {
			logger.info("file null");
			return "home";
		}
		if(awsService.uploadFile(file)) return "upload";
		else return "home";	
	}
	
	@GetMapping("/getpath")
	public String path(String name) {
		if(name == null || name.length() <= 0) {
			logger.info("path name null");
			return "home";
		}
		String path = awsService.getFilePath(name);
		if(path != null) return "upload";
		else return "home";
	}
	
	@GetMapping("/delete")
	public String delete(String name) {
		if(name == null || name.length() <= 0) {
			logger.info("delete name null");
			return "home";
		}
		if(awsService.deleteFile(name)) return "upload";
		else return "home";
	}
}
