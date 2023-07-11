package com.tadashop.nnt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
	private String uploadLogoDir;
	
	private String uploadProductImageDir;
	
}
