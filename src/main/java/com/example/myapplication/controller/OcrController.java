package com.example.myapplication.controller;


import com.example.myapplication.service.TesseractOCRService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class OcrController {


    @Autowired
    private TesseractOCRService tesseractOCRService;

    @PostMapping("/ocr")
    public String recognizeText(@RequestParam("image") MultipartFile image,
                                @RequestParam("language") String language) throws IOException {
        return tesseractOCRService.recognizeText(image.getInputStream(), language);
    }

}
