document.addEventListener('DOMContentLoaded', () => {
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-input');
    const progressContainer = document.getElementById('progress-container');
    const progressBar = document.getElementById('progress-bar');
    const resultDiv = document.getElementById('result');
    const downloadBtn = document.getElementById('download-btn');
    const fileInfo = document.getElementById('file-info');
    const compressCheckbox = document.getElementById('compress-checkbox');

    if (!dropZone) return;

    dropZone.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', () => handleFiles(fileInput.files));

    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragleave', () => {
        dropZone.classList.remove('dragover');
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        handleFiles(e.dataTransfer.files);
    });

    const handleFiles = (files) => {
        if (files.length === 0) return;

        const filesToUpload = IS_MULTIPLE ? files : [files[0]];

        resultDiv.style.display = 'none';
        progressContainer.style.display = 'block';
        progressBar.style.width = '0%';
        progressBar.textContent = '0%';

        uploadFiles(filesToUpload);
    };

    const uploadFiles = (files) => {
        const formData = new FormData();
        const paramName = IS_MULTIPLE ? 'files' : 'file';

        for (const file of files) {
            formData.append(paramName, file);
        }

        if (compressCheckbox && compressCheckbox.checked) {
            formData.append('compress', 'true');
        }

        const xhr = new XMLHttpRequest();
        xhr.open('POST', API_ENDPOINT, true);

        xhr.upload.onprogress = (event) => {
            if (event.lengthComputable) {
                const percentComplete = Math.round((event.loaded / event.total) * 100);
                progressBar.style.width = percentComplete + '%';
                progressBar.textContent = percentComplete + '%';
            }
        };

        xhr.onload = () => {
            if (xhr.status === 200) {
                const blob = xhr.response;
                const url = URL.createObjectURL(blob);
                const originalSize = Array.from(files).reduce((sum, file) => sum + file.size, 0);

                const disposition = xhr.getResponseHeader('Content-Disposition');
                let fileName = 'downloaded-file';
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    const matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                      fileName = matches[1].replace(/['"]/g, '');
                    }
                }

                downloadBtn.href = url;
                downloadBtn.download = fileName;

                fileInfo.textContent = `Original Size: ${formatBytes(originalSize)} | New Size: ${formatBytes(blob.size)}`;
                resultDiv.style.display = 'block';
            } else {
                alert('An error occurred during the conversion. Please check the console for details.');
                progressContainer.style.display = 'none';
            }
        };

        xhr.onerror = () => {
            alert('Request failed. Please check your connection or the server logs.');
            progressContainer.style.display = 'none';
        };

        xhr.responseType = 'blob';
        xhr.send(formData);
    };

    const formatBytes = (bytes, decimals = 2) => {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    };
});