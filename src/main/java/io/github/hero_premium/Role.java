package io.github.hero_premium;

import com.google.gson.annotations.SerializedName;

public enum Role {

    @SerializedName("user") USER,
    @SerializedName("assistant") ASSISTANT,
    @SerializedName("tool") TOOL,
    @SerializedName("system") SYSTEM,
    USER_FACING
}