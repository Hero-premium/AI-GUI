package org.hero;

import com.google.gson.annotations.SerializedName;

public enum Roles {

    @SerializedName("user") USER,
    @SerializedName("assistant") ASSISTANT,
    @SerializedName("tool") TOOL,
    @SerializedName("system") SYSTEM

}
