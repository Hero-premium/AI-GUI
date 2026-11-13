package io.github.hero_premium.tools;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

public enum PropertiesType {

    @SerializedName("array") ARRAY(List.class),
    @SerializedName("number") NUMBER(Double.class),
    @SerializedName("integer") INTEGER(Double.class),
    @SerializedName("object") OBJECT(Map.class),
    @SerializedName("string") STRING(String.class),
    @SerializedName("boolean") BOOLEAN(Boolean.class),
    @SerializedName("null") NULL(null);

    private final Class<?> type;

    PropertiesType(Class<?> type) {
        this.type = type;
    }

    @Contract(pure = true)
    public Class<?> getType() {
        return type;
    }
}