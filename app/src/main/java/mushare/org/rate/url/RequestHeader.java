package mushare.org.rate.url;

/**
 * Created by dklap on 1/4/2017.
 */

public class RequestHeader {
    private String key;
    private String value;

    public RequestHeader(String key, String value) {

        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
