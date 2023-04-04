package top.leyou.common.vo;

import lombok.Data;
import top.leyou.common.enums.ExceptionEnum;

@Data
public class ExceptionResult {
    private int status;
    private String msg;
    private Long timestamp;

    public ExceptionResult(ExceptionEnum em){
        this.status = em.getCode();
        this.msg = em.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
