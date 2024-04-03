package com.example.myapplication.service;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class OCREvaluationService {

    @Autowired
    private Tesseract tesseract;

    @Autowired
    private TesseractOCRService tesseractOCRService;


    public String evaluateOCR() throws IOException {

        // Load dataset and ground truth
        List<File> imageFiles = loadDataset();
        List<String> groundTruths = loadGroundTruths();
        List<String> ocrResults = new ArrayList<>();
        System.out.print(imageFiles.size());

        if (imageFiles.size() != groundTruths.size()) {
            System.out.println("Error: Dataset size does not match ground truth size.");
        } else {

            int totalImages = imageFiles.size();
            int totalLevenshteinDistance = 0;

            // Iterate through each image in the dataset and perform OCR
            for (int i = 0; i < totalImages; i++) {
                File imageFile = imageFiles.get(i);
                String gT = groundTruths.get(i);

                // Perform OCR on the image
                String resultText = tesseractOCRService.recognizeText(Files.newInputStream(imageFile.toPath()), "eng");
                resultText = resultText.trim();
                //resultText = resultText.replace("\n", "");
                ocrResults.add(resultText);

                // Calculate Levenshtein Distance
                int distance = levenshteinDistance(resultText, gT);
                System.out.println("Input image text is: " + gT + "  DistanceResult is: "+ distance);
                totalLevenshteinDistance += distance;
            }
            try {
                // Calculate accuracy
                double charAccuracy= calculateAccuracy(groundTruths,ocrResults);

                // Average Levenshtein Distance
                double averageLevenshteinDistance = (double) totalLevenshteinDistance / totalImages;


                //Precision
                double precision = calculatePrecision(groundTruths,ocrResults);

                //recall
                double recall = calculateRecall(groundTruths,ocrResults);

                //f1 score
                double f1score = calculateF1Score(precision,recall);


                return "accuracy: " + charAccuracy + "\nAverage distance: " + averageLevenshteinDistance + "\nprecision: "+ precision+ "\nrecall: "+recall+ "\nF1score: "+f1score ;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "failed to calculate the evaluation metrics";

    }

    private double calculateF1Score(double precision, double recall) {
        return 2 * (precision * recall) / (precision + recall);
    }

    private double calculateRecall(List<String> groundTruths, List<String> ocrResults) {
        int truePositives = 0;
        int falseNegatives = 0;

        for (int i = 0; i < groundTruths.size(); i++) {
            String groundTruth = groundTruths.get(i);
            String ocrResult = ocrResults.get(i);

            for (int j = 0; j < groundTruth.length(); j++) {
                if (j < ocrResult.length() && groundTruth.charAt(j) == ocrResult.charAt(j)) {
                    truePositives++;
                } else {
                    falseNegatives++;
                }
            }
        }

        return (double) truePositives / (truePositives + falseNegatives);
    }

    private double calculatePrecision(List<String> groundTruths, List<String> ocrResults) {
        int truePositives = 0;
        int falsePositives = 0;

        for (int i = 0; i < groundTruths.size(); i++) {
            String groundTruth = groundTruths.get(i);
            String ocrResult = ocrResults.get(i);

            for (int j = 0; j < groundTruth.length(); j++) {
                if (j < ocrResult.length() && groundTruth.charAt(j) == ocrResult.charAt(j)) {
                    truePositives++;
                } else {
                    falsePositives++;
                }
            }
        }

        return (double) truePositives / (truePositives + falsePositives);
    }

    private double calculateAccuracy(List<String> groundTruths, List<String> ocrResults) {
        int totalCharacters = 0;
        int correctlyRecognizedCharacters = 0;

        for (int i = 0; i < groundTruths.size(); i++) {
            String groundTruth = groundTruths.get(i);
            String ocrResult = ocrResults.get(i);

            totalCharacters += groundTruth.length();

            for (int j = 0; j < groundTruth.length(); j++) {
                if (j < ocrResult.length() && groundTruth.charAt(j) == ocrResult.charAt(j)) {
                    correctlyRecognizedCharacters++;
                }
            }
        }

        return (double) correctlyRecognizedCharacters / totalCharacters;
    }


    private int levenshteinDistance(String resultText, String gT) {
        // Calculate Levenshtein Distance between result text and ground truth
        int[][] dp = new int[resultText.length() + 1][gT.length() + 1];
        for (int i = 0; i <= resultText.length(); i++) {
            for (int j = 0; j <= gT.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (resultText.charAt(i - 1) == gT.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i][j - 1], dp[i - 1][j]), dp[i - 1][j - 1]);
                }
            }
        }
        return dp[resultText.length()][gT.length()];
    }

    private List<String> loadGroundTruths() throws IOException {
        // Load ground truth labels from a file or database, for example
        return Files.readAllLines(new File("src/main/resources/groundTruth.txt").toPath());
    }

    private List<File> loadDataset() throws IOException {
        // Load images from a directory, for example
        return Files.walk(new File("src/main/resources/testimages").toPath())
                .filter(Files::isRegularFile)
                .map(java.nio.file.Path::toFile)
                .toList();
    }


}
