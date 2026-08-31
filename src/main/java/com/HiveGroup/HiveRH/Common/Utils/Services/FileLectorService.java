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

    public byte[] saveFile(MultipartFile pdf) throws IOException {
        return pdf.getBytes();
    }

    public Path loadFile(byte[] arrByte) throws IOException {
        return Files.write(Paths.get("test.pdf"), arrByte);
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
        String contentType = file.getContentType();
        if (!Set.of("image/jpeg", "image/png")
                .contains(contentType)) throw new IllegalArgumentException("Extencion no permitida");
    }

    private void validateImage(MultipartFile file) {
        validateContentImage(file);
        validateSizeImage(file);
        validateExtImage(file);
    }

    public String test(MultipartFile file){
        validateImage(file);
        return "name:"+file.getOriginalFilename()+" | ext:"+file.getContentType()+" size:"+file.getSize()*1024*1024;
    }


}
