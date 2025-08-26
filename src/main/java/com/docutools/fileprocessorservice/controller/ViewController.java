package com.docutools.fileprocessorservice.controller;

import com.docutools.fileprocessorservice.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ViewController {

    private final ConversionService conversionService;

    @Autowired
    public ViewController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/convert/docx-to-pdf")
    public String handleDocxToPdf(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            String outputFilename = conversionService.convertDocxToPdf(file);
            redirectAttributes.addFlashAttribute("downloadUrl", "/api/files/download/" + outputFilename);
            redirectAttributes.addFlashAttribute("originalFilename", outputFilename);
            redirectAttributes.addFlashAttribute("message", "DOCX to PDF conversion successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error converting DOCX to PDF: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/convert/pdf-to-docx")
    public String handlePdfToDocx(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            String outputFilename = conversionService.convertPdfToDocx(file);
            redirectAttributes.addFlashAttribute("downloadUrl", "/api/files/download/" + outputFilename);
            redirectAttributes.addFlashAttribute("message", "PDF to DOCX conversion successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error converting PDF to DOCX: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/convert/images-to-pdf")
    public String handleImagesToPdf(@RequestParam("files") List<MultipartFile> files,
                                    @RequestParam(value = "compress", required = false) boolean compress,
                                    RedirectAttributes redirectAttributes) {
        try {
            String outputFilename = conversionService.convertImagesToPdf(files, compress);
            redirectAttributes.addFlashAttribute("downloadUrl", "/api/files/download/" + outputFilename);
            redirectAttributes.addFlashAttribute("originalFilename", outputFilename);
            redirectAttributes.addFlashAttribute("message", "Images merged to PDF successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error merging images: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/compress/{filename:.+}")
    public String handlePdfCompression(@PathVariable String filename, RedirectAttributes redirectAttributes) {
        try {
            String compressedFilename = conversionService.compressPdf(filename);
            redirectAttributes.addFlashAttribute("downloadUrl", "/api/files/download/" + compressedFilename);
            redirectAttributes.addFlashAttribute("message", "PDF compressed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error compressing PDF: " + e.getMessage());
        }
        return "redirect:/";
    }
}