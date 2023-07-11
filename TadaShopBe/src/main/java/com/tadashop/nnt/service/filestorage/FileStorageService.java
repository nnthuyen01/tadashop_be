package com.tadashop.nnt.service.filestorage;



import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tadashop.nnt.config.FileStorageProperties;
import com.tadashop.nnt.dto.UploadedFileInfo;
import com.tadashop.nnt.exception.FileNotFoundException;
import com.tadashop.nnt.exception.FileStorageException;


@Service
public class FileStorageService {
	private final Path fileLogoStorageLocation;
	
	private final Path fileProductImageStorageLocation;
	
	public FileStorageService(FileStorageProperties fileStorageProperties) {
		this.fileLogoStorageLocation = Paths.get(fileStorageProperties.getUploadLogoDir())
				.toAbsolutePath().normalize();
		this.fileProductImageStorageLocation = Paths.get(fileStorageProperties.getUploadProductImageDir())
				.toAbsolutePath().normalize();
		
		try {
			Files.createDirectories(fileLogoStorageLocation);

			Files.createDirectories(fileProductImageStorageLocation);
		}
		catch ( Exception ex) {
			throw new FileStorageException("Cound not create the directory where the uploaded files will be stored", ex);
		}
	}
	
	
//	Luu thong tin logo
	public String storeLogoFile(MultipartFile file) {
		return storeFile(fileLogoStorageLocation, file);
	}
	
	public String storeProductImageFile(MultipartFile file) {
		return storeFile(fileProductImageStorageLocation, file);
	}
	
	public UploadedFileInfo storeUploadedProductImageFile(MultipartFile file) {
		return storeUploadedFile(fileProductImageStorageLocation, file);
	}
	
//	Luu thong tin duoc upload (vi tri luu tru, file up load) 
	private String storeFile(Path location, MultipartFile file) {
//		Sinh ra chuoi ngau nhien de dat ten file
		UUID uuid = UUID.randomUUID();
		
//		Lay phan mo rong cua file de tao ra file moi, nham muc dich tranh trung ten file
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String filename = uuid.toString() + "." + ext;
		
		try {
			if(filename.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
			}
			
			// Bo sung vao location vi tri luu de tao ra duong dan file day du
			Path targetLocation = location.resolve(filename); 
			// Dua file vao location
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			// Tra ve ten file
			return filename;
		} catch (Exception ex) {
			throw new FileStorageException("Cound not store file "+ filename + ". Please try again!", ex);
		}
	}
	
	
	private UploadedFileInfo storeUploadedFile(Path location, MultipartFile file) {
//		Sinh ra chuoi ngau nhien de dat ten file
		UUID uuid = UUID.randomUUID();
		
//		Lay phan mo rong cua file de tao ra file moi, nham muc dich tranh trung ten file
		String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String filename = uuid.toString() + "." + ext;
		
		try {
			if(filename.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + filename);
			}
			
			// Bo sung vao location vi tri luu de tao ra duong dan file day du
			Path targetLocation = location.resolve(filename); 
			// Dua file vao location
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			
			UploadedFileInfo info = new UploadedFileInfo();
			info.setFileName(filename);
			info.setUid(uuid.toString());
			info.setName(StringUtils.getFilename(file.getOriginalFilename()));
			
			
			// Tra ve ten file
			return info;
		} catch (Exception ex) {
			throw new FileStorageException("Cound not store file "+ filename + ". Please try again!", ex);
		}
	}
	
	
//	Nap logo len client
	public Resource loadLogoFileAsResource(String filename) {
		return loadFileAsResoure(fileLogoStorageLocation, filename);
	}
	
	public Resource loadLogoProductImageFileAsResource(String filename) {
		return loadFileAsResoure(fileProductImageStorageLocation, filename);
	}
	
//	Doc file duoc luu tru phia server va tra ve duoi dang resoure
	private Resource loadFileAsResoure(Path location, String filename) {
		try {
			Path filePath = location.resolve(filename).normalize();
			
			Resource resource = new UrlResource(filePath.toUri());
			
			if (resource.exists()) {
				return resource;
				
			} else {
				throw new FileNotFoundException("File not found " + filename);
			}
		} catch (Exception ex) {
			throw new FileNotFoundException("File not found " + filename, ex);
		}
	}
	
	
	public void deleteLogoFile(String filename) {
		deleteFile(fileLogoStorageLocation, filename);
	}
	
	public void deleteProductImageFile(String filename) {
		deleteFile(fileProductImageStorageLocation, filename);
	}
	
//	Xoa file
	private void deleteFile(Path location, String filename) {
		try {
			Path filePath = location.resolve(filename).normalize();
			
			if (!Files.exists(filePath)) {
				throw new FileNotFoundException("File not found " + filename);
			}
			Files.delete(filePath);
		} catch (Exception ex) {
			throw new FileNotFoundException("File not found " + filename, ex);
		}
	}
	
}
