package org.mushare.rate.data;

import android.support.annotation.NonNull;

/**
 * Created by dklap on 3/9/2017.
 */

public class MyCurrencyWrapper implements Comparable<MyCurrencyWrapper> {
    private String cid;
    private char character;
    private MyCurrency myCurrency;

    public MyCurrencyWrapper(String cid, MyCurrency myCurrency) {
        this.cid = cid;
        this.myCurrency = myCurrency;
    }

    public String getCid() {
        return cid;
    }

    public MyCurrency getCurrency() {
        return myCurrency;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    @Override
    public int compareTo(@NonNull MyCurrencyWrapper o) {
        return myCurrency.getCode().compareTo((o.getCurrency().getCode()));
    }
}
