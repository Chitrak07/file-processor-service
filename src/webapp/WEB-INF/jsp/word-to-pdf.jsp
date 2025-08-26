<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Word to PDF Converter</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
    <div class="container">
        <a href="/" class="back-link">&larr; Back to Home</a>
        <h1>Word → PDF Converter</h1>
        <div id="drop-zone" class="drop-zone">
            <p>Drag & Drop a .DOCX file here or click to select</p>
            <input type="file" id="file-input" accept=".docx,.doc" hidden>
        </div>
        <div class="options">
            <input type="checkbox" id="compress-checkbox">
            <label for="compress-checkbox">Compress PDF after conversion</label>
        </div>
        <div class="progress-container" id="progress-container" style="display: none;">
            <div class="progress-bar" id="progress-bar">0%</div>
        </div>
        <div id="result" class="result" style="display: none;">
            <h3>Conversion Complete!</h3>
            <p id="file-info"></p>
            <a id="download-btn" class="download-btn" href="#" download>Download PDF File</a>
        </div>
    </div>
    <script>
        const API_ENDPOINT = '<c:url value="/api/convert/word-to-pdf"/>';
        const IS_MULTIPLE = false;
    </script>
    <script src="<c:url value='/js/app.js'/>"></script>
</body>
</html>