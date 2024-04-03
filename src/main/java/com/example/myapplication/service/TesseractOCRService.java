package com.example.myapplication.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


@Service
public class TesseractOCRService {

    @Autowired
    private Tesseract tesseract;

    public static BufferedImage convertToGrayscale(BufferedImage original) {
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = grayscale.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    public static BufferedImage applyGaussianBlur(BufferedImage original) {
        BufferedImage blurred = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = blurred.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return blurred;
    }

    public static BufferedImage applyThresholding(BufferedImage original) {
        // Dummy thresholding, replace with your actual implementation
        BufferedImage thresholded = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = thresholded.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return thresholded;
    }

    public BufferedImage convertToLuminosity(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage luminosityImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int luminosity = (int) (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue());
                luminosityImage.setRGB(x, y, new Color(luminosity, luminosity, luminosity).getRGB());
            }
        }

        return luminosityImage;
    }

    public BufferedImage enhanceDPI(BufferedImage originalImage, int targetDPI) {
        double scaleFactor = targetDPI / 72.0; // Assuming the original DPI is 72
        int newWidth = (int) (originalImage.getWidth() * scaleFactor);
        int newHeight = (int) (originalImage.getHeight() * scaleFactor);

        BufferedImage enhancedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = enhancedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return enhancedImage;
    }


    public String recognizeText(InputStream inputStream, String language) throws IOException {
        tesseract.setLanguage(language);
        BufferedImage image = ImageIO.read(inputStream);
        // Enhance DPI
        int targetDPI = 300; // You can adjust this value based on your requirements
        BufferedImage enhancedImage = enhanceDPI(image, targetDPI);
        // Convert to grayscale
        BufferedImage grayscaleImage = convertToGrayscale(enhancedImage);
//        // Convert to luminosity
//        BufferedImage luminosityImage = convertToLuminosity(enhancedImage);

        // Apply Gaussian blur
        BufferedImage blurredImage = applyGaussianBlur(grayscaleImage);

        // Apply thresholding
        BufferedImage thresholdedImage = applyThresholding(blurredImage);

        try {
            String outputText = tesseract.doOCR(thresholdedImage);
            System.out.println(outputText);
            FileWriter fileWriter = new FileWriter("src/main/resources/img.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(outputText);
            bufferedWriter.close();
            System.out.println("Text has been written to the file successfully.");
            return outputText;


        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return "failed";

    }


}

