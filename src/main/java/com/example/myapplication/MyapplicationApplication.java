package com.example.myapplication;

import com.example.myapplication.service.TesseractOCRService;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class MyapplicationApplication {


	public static void main(String[] args) throws TelegramApiException {
		System.setProperty("TESSDATA_PREFIX", "/usr/local/share/tessdata/");
		System.setProperty("jna.library.path", "/usr/local/lib/");
		SpringApplication.run(MyapplicationApplication.class, args);


	}

}
