package top.leyou.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.leyou.service.UploadService;

import javax.annotation.Resource;
import java.util.Stack;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Resource
    private UploadService uploadService;

    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file){
        String url = uploadService.uploadImage(file);
//        Stack
        return ResponseEntity.ok(url);
    }
}
