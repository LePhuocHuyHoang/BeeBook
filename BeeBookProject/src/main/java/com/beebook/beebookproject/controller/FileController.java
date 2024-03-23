package com.beebook.beebookproject.controller;


import com.beebook.beebookproject.base.BaseResponse;
import com.beebook.beebookproject.hdfs.HadoopClient;
import com.beebook.beebookproject.common.util.FileUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * hadoop hdfs文件操作相关接口
 *
 * @author zhangcx
 * on 2020/5/30 - 14:15
 */
@RestController
@RequestMapping("file")
@AllArgsConstructor
public class FileController {

    private HadoopClient hadoopClient;

    /**
     * tải tập tin lên
     */
    @PostMapping("upload")
    public BaseResponse upload(@RequestParam String uploadPath, MultipartFile file) {
        hadoopClient.copyFileToHDFS(false, true, FileUtil.MultipartFileToFile(file).getPath(), uploadPath);
        return BaseResponse.ok();
    }

    @PostMapping("uploads")
    public BaseResponse uploads(@RequestParam String uploadPath, @RequestParam("files") MultipartFile[] files) {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files) {
            filePaths.add(FileUtil.MultipartFileToFile(file).getPath());
        }
        hadoopClient.copyFilesToHDFS(false, true, filePaths, uploadPath);
        return BaseResponse.ok();
    }

