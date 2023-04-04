package top.leyou.common.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import top.leyou.common.enums.ExceptionEnum;

@NoArgsConstructor
@AllArgsConstructor
public class LyException extends RuntimeException{
    private ExceptionEnum exceptionEnum;
}
