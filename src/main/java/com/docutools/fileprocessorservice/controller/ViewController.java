package com.docutools.fileprocessorservice.controller;

import com.docutools.fileprocessorservice.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ViewController {

    private final ConversionService conversionService;

    @Autowired
    public ViewController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Handles GET requests to the root URL ("/") and displays the main upload page.
     * @return The name of the HTML template to render ("index").
     */
    @GetMapping("/")
    public String showUploadForm() {
        return "index";
    }

    /**
     * Handles the file upload form submission. It processes the file and returns
     * the result to the same page.
     * @param file The uploaded file from the form.
     * @param model The model to add attributes to for the view.
     * @return The name of the HTML template to render ("index").
     */
    @PostMapping("/upload-and-process")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("errorMessage", "Please select a file to process.");
            return "index";
        }

        try {
            String extractedText = conversionService.extractText(file);
            model.addAttribute("extractedText", extractedText);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error processing file: " + e.getMessage());
        }

        return "index";
    }
}