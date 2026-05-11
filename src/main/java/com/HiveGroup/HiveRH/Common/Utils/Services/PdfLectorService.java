package com.HiveGroup.HiveRH.Common.Utils.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public abstract class PdfLectorService {

    public static byte[] savePDF(MultipartFile pdf) throws IOException {
        return pdf.getBytes();
    }

    public static Path loadPDF(byte[] arrByte) throws IOException {
        return Files.write(Paths.get("test.pdf"), arrByte);
    }


}
