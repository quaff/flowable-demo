package com.example.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class TableExtractionHelperApp {

    public static void main(String[] args) throws Exception {
        Resource resource = new ClassPathResource("ccb.pdf");
        try (InputStream inputStream = resource.getInputStream()) {
            List<List<String>> result = TableExtractionHelper.extract(inputStream);
            for (List<String> row : result) {
                System.out.println(row);
            }
        }
    }

}