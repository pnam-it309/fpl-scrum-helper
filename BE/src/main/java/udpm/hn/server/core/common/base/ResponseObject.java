package udpm.hn.server.core.common.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.manage.phase.model.response.MAPhaseResponse;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResponseObject<T>  {

    private boolean isSuccess = false;

    private HttpStatus status;

    private T data;

    private String message;

    private Instant timestamp = Instant.now();

    public ResponseObject(T obj, HttpStatus status, String message) {
        processResponseObject(obj);
        this.status = status;
        this.message = message;
    }

    public ResponseObject(List<MAPhaseResponse> allPhaseByProjectId) {
        this.data = (T) allPhaseByProjectId;
        this.status = allPhaseByProjectId.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        this.message = allPhaseByProjectId.isEmpty() ? "Không tìm thấy dữ liệu" : "Lấy dữ liệu thành công";
        this.isSuccess = !allPhaseByProjectId.isEmpty();
    }

    public ResponseObject(Integer integer) {
    }

    public ResponseObject(boolean b, String s, Object message) {
    }

    public void processResponseObject(T obj) {
        if (obj != null) {
            this.isSuccess = true;
            this.data = obj;
        }
    }

    public ResponseObject<T> success(T obj, String message) {
        processResponseObject(obj);
        this.status = HttpStatus.OK;
        this.message = message;
        return this;
    }

    public ResponseObject<T> success(String message) {
        this.isSuccess = true;
        this.status = HttpStatus.OK;
        this.message = message;
        return this;
    }

    public ResponseObject<T> error(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        return this;
    }

    public static <T> ResponseObject<T> successForward(T obj, String message) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setSuccess(true);
        responseObject.setStatus(HttpStatus.OK);
        responseObject.setData(obj);
        responseObject.setMessage(message);
        return responseObject;
    }

    public static <T> ResponseObject<T> errorForward(String message, HttpStatus status) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setSuccess(false);
        responseObject.setStatus(status);
        responseObject.setMessage(message);
        return responseObject;
    }


}
