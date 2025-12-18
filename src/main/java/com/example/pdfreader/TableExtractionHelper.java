package com.example.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class TableExtractionHelper {

    @SuppressWarnings("rawtypes")
    public static List<List<String>> extract(InputStream inputStream) throws IOException {
        List<List<String>> result = new ArrayList<>();
        try (PDDocument document = PDDocument.load(inputStream)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            PageIterator pageIterator = extractor.extract();
            List<String> header = null;
            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                SpreadsheetExtractionAlgorithm algorithm = new SpreadsheetExtractionAlgorithm();
                List<Table> tables = algorithm.extract(page);
                for (Table table : tables) {
                    for (List<RectangularTextContainer> row : table.getRows()) {
                        List<String> list = new ArrayList<>();
                        for (RectangularTextContainer cell : row) {
                            list.add(cell.getText());
                        }
                        if (!isValidRow(list)) {
                            continue;
                        }
                        if (isColumnar(list)) {
                            List<List<String>> expanded = expandRow(list);
                            if (header == null) {
                                header = expanded.get(0);
                            } else {
                                expanded.remove(header);
                            }
                            result.addAll(expanded);
                        } else {
                            list = list.stream().map(s -> s.replaceAll("\\s", "")).toList();
                            if (header == null) {
                                header = list;
                                result.add(header);
                            } else if (!header.equals(list)) {
                                result.add(list);
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    private static List<List<String>> expandRow(List<String> row) {
        List<String[]> list = row.stream().map(string -> string.split("\\r")).toList();
        int length = list.get(0).length;
        List<List<String>> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            List<String> newRow = new ArrayList<>();
            for (String[] array : list) {
                newRow.add(i < array.length ? array[i] : "");
            }
            result.add(newRow);
        }
        return result;
    }

    private static boolean isColumnar(List<String> row) {
        int length = row.get(0).split("\\r").length;
        if (length < 2) {
            return false;
        }
        return row.stream().skip(1).filter(s -> !s.isEmpty()).allMatch(s -> s.split("\\r").length >= length);
    }

    private static boolean isValidRow(List<String> row) {
        if (row.size() < 2) {
            return false;
        }
        return !row.stream().skip(2).allMatch(String::isEmpty);
    }
}