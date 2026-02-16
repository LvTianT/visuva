package com.example.picturemaster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.picturemaster.mapper")
public class PictureMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PictureMasterApplication.class, args);
	}

}
