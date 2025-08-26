package com.docutools.fileprocessorservice.controller;

import com.docutools.fileprocessorservice.service.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Base path for all methods in this controller
public class FileApiController {

    private final ConversionService conversionService;

    public FileApiController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert/pdf-to-word")
    public ResponseEntity<Resource> handlePdfToWord(@RequestParam("file") MultipartFile file) {
        try {
            Path pdfPath = conversionService.saveFile(file);
            Path wordPath = conversionService.convertPdfToWord(pdfPath);
            Resource resource = loadFileAsResource(wordPath);

            Files.deleteIfExists(pdfPath); // Clean up original file

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/convert/word-to-pdf")
    public ResponseEntity<Resource> handleWordToPdf(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "false") boolean compress) {
        Path wordPath = null;
        Path pdfPath = null;
        Path finalPath = null;
        try {
            wordPath = conversionService.saveFile(file);
            pdfPath = conversionService.convertWordToPdf(wordPath);

            finalPath = pdfPath;
            if (compress) {
                finalPath = conversionService.compressPdf(pdfPath);
            }

            Resource resource = loadFileAsResource(finalPath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        } finally {
            try {
                if (wordPath != null) Files.deleteIfExists(wordPath);
                if (pdfPath != null && compress && finalPath != null && !pdfPath.equals(finalPath)) Files.deleteIfExists(pdfPath);
            } catch (IOException e) {
                e.printStackTrace(); // Log cleanup failure
            }
        }
    }

    @PostMapping("/convert/image-to-pdf")
    public ResponseEntity<Resource> handleImageToPdf(@RequestParam("files") List<MultipartFile> files, @RequestParam(defaultValue = "false") boolean compress) {
        try {
            List<MultipartFile> processedFiles;
            if (compress) {
                processedFiles = files.stream().map(file -> {
                    try {
                        byte[] compressedBytes = conversionService.compressImage(file, 0.5f); // 50% quality
                        return new CustomMultipartFile(file.getOriginalFilename(), file.getOriginalFilename(), file.getContentType(), compressedBytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
            } else {
                processedFiles = new ArrayList<>(files);
            }

            Path pdfPath = conversionService.convertImagesToPdf(processedFiles);
            Resource resource = loadFileAsResource(pdfPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted-images.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private Resource loadFileAsResource(Path path) throws MalformedURLException {
        return new UrlResource(path.toUri());
    }

    private static class CustomMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public CustomMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(Path dest) throws IOException { Files.write(dest, content); }
        @Override public void transferTo(File dest) throws IOException, IllegalStateException { Files.write(dest.toPath(), content);}
    }
}
