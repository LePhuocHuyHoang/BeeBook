package com.beebook.beebookproject.hdfs;


import com.beebook.beebookproject.props.HadoopProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@AllArgsConstructor
public class HadoopClient {

    private FileSystem fs;
    private HadoopProperties hadoopProperties;

    /**
     * Tạo thư mục trong quá trình khởi tạo, tạo nếu chưa tồn tại
     */
    @PostConstruct
    public void init(){
        mkdir(hadoopProperties.getDirectoryPath(),true);
    }

    /**
     * Nối địa chỉ đường dẫn file
     *
     * @param folder thư mục @param
     * @return {@link String}
     */
    private String spliceFolderPath(String folder) {
        if (StringUtils.isNotEmpty(folder)) {
            return hadoopProperties.getPath() + folder;
        } else {
            return hadoopProperties.getPath();
        }
    }

    /**
     * Tạo một thư mục
     *
     * @param folderPath tên đường dẫn thư mục
     * @param create Có tạo thư mục mới nếu nó không tồn tại
     * @return {@link boolean}
     */
    public boolean mkdir(String folderPath, boolean create){
        log.info("【Bắt đầu tạo một thư mục】 Tên đường dẫn thư mục: {}", folderPath);
        boolean flag = false;
        if(StringUtils.isEmpty(folderPath)){
            throw new IllegalArgumentException("Thư mục không thể trống");
        }
        try{
            Path path = new Path(folderPath);
            if (create){
                if (!fs.exists(path)){
                    fs.mkdirs(path);
                }
            }
            if (fs.getFileStatus(path).isDirectory()){
                flag = true;
            }
        }catch (Exception e){
            log.error("【Không tạo được thư mục】", e);
        }
        return flag;
    }


    public boolean mkdirfolder(String folderPath, boolean create){
        log.info("【Bắt đầu tạo một thư mục】 Tên đường dẫn thư mục: {}", folderPath);
        boolean flag = false;
        if(StringUtils.isEmpty(folderPath)){
            throw new IllegalArgumentException("Thư mục không thể trống");
        }
        try{
            //preFolderPath
            String preFolderPath = "/data/book";
            Path path = new Path(preFolderPath+"/"+folderPath);
            if (create){
                if (!fs.exists(path)){
                    fs.mkdirs(path);
                }
            }
            if (fs.getFileStatus(path).isDirectory()){
                flag = true;
            }
        }catch (Exception e){
            log.error("【Không tạo được thư mục】", e);
        }
        return flag;
    }

    /**
     * Tải lên tệp
     *
     * @param delSrc đề cập đến việc có xóa tệp nguồn hay không, true nghĩa là xóa và mặc định là sai
     * @param overwrite có ghi đè không
     * @param srcFile file nguồn, đường dẫn file upload
     * @param destPath  đường dẫn đích fs
     */
    public  void copyFileToHDFS(boolean delSrc, boolean overwrite, String srcFile, String destPath) {
        log.info("【Tải lên tệp】 Bắt đầu Upload, Upload đường dẫn file: {}", srcFile);
        Path srcPath = new Path(srcFile);

        Path dstPath = new Path(destPath);
        try {

            fs.copyFromLocalFile(delSrc, overwrite, srcPath, dstPath);
        } catch (IOException e) {
            log.error("【Tải tệp lên không thành công】", e);
        }
    }


    /**
     * Xóa tập tin hoặc thư mục tập tin
     *
     * @param path đường dẫn thư mục tệp
     * @param fileName tên tệp
     */
    public void rmdir(String path, String fileName) {
        log.info("【Xóa các tập tin】 Bắt đầu xóa, xóa đường dẫn của thư mục file: {}, Tập tin thư mục: {}", path, fileName);
        try {

            if(StringUtils.isNotBlank(fileName)){
                path =  path + "/" +fileName;
            }

            fs.delete(new Path(path), true);
        } catch (IllegalArgumentException | IOException e) {
            log.error("【Không thể xóa tập tin】", e);
        }
    }

    /**
     * Tải tập tin
     *
     * Đường dẫn @param
     * @param fileName tên tệp
     * @param outputStream  đầu ra luồng đầu ra
     * @throws IOException  luồng IOException
     */
    public void download(String path, String fileName, OutputStream outputStream) throws IOException {
        log.info("【Tải tập tin】 Bắt đầu tải xuống, tải tên file: {}", fileName);
        @Cleanup InputStream is = fs.open(new Path(path + fileName));
        IOUtils.copyBytes(is, outputStream,4096,true);
    }

