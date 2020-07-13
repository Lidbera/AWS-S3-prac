package com.s3.prac;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.s3.prac.AmazonS3Service.ImgPath;

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
	public String upload(MultipartFile file, HttpServletRequest req) {		
		if(file == null) {
			logger.info("file null");
			return "home";
		}
		String name = awsService.uploadFile(file, ImgPath.RECIPE);
		if(name != null) { 
			req.setAttribute("name", name);
			return "fin";
		}
		else return "home";	
	}
	
	@GetMapping("/getpath")
	public String path(String name) {
		if(name == null || name.length() <= 0) {
			logger.info("path name null");
			return "home";
		}
		String path = awsService.getFilePath(name, ImgPath.RECIPE);
		if(path != null) return "fin";
		else return "home";
	}
	
	@GetMapping("/delete")
	public String delete(String name) {
		if(name == null || name.length() <= 0) {
			logger.info("delete name null");
			return "home";
		}
		if(awsService.deleteFile(name, ImgPath.RECIPE)) return "fin";
		else return "home";
	}
	
	@PostMapping("/uploads")
	public void uploads(MultipartHttpServletRequest req) {
		int count = Integer.parseInt(req.getParameter("count"));
		
		List<MultipartFile> list = new ArrayList<MultipartFile>();
		for(int i = 0; i < count; i++) {
			MultipartFile file = req.getFile("file"+i);
			if(file != null) list.add(file);
		}
		if(list.size() == 0) {
			logger.info("file null");
		}
		List<String> names = awsService.uploadFiles(list, ImgPath.RECIPE);
		String log = "";
		for(String n : names) log += n + ", ";
		logger.info("uploaded files name: {}", log);
	}
	
	@GetMapping("/fin")
	public String fin() {
		return "fin";
	}
}
