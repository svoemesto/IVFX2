package com.svoemesto.ivfx.tables;

import java.util.ArrayList;
import java.util.List;

public enum IVFXEnumTagsTypes {
    DESCRIPTION,
    PERSON,
    OBJECT,
    SCENE,
    EVENT;

    public static List<IVFXEnumTagsTypes> loadList() {
        List<IVFXEnumTagsTypes> list = new ArrayList<>();
        list.add(DESCRIPTION);
        list.add(PERSON);
        list.add(OBJECT);
        list.add(SCENE);
        list.add(EVENT);
        return list;
    }
}
