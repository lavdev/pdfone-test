package com.example.pdfone.controller;

import com.example.pdfone.domain.UploadFileResponse;
import com.example.pdfone.services.FileStorageService;
import com.example.pdfone.services.PdfOneService;
import com.example.pdfone.util.MD5;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@CrossOrigin(origins = "*")
@RequestMapping(path = {"/api/v1/test"}, produces = APPLICATION_JSON_VALUE)
@RestController
public class DemoController {

    private final FileStorageService fileStorageService;
    private final PdfOneService pdfOneService;

    @Autowired
    public DemoController(FileStorageService fileStorageService, PdfOneService pdfOneService) {
        this.fileStorageService = fileStorageService;
        this.pdfOneService = pdfOneService;
    }

    @GetMapping("/ping")
    ResponseEntity<?> ping() {
        return ResponseEntity.ok("The server is running");
    }

    @PostMapping("/extract")
    ResponseEntity<?> extract(@RequestParam("file") MultipartFile file) throws IOException {

        log.info("Multipart={}", file.getOriginalFilename());

        if (file.getBytes().length == 0) {
            return ResponseEntity.badRequest()
                    .body("File length equal zero, is not allowed.");
        }

        String code = MD5.getMd5(Objects.requireNonNull(file.getOriginalFilename()));

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/" + code + "/")
                .path(file.getName()).toUriString();

        UploadFileResponse uploadFileResponse =
                new UploadFileResponse(file.getName(), fileDownloadUri, file.getContentType(), file.getSize(), file.getBytes());

        if (fileStorageService.storeFile(file) != null) {
            String text = this.pdfOneService.doExtract(file.getOriginalFilename());
            uploadFileResponse.setPlainText(text);
        }

        return ResponseEntity.ok(uploadFileResponse);
    }

    @PostMapping("/convert")
    ResponseEntity<?> convert(@RequestParam("file") MultipartFile file) throws IOException {

        log.info("Multipart={}", file.getOriginalFilename());

        if (file.getBytes().length == 0) {
            return ResponseEntity.badRequest()
                    .body("File length equal zero, is not allowed.");
        }

        String code = MD5.getMd5(Objects.requireNonNull(file.getOriginalFilename()));

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/" + code + "/")
                .path(file.getName()).toUriString();

        UploadFileResponse uploadFileResponse =
                new UploadFileResponse(file.getName(), fileDownloadUri, file.getContentType(), file.getSize(), file.getBytes());

        if (fileStorageService.storeFile(file) != null) {
            this.pdfOneService.convertToSearchabe(file.getOriginalFilename());
        }

        return ResponseEntity.ok(uploadFileResponse);
    }
}
