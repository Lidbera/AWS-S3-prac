package com.s3.prac;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

@Service
public class AmazonS3Service {
	private static final Logger logger = LoggerFactory.getLogger(AmazonS3Service.class);
	
	private AmazonS3 amazonS3;
	
	@Value("${s3.bucket}") private String bucket;
	@Value("${s3.access}") private String access;
	@Value("${s3.secret}") 	private String secret;
	@Value("${s3.img_path}") 	private String img_path;
	
	public void init() {
		AWSCredentials awsCredentials = new BasicAWSCredentials(access, secret);
		amazonS3 = new AmazonS3Client(awsCredentials);
		amazonS3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
	}
	
	public boolean uploadFile(MultipartFile file) {
		if(amazonS3 == null) init();		
		if(file.isEmpty()) return false;
		
		String name = rename(file.getOriginalFilename());
		InputStream input = null;
		if(amazonS3 != null) {
			try {
				String bucketpath = bucket + img_path;
				input = file.getInputStream();
				ObjectMetadata metadata = new ObjectMetadata();
				
				PutObjectRequest putObjectRequest = new PutObjectRequest(bucketpath, name, input, metadata);
				putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead); // file permission
				amazonS3.putObject(putObjectRequest); // upload file
			} catch (Exception e) {
				logger.warn("upload fail: [{}]", e.getStackTrace());
				return false;
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					logger.warn("input-close fail: [{}]", e.getStackTrace());
				}
			}
		}
		logger.info("upload success: [{}]", name);
		return true;
	}
	private String rename(String base) {
		UUID rand = UUID.randomUUID();
		int ext_index = base.indexOf(".");
		String ext = base.substring(ext_index);
		base = base.substring(0, ext_index - 1);
		String name = String.format("%s_%s%s", base, rand, ext);
		return name;
	}
	
	public String getFilePath(String filename) {
		if(amazonS3 == null) init();
		URL path = amazonS3.getUrl(bucket + img_path, filename);
		if(path != null) logger.info("getpath success: [{}]", path);
		return path.toString();
	}
	
	public boolean deleteFile(String filename) {
		if(amazonS3 == null) init();
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(bucket + img_path, filename));
		} catch (Exception e) {
			logger.warn("delete fail: [{}]", e.getStackTrace());
			return false;
		}
		logger.info("delete success: [{}]", filename);
		return true;
	}
}