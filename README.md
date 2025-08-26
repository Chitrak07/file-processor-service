# 📄 File Processor Service

A powerful Spring Boot web service for converting, compressing, and managing document files like PDF, Word, and images with a clean, drag-and-drop user interface.

---

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue.svg" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg" alt="Spring Boot 3.3.3">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT">
  <img src="https://img.shields.io/badge/Build-Passing-brightgreen" alt="Build Passing">
</p>

<p align="center">
  <em>(Replace this with a GIF or screenshot of your application in action)</em>
  <br>
  <img src="https://placehold.co/800x450/f4f7f6/333?text=App+Screenshot+Here" alt="Application Screenshot">
</p>

## ✨ Core Features

-   ✅ **PDF → Word Converter**: Upload a `.pdf` and get a `.docx` file.
-   ✅ **Word → PDF Converter**: Upload a `.docx` and get a `.pdf` file.
-   ✅ **Image → PDF Converter**: Upload multiple `.jpg`, `.jpeg`, or `.png` images and merge them into a single PDF.
-   ✅ **PDF Compression**: Option to compress PDFs after conversion to reduce file size.
-   ✅ **Image Compression**: Option to compress images before generating a PDF.
-   🎨 **Modern UI**: Clean, responsive, and intuitive drag-and-drop interface.
-   🔄 **Progress Bars**: Real-time feedback on file uploads and processing.

## 🛠️ Tech Stack

This project is built with a robust set of modern technologies:

-   **Backend**:
    -   **Java 21**
    -   **Spring Boot 3.3.3** (with Spring Web)
    -   **Apache Maven** (Dependency Management)
-   **Frontend**:
    -   **JSP (Jakarta Server Pages)** with JSTL
    -   **HTML5, CSS3, JavaScript (ES6)**
-   **File Processing Libraries**:
    -   **Apache PDFBox**: For PDF creation and text extraction.
    -   **docx4j**: For creating and converting `.docx` files.
    -   **iText 7**: For powerful PDF compression.
    -   **Thumbnailator**: For efficient image compression.
-   **Server**:
    -   **Embedded Apache Tomcat**

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing.

### Prerequisites

-   **JDK 21** or later installed.
-   **Apache Maven** installed and configured.
-   An IDE like **IntelliJ IDEA** or **Eclipse**.

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/your-username/file-processor-service.git](https://github.com/your-username/file-processor-service.git)
    cd file-processor-service
    ```

2.  **Build the project with Maven:**
    This will download all the necessary dependencies.
    ```sh
    mvn clean install
    ```

3.  **Run the application:**
    You can run the application directly from your IDE by running the `main` method in `FileProcessorServiceApplication.java`, or by using the Spring Boot Maven plugin:
    ```sh
    mvn spring-boot:run
    ```

4.  **Access the application:**
    Once the application is running, open your web browser and navigate to:
    [**http://localhost:8080**](http://localhost:8080)

## 🔌 API Endpoints

The service exposes the following RESTful endpoints for file processing.

| Method | Endpoint                           | Description                                       |
| :----- | :--------------------------------- | :------------------------------------------------ |
| `POST` | `/api/convert/pdf-to-word`         | Converts an uploaded PDF file to a Word document. |
| `POST` | `/api/convert/word-to-pdf`         | Converts a Word document to a PDF.                |
| `POST` | `/api/convert/image-to-pdf`        | Merges multiple images into a single PDF file.    |

**Query Parameters:**

-   `compress=true`: Can be used with the `word-to-pdf` and `image-to-pdf` endpoints to enable file compression.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
