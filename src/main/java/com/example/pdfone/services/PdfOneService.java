package com.example.pdfone.services;


import com.gnostice.core.DocumentStudioException;
import com.gnostice.core.digitizationengine.DigitizationMode;
import com.gnostice.core.digitizationengine.RecognizeElementTypes;
import com.gnostice.documents.ConverterDigitizerSettings;
import com.gnostice.documents.ConverterException;
import com.gnostice.documents.DocumentConverter;
import com.gnostice.documents.FormatNotSupportedException;
import com.gnostice.pdfone.PdfDocument;
import com.gnostice.pdfone.PdfException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log4j2
@Service
public class PdfOneService {

    @Value("${file.upload-dir}")
    String tempPath;

// Trial key
//    static {
//        // Activate the product
//        Framework.activate("G23VRW:34KEOVT:5PVZ16V:D9OV8",
//                "GNE8RI6Z:45019ERAE:7ERFGQGR6:VTOR10");
//    }

    public String doExtract(String filename) {

        File tp = new File(tempPath);

        if (!tp.exists()) {
            throw new RuntimeException("Temp directory is not found.");
        }

        log.info("The Extracting text process is about to begin.");

        File inputFileName = new File(tp.getAbsolutePath() + "/" + filename);

        String out = inputFileName.getName().replace("pdf", "txt");
        File outputFilename = new File(out);

        log.info("input={}", inputFileName);
        log.info("output={}", outputFilename);

        try {

            if (inputFileName.exists()) {

                FileOutputStream fos = new FileOutputStream(out);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);

                // Load a PDF document
                PdfDocument doc = new PdfDocument();
                doc.load(inputFileName.getAbsoluteFile());

                // Extract text from page 1 of the document
                // and save it to the file writer
                log.info("Extracting text from PDF");
                doc.saveAsText(1, osw);
                log.info("The extraction text process has been done.");
                osw.close();

                // Close the PDF document
                doc.close();

                // read and get all extracted content text;
                FileInputStream fi = new FileInputStream(outputFilename);
                byte[] extrated = fi.readAllBytes();
                fi.close();

                // delete the temporary file
                inputFileName.delete();
                outputFilename.delete();

                return new String(extrated);
            }
        } catch (PdfException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private void setEnv(String key, String value) {
        try {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }

    public void convertToSearchabe(String filename) {

        setEnv("TESSDATA_PREFIX", "C:\\tesseract\\tessdata");

        File tp = new File(tempPath);

        if (!tp.exists()) {
            throw new RuntimeException("Temp directory is not found.");
        }

        if (!filename.endsWith(".pdf")) {
            throw new RuntimeException("Invalid type, just PDF file is allowed.");
        }

        log.info("The Convertion process is about to begin.");

        File inputFileName = new File(tp.getAbsolutePath() + "/" + filename);

        int i = filename.indexOf(".");

        String temp = filename.substring(0, i) + "_out.pdf";
        File outputFilename = new File(tp.getAbsolutePath() + "/" + temp);

        log.info("Output={}", outputFilename.getAbsolutePath());

        // Create a converter instance
        DocumentConverter dc = new DocumentConverter();

        // Change digitizer settings to recognize text from image data
        ConverterDigitizerSettings cds = dc.getPreferences().getDigitizerSettings();
        cds.setDigitizationMode(DigitizationMode.ALL_IMAGES);
        cds.setRecognizeElementTypes(RecognizeElementTypes.TEXT);

        try {
            // Convert an image or scanned-PDF to PDF and digitize any text in it
            dc.convertToFile(
                    inputFileName.getAbsolutePath(),
                    outputFilename.getAbsolutePath());
        } catch (FormatNotSupportedException | ConverterException e) {
            e.printStackTrace();
        } catch (DocumentStudioException e) {
            throw new RuntimeException(e);
        }

    }

}
