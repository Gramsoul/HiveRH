package com.HiveGroup.HiveRH.Common.Utils.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class FileLectorService {

    public byte[] savePDF(MultipartFile pdf) throws IOException {
        if (pdf == null || pdf.isEmpty()) throw new IllegalArgumentException("El archivo esta vacio");
        if (pdf.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("Tamaño maximo superado (5mb)");

        String contentName = pdf.getOriginalFilename();
        if (contentName == null || !contentName.contains(".")) {
            throw new IllegalArgumentException("Extensión no permitida");
        }

        String ext = contentName.substring(contentName.lastIndexOf('.') + 1);
        if (!ext.contains("pdf"))
            throw new IllegalArgumentException("Extencion no permitida"+" -> "+ext);

        return pdf.getBytes();
    }

    public byte[] savePicture(MultipartFile file) throws IOException{
        validateImage(file);
        return file.getBytes();
    }

    private void validateContentImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("El archivo esta vacio");
    }

    private void validateSizeImage(MultipartFile file) {
        long convert = 1024 * 1024;
        long maxSize = 2 * convert; //el numero es igual a la cantidad de mb
        if (file.getSize() > maxSize) throw new IllegalArgumentException("Tamaño maximo superado (2mb)");
    }

    private void validateExtImage(MultipartFile file) {
        String contentName = file.getOriginalFilename();

        if (contentName == null || !contentName.contains(".")) {
            throw new IllegalArgumentException("Extensión no permitida");
        }

        String ext = contentName.substring(contentName.lastIndexOf('.') + 1);
        if (!ext.contains("jpg") && !ext.contains("png") && !ext.contains("jpeg"))
            throw new IllegalArgumentException("Extencion no permitida"+" -> "+ext);
    }

    private void validateImage(MultipartFile file) {
        validateContentImage(file);
        validateSizeImage(file);
        validateExtImage(file);
    }
}
