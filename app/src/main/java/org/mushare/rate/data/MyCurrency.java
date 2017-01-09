package org.mushare.rate.data;

/**
 * Created by dklap on 1/3/2017.
 */

public class MyCurrency {
    private String code;
    private String icon;
    private String name;

    public MyCurrency(String code, String icon, String name) {
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
