package dictionary;

/**
 * Created by wengjiayu on 01/11/2017.
 * E-mail wengjiayu521@163.com
 */
public class ExceptionError {

    private ExceptionError() {
    }

    public static final Integer SYS_ERROR_CODE = 50000;
    public static final Integer APP_ERROR_CODE = 40000;
    public static final Integer API_ERROR_CODE = 30000;

    public static final Integer HTTP_TIMEOUT_IN_MS = 10000;
    public static final Integer CONNECT_TIMEOUT_IN_MS = 5000;
    public static final Integer REQUEST_TIMEOUT_IN_MS = 5000;

}
