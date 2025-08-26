package com.docutools.fileprocessorservice.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import net.coobird.thumbnailator.Thumbnails;
import org.docx4j.Docx4J;
import org.docx4j.convert.in.Docx4j_getDocx;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
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

    private final Path generatedFileDir;

    public ConversionService() {
        this.generatedFileDir = Paths.get("generated-files");
        try {
            if (!Files.exists(generatedFileDir)) {
                Files.createDirectories(generatedFileDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create generated file directory", e);
        }
    }

    public String convertDocxToPdf(MultipartFile file) throws Exception {
        String baseFilename = getBaseFilename(file.getOriginalFilename());
        String outputFilename = baseFilename + ".pdf";
        Path outputPath = this.generatedFileDir.resolve(outputFilename);

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(file.getInputStream());
        try (OutputStream os = new FileOutputStream(outputPath.toFile())) {
            Docx4J.toPDF(wordMLPackage, os);
        }
        return outputFilename;
    }

    public String convertPdfToDocx(MultipartFile file) throws Exception {
        String baseFilename = getBaseFilename(file.getOriginalFilename());
        String outputFilename = baseFilename + ".docx";
        Path outputPath = this.generatedFileDir.resolve(outputFilename);

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        // THE FIX IS HERE: Using the correct class name "Docx4j_getDocx"
        Docx4j_getDocx converter = new Docx4j_getDocx();
        converter.convert(file.getInputStream(), wordMLPackage);

        wordMLPackage.save(outputPath.toFile());

        return outputFilename;
    }

    public String convertImagesToPdf(List<MultipartFile> files, boolean compress) throws IOException {
        String outputFilename = "merged-" + UUID.randomUUID() + ".pdf";
        Path outputPath = this.generatedFileDir.resolve(outputFilename);

        PdfWriter writer = new PdfWriter(outputPath.toFile());
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            if (compress) {
                ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
                Thumbnails.of(inputStream)
                        .size(1600, 1200) // Max dimensions
                        .outputQuality(0.75) // Adjust quality
                        .toOutputStream(compressedOutputStream);
                inputStream = new ByteArrayInputStream(compressedOutputStream.toByteArray());
            }

            Image img = new Image(ImageDataFactory.create(inputStream.readAllBytes()));
            pdfDoc.addNewPage(PageSize.A4);
            // Scale image to fit the page
            img.setAutoScale(true);
            // Center image
            img.setRelativePosition((PageSize.A4.getWidth() - img.getImageScaledWidth()) / 2, 0, 0, 0);
            document.add(img);
        }
        document.close();
        return outputFilename;
    }

    public String compressPdf(String filename) throws IOException {
        Path sourcePath = this.generatedFileDir.resolve(filename);
        String compressedFilename = "compressed-" + filename;
        Path outputPath = this.generatedFileDir.resolve(compressedFilename);

        PdfReader reader = new PdfReader(sourcePath.toFile());
        PdfWriter writer = new PdfWriter(outputPath.toFile(), new WriterProperties().setFullCompressionMode(true));
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();

        return compressedFilename;
    }

    private String getBaseFilename(String filename) {
        if (filename == null) {
            return "file";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}