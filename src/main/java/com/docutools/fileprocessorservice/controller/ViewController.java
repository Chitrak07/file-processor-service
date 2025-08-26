package com.docutools.fileprocessorservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ViewController {

    /**
     * Handles the request for the main home page.
     * @return The name of the index.jsp view.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Handles the request for the PDF to Word converter page.
     * @return The name of the pdf-to-word.jsp view.
     */
    @GetMapping("/pdf-to-word")
    public String pdfToWordPage() {
        return "pdf-to-word";
    }

    /**
     * Handles the request for the Word to PDF converter page.
     * @return The name of the word-to-pdf.jsp view.
     */
    @GetMapping("/word-to-pdf")
    public String wordToPdfPage() {
        return "word-to-pdf";
    }

    /**
     * Handles the request for the Image to PDF converter page.
     * @return The name of the image-to-pdf.jsp view.
     */
    @GetMapping("/image-to-pdf")
    public String imageToPdfPage() {
        return "image-to-pdf";
    }

    /**
     * A simple test endpoint to verify the controller is working.
     * If you can access /ping, the controller is correctly mapped.
     * @return A simple string "Pong!".
     */
    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "Pong! The ViewController is working.";
    }
}
