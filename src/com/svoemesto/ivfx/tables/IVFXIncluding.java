package com.svoemesto.ivfx.tables;

public class IVFXIncluding {
    private boolean isIncluding;
    private String name;

    public IVFXIncluding() {
    }

    public void setIsIncluding(boolean including) {
        this.isIncluding = including;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public boolean getIsIncluding() {
        return this.isIncluding;
    }
}