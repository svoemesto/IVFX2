package com.svoemesto.ivfx.tables;

public class IVFXAndOr {
    private boolean isAnd;
    private String name;

    public IVFXAndOr() {
    }

    public void setIsAnd(boolean isAnd) {
        this.isAnd = isAnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public boolean getIsAnd() {
        return this.isAnd;
    }
}