package top.leyou.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.leyou.common.enums.ExceptionEnum;
import top.leyou.common.exception.LyException;
import top.leyou.config.UploadProperties;
import top.leyou.service.UploadService;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadServiceImpl implements UploadService {
//    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/jpg","image/bmp","image/png");

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private UploadProperties prop;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType = file.getContentType();
            if(!prop.getAllowTypes().contains(contentType)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //校验文件内容
            BufferedImage read = ImageIO.read(file.getInputStream());
            if(read == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
//            //准备目标路径
//            File dest = new File("C:\\Users\\11078\\Desktop\\code\\"+file.getOriginalFilename());
//            //保存文件到本地
//            file.transferTo(dest);
//            "http://image.leyou.com/"
            return prop.getBaseUrl()+ storePath.getFullPath();
        } catch (IOException e) {
            log.error("[文件上传] 上传文件失败!",e);
            throw  new LyException(ExceptionEnum.UPLOAD_ERROR);
        }
        //返回文件地址
    }
}
