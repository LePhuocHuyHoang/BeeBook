package com.beebook.beebookproject.hdfs;


import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Demo{
    @Autowired
    private FileSystem fileSystem;

//    public static boolean existFile(String path) throws Exception {
//        if (StringUtils.isEmpty(path)) {
//            return false;
//        }
//        FileSystem fs = getFileSystem();
//        Path srcPath = new Path(path);
//        boolean isExists = fs.exists(srcPath);
//        return isExists;
//    }

    public List<Map<String, String>> findAll(String path){
        try {
            Path dir =new Path(path);
            FileStatus[] fss = fileSystem.listStatus(dir);
            List<Map<String, String>> list = Arrays.stream(fss).map(file ->
                            Map.of("Directory", Boolean.toString(file.isDirectory()),
                                    "name", file.getPath().getName(),
                                    "Path", file.getPath().toUri().getPath(),
                                    "ModificationTime", Long.toString(file.getModificationTime()),
                                    "Len", Long.toString(file.getLen())))
                    .collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            log.error("Truy vấn lỗi HDFS: {}", e);
        }
        return List.of();
    }

    public void uploadFile(MultipartFile file) throws Exception{
        String fileName = file.getOriginalFilename();
        log.info("name: {}", fileName);

        FSDataOutputStream fos = fileSystem.create(new Path("/" + fileName));

        InputStream fis = file.getInputStream();

        IOUtils.copyBytes(fis,fos,1024,true);
    }

    public void uploadFiles(List<MultipartFile> files) throws Exception {
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                // Create FSDataOutputStream for the file on HDFS
                FSDataOutputStream fos = fileSystem.create(new Path("/" + fileName));

                // Get InputStream from MultipartFile
                InputStream fis = file.getInputStream();

                // Copy bytes from InputStream to FSDataOutputStream
                IOUtils.copyBytes(fis, fos, 1024, true);

                // Close streams
                fis.close();
                fos.close();
            }
        }
    }


    public boolean deleteFile(String path) throws Exception {
        Path srcPath = new Path(path);

        System.out.println("Deleting file: " + path);

        if (!fileSystem.exists(srcPath)) {
            System.out.println("File does not exist: " + path);
            return false;
        }

        boolean isOk = fileSystem.delete(srcPath, true);
        if (isOk) {
            System.out.println("File deleted successfully: " + path);
        } else {
            System.out.println("Failed to delete file: " + path);
        }

        return isOk;
    }

    public byte[] getFileContent(String remoteFilePath) throws IOException {
        FSDataInputStream inputStream = fileSystem.open(new Path(remoteFilePath));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();

        return outputStream.toByteArray();
    }
}