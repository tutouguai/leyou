package top.leyou.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "ly.upload")
public class UploadProperties {
    private String BaseUrl;
    private List<String> allowTypes;
}
