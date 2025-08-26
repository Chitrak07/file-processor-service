package com.docutools.fileprocessorservice.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ConversionService {

    /**
     * Extracts text content from a given file (PDF, DOCX, XLSX).
     * @param file The MultipartFile to process.
     * @return The extracted text as a String.
     */
    public String extractText(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name is null.");
        }
        String fileExtension = getFileExtension(originalFilename);

        try (InputStream inputStream = file.getInputStream()) {
            return switch (fileExtension) {
                case ".pdf" -> extractTextFromPdf(inputStream);
                case ".docx" -> extractTextFromDocx(inputStream);
                case ".xlsx" -> extractTextFromXlsx(inputStream);
                default -> "Unsupported file type for text extraction: " + fileExtension;
            };
        }
    }

    /**
     * Saves an uploaded file to a local 'uploads' directory.
     * @param file The MultipartFile to save.
     * @return The absolute path of the saved file.
     */
    public String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (file.getOriginalFilename() == null) {
            throw new IOException("File has no name.");
        }

        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toAbsolutePath().toString();
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // no extension
        }
        return fileName.substring(lastIndexOf).toLowerCase();
    }

    private String extractTextFromPdf(InputStream inputStream) throws IOException {
        StringBuilder text = new StringBuilder();
        try (PdfReader reader = new PdfReader(inputStream);
             PdfDocument pdfDocument = new PdfDocument(reader)) {
            int numPages = pdfDocument.getNumberOfPages();
            for (int i = 1; i <= numPages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i)));
                text.append("\n");
            }
        }
        return text.toString();
    }

    private String extractTextFromDocx(InputStream inputStream) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
        List<Object> content = wordMLPackage.getMainDocumentPart().getContent();
        StringWriter sw = new StringWriter();
        for (Object o : content) {
            TextUtils.extractText(o, sw);
        }
        return sw.toString();
    }

    private String extractTextFromXlsx(InputStream inputStream) throws IOException {
        StringBuilder text = new StringBuilder();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        text.append(getCellStringValue(cell)).append("\t");
                    }
                    text.append("\n");
                }
            }
        }
        return text.toString();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}