package com.gyjian;

public class Main {
    public static final String SRC = "E:\\test.pdf";
    public static final String DEST = "results";

    public static void main(String[] args) throws Exception {
        new PdfImageExtractor().getImagesFromPDF(SRC, DEST);
    }
}