//    /**
//     * Test
//     */
//    @PostMapping("uploadTest")
//    public BaseResponse uploadTest(@RequestParam String uploadPath) {
//        hadoopClient.copyFileToHDFS(false, true, "/hadooptest/access.log-20190720", uploadPath);
//        return BaseResponse.ok();
//    }


    /**
     * Tải tập tin
     */
    @GetMapping("download")
    public void download(@RequestParam String path, @RequestParam String fileName, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("charset", "utf-8");
        response.setContentType("application/force-download");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        OutputStream os;
        try {
            os = response.getOutputStream();
            hadoopClient.download(path, fileName, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tạo một thư mục (Tạo thư mục sách)
     */
    @PostMapping("mkdir")
    public BaseResponse mkdir(@RequestParam String folderPath) {
        boolean result = false;
        if(!hadoopClient.isPathExists(folderPath)){
            return BaseResponse.ok(HttpStatus.BAD_REQUEST, "Folder already exists ");
        }
        if (StringUtils.isNotEmpty(folderPath)) {
            result = hadoopClient.mkdirfolder(folderPath, true);
        }
        return BaseResponse.ok(result);
    }

    /**
     * Thông tin thư mục
     */
    @GetMapping("getPathInfo")
    public BaseResponse<List<Map<String, Object>>> getPathInfo(@RequestParam String path) {
        return BaseResponse.ok(hadoopClient.getPathInfo(path));
    }

    /**
     * Lấy danh sách các file trong thư mục
     */
    @GetMapping("getFileList")
    public BaseResponse<List<Map<String,String>>> getFileList(@RequestParam String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        return BaseResponse.ok(hadoopClient.getFileList(path));
    }

    /**
     * Xóa tập tin hoặc thư mục
     */
    @PostMapping("rmdir")
    public BaseResponse rmdir(@RequestParam String path, @RequestParam(required = false) String fileName) {
        hadoopClient.rmdir(path, fileName);
        return BaseResponse.ok();
    }

    /**
     * Đọc nội dung tập tin
     */
    @GetMapping("readFile")
    public BaseResponse readFile(@RequestParam String filePath) {
        return BaseResponse.ok((Object)hadoopClient.readFile(filePath));
    }
//    getBookCover (prams bkId){
//    /data/book/+bookkid/book-cover
//                /data/book/+bookkid/book-cover
//    }

    /**
     * Đọc nội dung tập tin byte[]
     */
    @GetMapping("/readFileToImage")
    public BaseResponse readFileToImage(@RequestParam String filePath) {
        byte[] imageData = hadoopClient.readFiletoByte(filePath);
        String base64Image = Base64.getEncoder().encodeToString(imageData);

        // Kiểm tra xem dữ liệu hợp lệ trước khi trả về
        if (imageData != null) {
            return BaseResponse.ok(base64Image);
        } else {
            return BaseResponse.error("Không thể đọc tệp.");
        }
    }

    @GetMapping("/readFileToImagePages")
    public BaseResponse<?> readFileToImagePages(@RequestParam int page, @RequestParam int bookId) {

        if(!hadoopClient.isPathExists("/data/book/book_" + bookId)) {
            return BaseResponse.ok(HttpStatus.BAD_REQUEST, "Book id is not exists");
        }
        if(page < 1){
            return BaseResponse.ok(HttpStatus.BAD_REQUEST, "Page number must be greater than or equal to 1");
        }
        int pageSize = 5; // Số lượng giá trị mỗi trang
        int startId = (page - 1) * pageSize + 1; // ID bắt đầu của trang hiện tại
        int endId = startId + pageSize - 1; // ID kết thúc của trang hiện tại
        List<Map<String, String>> imgs = new ArrayList<>();
        try {
            for (int i = startId; i <= endId; i++) {
                Map<String, String> bookCoverMap = new HashMap<>();
                String filePath = "/data/book/book_" + bookId +"/img_" + i + ".jpg";
                byte[] imageData = hadoopClient.readFiletoByte(filePath);

                if (imageData != null && imageData.length > 0) {
                    String base64Image = Base64.getEncoder().encodeToString(imageData);
                    bookCoverMap.put("img_"+i, base64Image);
                } else {
                    if(i == startId){
                        return BaseResponse.ok(Collections.emptyList());
                    }
                    // Nếu không có cover cho sách, gán giá trị rỗng
                    bookCoverMap.put("img_"+i, "");
                }
                if (!bookCoverMap.isEmpty()) {
                    imgs.add(bookCoverMap);
                }
            }
            return BaseResponse.ok(imgs);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.error("Error occurred while reading files to images.");
        }
    }

    @GetMapping("/getBookCoverList")
    public BaseResponse<List<Map<String, String>>> getBookCoverList(@RequestParam String bookIdList) {
        String path = "/data/book";
        String[] bookIds = bookIdList.split(",");
        List<Map<String, String>> bookCovers = new ArrayList<>();

        try {
            for (String bookId : bookIds) {
                String bookCoverFilePath = path + "/book_" + bookId + "/book_cover.jpg";
                String backgroundCoverFilePath = path + "/book_" + bookId + "/background_cover.jpg";

                byte[] imageBookCoverData = hadoopClient.readFiletoByte(bookCoverFilePath);
                byte[] imageBackgroundCoverData = hadoopClient.readFiletoByte(backgroundCoverFilePath);

                // Tạo một map mới để chứa dữ liệu cover của sách
                Map<String, String> bookCoverMap = new HashMap<>();


                if (imageBookCoverData != null && imageBookCoverData.length > 0) {
                    String base64ImageBookCover = Base64.getEncoder().encodeToString(imageBookCoverData);
                    bookCoverMap.put("book_cover", base64ImageBookCover);
                } else {
                    // Nếu không có cover cho sách, gán giá trị rỗng
                    bookCoverMap.put("book_cover", "");
                }

                if (imageBackgroundCoverData != null && imageBackgroundCoverData.length > 0) {
                    String base64ImageBackgroundCover = Base64.getEncoder().encodeToString(imageBackgroundCoverData);
                    bookCoverMap.put("background_cover", base64ImageBackgroundCover);
                } else {
                    // Nếu không có background cover, gán giá trị rỗng
                    bookCoverMap.put("background_cover", "");
                }
                // Kiểm tra xem map có chứa dữ liệu không trước khi thêm vào list
                if (!bookCoverMap.isEmpty()) {
                    bookCovers.add(bookCoverMap);
                }
            }
            return BaseResponse.ok(bookCovers);
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            e.printStackTrace();
            return BaseResponse.error("Error occurred while fetching book covers.");
        }
    }





    /**
     * Đổi tên tập tin hoặc thư mục
     */
    @PostMapping("renameFile")
    public BaseResponse renameFile(@RequestParam String oldName, @RequestParam String newName) {
        return BaseResponse.ok(hadoopClient.renameFile(oldName, newName));
    }

    /**
     * Tải lên các tập tin địa phương
     */
    @PostMapping("uploadFileFromLocal")
    public BaseResponse uploadFileFromLocal(@RequestParam String path, @RequestParam String uploadPath) {
        hadoopClient.copyFileToHDFS(false, true, path, uploadPath);
        return BaseResponse.ok();
    }

    /**
     * Tải tập tin về máy cục bộ
     */
    @PostMapping("downloadFileFromLocal")
    public BaseResponse downloadFileFromLocal(@RequestParam String path, @RequestParam String downloadPath) {
        hadoopClient.downloadFileFromLocal(path, downloadPath);
        return BaseResponse.ok();
    }

    /**
     * Sao chép tập tin
     */
    @PostMapping("copyFile")
    public BaseResponse copyFile(String sourcePath, String targetPath) {
        hadoopClient.copyFile(sourcePath, targetPath);
        return BaseResponse.ok();
    }

    /**
     * Đọc List image
     * @param directoryPath
     * @return
     */
    @GetMapping("/readFilesToImages")
    public BaseResponse readFilesToImages(@RequestParam String directoryPath) {
        List<byte[]> imageList = hadoopClient.readFilesToImages(directoryPath);
        if (imageList != null && !imageList.isEmpty()) {
            return BaseResponse.ok(imageList);
        } else {
            return BaseResponse.error("Không thể đọc tệp.");
        }
    }

}
