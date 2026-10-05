package org.hero.tools;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public enum PropertiesType {
    @SerializedName("array") ARRAY(List.class),
    @SerializedName("integer") INTEGER(Long.class),
    @SerializedName("number") NUMBER(Double.class),
    @SerializedName("object") OBJECT(Map.class),
    @SerializedName("string") STRING(String.class),
    @SerializedName("boolean") BOOLEAN(Boolean.class),
    @SerializedName("null") NULL(Void.class);

    private final Class<?> clazz;

    PropertiesType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getType() {
        return clazz;
    }
}
