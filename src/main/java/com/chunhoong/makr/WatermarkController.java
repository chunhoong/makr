package com.chunhoong.makr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/watermark")
public class WatermarkController {

    private final WatermarkService watermarkService;

    @Autowired
    public WatermarkController(WatermarkService watermarkService) {
        this.watermarkService = watermarkService;
    }

    @PostMapping(produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> addWatermark(
            @RequestParam("pdfFile") MultipartFile file,
            @RequestParam("watermarkText") String watermarkText,
            @RequestParam("fontSize") int fontSize) throws IOException {
        File input = File.createTempFile("input.", ".pdf");
        file.transferTo(input);

        File output = watermarkService.addWatermark(input, watermarkText, fontSize);

        HttpHeaders headers = new HttpHeaders();
        headers.set("content-disposition", "inline; filename=" + file.getOriginalFilename());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(Files.readAllBytes(output.toPath()), headers, HttpStatus.OK);
    }

}
