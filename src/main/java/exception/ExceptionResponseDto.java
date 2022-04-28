package exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponseDto {
    private String statusCode;
    private String statusMsg;
    private Object data;

    public ExceptionResponseDto(StatusCode statusCode, Object data){
        this.statusCode = statusCode.getStatusCode();
        this.statusMsg = statusCode.getStatusMsg();
        this.data = data;
    }
}