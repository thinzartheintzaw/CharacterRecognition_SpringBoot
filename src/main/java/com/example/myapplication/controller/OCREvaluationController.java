package com.example.myapplication.controller;


import com.example.myapplication.service.OCREvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class OCREvaluationController {

    @Autowired
    private OCREvaluationService ocrEvaluationService;

    @PostMapping("/evaluate-ocr")
    public String evaluateOCR() throws IOException {
        return ocrEvaluationService.evaluateOCR();
    }

}
