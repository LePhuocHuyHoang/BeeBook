package com.beebook.beebookproject.common.util;

import org.apache.hadoop.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class FileUtil {
    /**
     * Luồng đầu vào ghi tập tin
     * @param in luồng đầu vào
     * @param file tệp
     */
    public static void inputStreamToFile(InputStream in,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            IOUtils.closeStreams(os, in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * MultipartFile vào tập tin
     * @param file tải tệp lên
     * @return {@link File}
     */
    public static File MultipartFileToFile(MultipartFile file) {
        File f = null;
        try {
            if (file != null && file.getSize() > 0) {
                InputStream in = file.getInputStream();
                f = new File(Objects.requireNonNull(file.getOriginalFilename()));
                inputStreamToFile(in, f);
            }
            return f;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return f;
        }
    }
}
