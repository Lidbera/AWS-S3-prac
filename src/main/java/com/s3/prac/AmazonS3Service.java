package com.s3.prac;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

@Service
public class AmazonS3Service {
	private static final Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);
	
	private AmazonS3 amazonS3;
	
	@Value("${s3.bucket}") private String bucket;
	@Value("${s3.access}") private String access;
	@Value("${s3.secret}") 	private String secret;
	
	public enum ImgPath {
		PROFILE("profile_img"),
		RECIPE("recipe_img"),
		UTIL("util_img");

		private String path;
		ImgPath(String path) {
			this.path = path;
		}
		public String getPath() {
			return "/" + path;
		}
	}
	
	public void init() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(access, secret);
		amazonS3 = new AmazonS3Client(awsCredentials);
		amazonS3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
	}
	
	public String uploadFile(MultipartFile file, ImgPath path) {
		if(amazonS3 == null) init();		
		if(file.isEmpty()) return null;
		
		String name = rename(file.getOriginalFilename());
		InputStream input = null;
		if(amazonS3 != null) {
			try {
				String bucketpath = bucket + path.getPath();
				input = file.getInputStream();
				byte[] bytes = IOUtils.toByteArray(input);
				ObjectMetadata metadata = new ObjectMetadata();
				metadata.setContentLength(bytes.length);
				ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
				
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketpath, name, stream, metadata);
				putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
				amazonS3.putObject(putObjectRequest);
			} catch (Exception e) {
				logger.warn("upload fail: [{}]", e.getStackTrace());
				return null;
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					logger.warn("input-close fail: [{}]", e.getStackTrace());
				}
			}
		}
		logger.info("upload success: [{}]", name);
		return name;
	}
	public List<String> uploadFiles(List<MultipartFile> files, ImgPath path) {
		List<String> nameList = new ArrayList<String>();
		for(MultipartFile file : files) {
			nameList.add(uploadFile(file, path));
		}
		return nameList;
	}
	
	private String rename(String base) {
		UUID rand = UUID.randomUUID();
		int ext_index = base.indexOf(".");
		String ext = base.substring(ext_index);
		base = base.substring(0, ext_index - 1);
		String name = String.format("%s_%s%s", base, rand, ext);
		return name;
	}
	
	public String getFilePath(String filename, ImgPath path) {
		if(amazonS3 == null) init();
		URL url = amazonS3.getUrl(bucket + path.getPath(), filename);
		if(url != null) logger.info("getpath success: [{}]", url);
		return url.toString();
	}
	
	public boolean deleteFile(String filename, ImgPath path) {
		if(amazonS3 == null) init();
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(bucket + path.getPath(), filename));
		} catch (Exception e) {
			logger.warn("delete fail: [{}]", e.getStackTrace());
			return false;
		}
		logger.info("delete success: [{}]", filename);
		return true;
	}
}