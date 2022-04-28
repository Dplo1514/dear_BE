package exception;

import lombok.Getter;

@Getter
public class PrivateException extends RuntimeException {
    private StatusCode statusCode;

    public PrivateException(StatusCode statusCode) {
        super(statusCode.getStatusMsg());
        this.statusCode = statusCode;
    }
}