    /**
     * Tải tập tin về máy cục bộ
     *
     * Đường dẫn tệp @param
     * @param downloadPath đường dẫn tải xuống cục bộ
     */
    public void downloadFileFromLocal(String path, String downloadPath) {
        log.info("【Tải tập tin về máy cục bộ】 Bắt đầu tải xuống, đường dẫn tệp: {}, Đường dẫn tải xuống cục bộ: {}", path, downloadPath);

        Path clientPath = new Path(path);

        Path serverPath = new Path(downloadPath);
        try {

            fs.copyToLocalFile(false, clientPath, serverPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Nhận thông tin thư mục
     *
     * Đường dẫn thư mục đường dẫn @param
     */
    public List<Map<String, Object>> getPathInfo(String path) {
        log.info("【Nhận thông tin thư mục】 Bắt đầu nhận, đường dẫn thư mục: {}", path);
        FileStatus[] statusList;
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            statusList = fs.listStatus(new Path(path));
            if (null != statusList && statusList.length > 0) {
                for (FileStatus fileStatus : statusList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("filePath", fileStatus.getPath());
                    map.put("fileStatus", fileStatus.toString());
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách các file trong thư mục
     *
     * Đường dẫn thư mục đường dẫn @param
     * @return {@danh sách liên kết}
     */
    public List<Map<String, String>> getFileList(String path) {
        log.info("【Lấy danh sách các tập tin trong một thư mục】 Bắt đầu nhận, đường dẫn thư mục: {}", path);
        List<Map<String, String>> list = new ArrayList<>();
        try {

            RemoteIterator<LocatedFileStatus> filesList = fs.listFiles(new Path(path), true);
            while (filesList.hasNext()) {
                LocatedFileStatus next = filesList.next();
                String fileName = next.getPath().getName();
                Path filePath = next.getPath();
                Map<String, String> map = new HashMap<>();
                map.put("fileName", fileName);
                map.put("filePath", filePath.toString());
                list.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, String>> getBookCover(String path) {
        log.info("【Lấy danh sách các tập tin trong một thư mục】 Bắt đầu nhận, đường dẫn thư mục: {}", path);
        List<Map<String, String>> list = new ArrayList<>();
        try {

            RemoteIterator<LocatedFileStatus> filesList = fs.listFiles(new Path(path), true);
            while (filesList.hasNext()) {
                LocatedFileStatus next = filesList.next();
                String fileName = next.getPath().getName();
                Path filePath = next.getPath();
                Map<String, String> map = new HashMap<>();
                map.put("fileName", fileName);
                map.put("filePath", filePath.toString());
                list.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đọc nội dung tập tin từ đường dẫn tệp
     * @param filePath
     */
    public String readFile(String filePath) {
        log.info("【Đọc nội dung tập tin】 Bắt đầu đọc, đường dẫn tập tin: {}", filePath);
        Path newPath = new Path(filePath);
        InputStream in = null;
        BufferedReader reader = null;
        StringBuilder buffer = new StringBuilder();
        try {
            in = fs.open(newPath);
            String line;
            reader = new BufferedReader(new InputStreamReader(in, "GBK"));
            line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append("\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            IOUtils.closeStream(in);
        }
        return buffer.toString();
    }

    private byte[] readFileToByte(FileSystem fs, Path filePath) {
        byte[] buffer = null;
        try {
            if (fs.exists(filePath)) {
                FSDataInputStream inputStream = fs.open(filePath);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] tempBuffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(tempBuffer)) != -1) {
                    outputStream.write(tempBuffer, 0, bytesRead);
                }
                buffer = outputStream.toByteArray();
                inputStream.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Đọc nội dung tập tin từ đường dẫn tệp
     * @param filePath
     */
    public byte[] readFiletoByte(String filePath) {
        byte[] buffer = null;
        try {
            Path path = new Path(filePath);
            if (fs.exists(path)) {
                FSDataInputStream inputStream = fs.open(path);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] tempBuffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(tempBuffer)) != -1) {
                    outputStream.write(tempBuffer, 0, bytesRead);
                }
                buffer = outputStream.toByteArray();
                inputStream.close();
                outputStream.close();
            }
//            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public List<byte[]> readFilesToImages(String directoryPath) {
        List<byte[]> imageList = new ArrayList<>();
        try {
            Path dirPath = new Path(directoryPath);
            if (fs.exists(dirPath) && fs.isDirectory(dirPath)) {
                // List all files in the directory
                Path[] files = FileUtil.stat2Paths(fs.listStatus(dirPath));
                // Read each file and add its content to the list
                for (Path file : files) {
                    byte[] fileData = readFileToByte(fs, file);
                    if (fileData != null) {
                        imageList.add(fileData);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageList;
    }


    /**
     * Đổi tên tập tin hoặc thư mục
     * @param oldName tên tập tin hoặc thư mục cũ
     * @param newName Tệp mới hoặc tên thư mục mới
     * @return Liệu thay đổi có thành công hay không đúng: thành công/sai: thất bại
     */
    public boolean renameFile(String oldName, String newName) {
        log.info("【Đổi tên tập tin hoặc thư mục】 Bắt đầu đổi tên, tập tin cũ hoặc tên thư mục cũ: {}, Tên tập tin hoặc thư mục mới: {} ", oldName, newName);
        boolean isOk = false;
        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        try {
            isOk = fs.rename(oldPath, newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isOk;
    }

    /**
     * Sao chép tập tin
     *
     * @param sourcePath đường dẫn sao chép
     * @param targetPath đường dẫn đích
     */
    public void copyFile(String sourcePath, String targetPath) {
        log.info("【Sao chép tệp】 Bắt đầu sao chép, sao chép đường dẫn: {}, đường dẫn đích: {}", sourcePath, targetPath);

        Path oldPath = new Path(sourcePath);

        Path newPath = new Path(targetPath);

        FSDataInputStream inputStream;
        FSDataOutputStream outputStream;
        try {
            inputStream = fs.open(oldPath);
            outputStream = fs.create(newPath);
            IOUtils.copyBytes(inputStream, outputStream, 1024 * 1024 * 64, false);
            IOUtils.closeStreams(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void copyFilesToHDFS(boolean delSrc, boolean overwrite, List<String> srcFiles, String destPath) {
        log.info("Starting file upload to HDFS...");
        Path dstPath = new Path(destPath);

        try {
            for (String srcFile : srcFiles) {
                Path srcPath = new Path(srcFile);
                fs.copyFromLocalFile(delSrc, overwrite, srcPath, dstPath);
                log.info("File uploaded: {}", srcFile);
            }
        } catch (IOException e) {
            log.error("Failed to upload files to HDFS", e);
        }
    }
    public boolean isPathExists(String path) {

        try {
            // Tạo một đối tượng Path từ đường dẫn
            Path hdfsPath = new Path(path);
            // Kiểm tra xem đường dẫn tồn tại trên HDFS hay không
            return fs.exists(hdfsPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fs != null) {
                try {
//                    fs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
