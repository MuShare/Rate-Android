package mushare.org.rate.data;

/**
 * Created by dklap on 1/3/2017.
 */

public class Currency {
    private String code;
    private String icon;
    private String name;

    public Currency(String code, String icon, String name) {
        this.name = name;
        this.code = code;
        this.icon = icon;
    }


    public String getCode() {
        return code;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
