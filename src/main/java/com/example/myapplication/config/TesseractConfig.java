package com.example.myapplication.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import net.sourceforge.tess4j.util.PdfUtilities;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.Utils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

@Configuration
public class TesseractConfig {

    @Bean
    Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("/usr/local/share/tessdata/");
        tesseract.setLanguage("mya");
        tesseract.setPageSegMode(1); // Set page segmentation mode
        tesseract.setOcrEngineMode(1); // Set OCR engine mode (1 for LSTM, 3 for both LSTM and Legacy)
        System.out.println("TEST: "+tesseract);
        return tesseract;
    }
}
