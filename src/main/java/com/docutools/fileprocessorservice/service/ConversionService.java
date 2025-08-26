package com.docutools.fileprocessorservice.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import net.coobird.thumbnailator.Thumbnails;
// Import the Loader class
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ConversionService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Path saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath);
        return filePath;
    }

    public Path convertPdfToWord(Path pdfPath) throws Exception {
        // --- THIS IS THE CORRECTED LINE ---
        try (PDDocument pdf = Loader.loadPDF(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdf);

            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
            wordPackage.getMainDocumentPart().addParagraphOfText(text);

            Path wordPath = Paths.get(uploadDir, pdfPath.getFileName().toString().replace(".pdf", ".docx"));
            wordPackage.save(wordPath.toFile());
            return wordPath;
        }
    }

    public Path convertWordToPdf(Path wordPath) throws Exception {
        try (InputStream is = new FileInputStream(wordPath.toFile())) {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(is);
            Path pdfPath = Paths.get(uploadDir, wordPath.getFileName().toString().replace(".docx", ".pdf"));
            try (OutputStream os = new FileOutputStream(pdfPath.toFile())) {
                Docx4J.toPDF(wordPackage, os);
                os.flush();
            }
            return pdfPath;
        }
    }

    public Path convertImagesToPdf(List<MultipartFile> imageFiles) throws IOException {
        Path pdfPath = Paths.get(uploadDir, UUID.randomUUID() + ".pdf");
        try (PDDocument document = new PDDocument()) {
            for (MultipartFile file : imageFiles) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, file.getBytes(), file.getOriginalFilename());

                // Scale image to fit page width
                float pageWidth = page.getMediaBox().getWidth() - 40; // with margin
                float pageHeight = page.getMediaBox().getHeight() - 40;
                float scale = Math.min(pageWidth / pdImage.getWidth(), pageHeight / pdImage.getHeight());
                float scaledWidth = pdImage.getWidth() * scale;
                float scaledHeight = pdImage.getHeight() * scale;

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 20, pageHeight - scaledHeight + 20, scaledWidth, scaledHeight);
                }
            }
            document.save(pdfPath.toFile());
        }
        return pdfPath;
    }

    public Path compressPdf(Path pdfPath) throws IOException {
        Path compressedPdfPath = Paths.get(uploadDir, "compressed_" + pdfPath.getFileName());
        PdfReader reader = new PdfReader(pdfPath.toString());
        PdfWriter writer = new PdfWriter(compressedPdfPath.toString(), new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();
        return compressedPdfPath;
    }

    public byte[] compressImage(MultipartFile imageFile, float quality) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(imageFile.getInputStream())
                    .scale(1.0)
                    .outputQuality(quality)
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        }
    }
}
