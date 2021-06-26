package com.chunhoong.makr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PdfController {

    private final WatermarkService watermarkService;
    private final MergerService mergerService;

    @Autowired
    public PdfController(WatermarkService watermarkService, MergerService mergerService) {
        this.watermarkService = watermarkService;
        this.mergerService = mergerService;
    }

    @PostMapping(value = "/watermark", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> addWatermark(
            @RequestParam("pdfFile") MultipartFile multipartFile,
            @RequestParam("watermarkText") String watermarkText,
            @RequestParam("fontSize") int fontSize) throws IOException {
        File watermarkedFile = watermarkService.addWatermark(toFile(multipartFile), watermarkText, fontSize);
        return servePDF(watermarkedFile, multipartFile.getOriginalFilename());
    }

    @PostMapping(value = "/merge", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> merge(@RequestParam("pdfFiles") MultipartFile[] multipartFiles,
                                        @RequestParam("fileName") String fileName) throws IOException {
        List<File> files = Arrays.stream(multipartFiles)
                .map(this::toFile)
                .collect(Collectors.toList());

        return servePDF(mergerService.mergeFiles(files), fileName(fileName));
    }

    @PostMapping(value = "/mergeAndWatermark", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> mergeAndWatermark(
            @RequestParam("pdfFiles") MultipartFile[] multipartFiles,
            @RequestParam("fileName") String fileName,
            @RequestParam("watermarkText") String watermarkText,
            @RequestParam("fontSize") int fontSize
    ) throws IOException {
        List<File> files = Arrays.stream(multipartFiles)
                .map(this::toFile)
                .collect(Collectors.toList());

        File mergedFiles = mergerService.mergeFiles(files);
        File mergedAndWatermarkedFiles = watermarkService.addWatermark(mergedFiles, watermarkText, fontSize);

        return servePDF(mergedAndWatermarkedFiles, fileName(fileName));
    }

    private ResponseEntity<byte[]> servePDF(File pdfFile, String fileName) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-disposition", "inline; filename=" + fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(Files.readAllBytes(pdfFile.toPath()), headers, HttpStatus.OK);
    }

    private File toFile(MultipartFile multipartFile) {
        try {
            File file = File.createTempFile("input.", ".pdf");
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String fileName(String fileName) {
        if (!fileName.endsWith(".pdf")) {
            fileName = fileName + ".pdf";
        }
        return fileName;
    }
}
