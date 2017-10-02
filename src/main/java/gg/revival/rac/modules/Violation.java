package gg.revival.rac.modules;

import lombok.Getter;

public class Violation {

    @Getter String information;
    @Getter long createTime;

    public Violation(String information) {
        this.information = information;
        this.createTime = System.currentTimeMillis();
    }

}
