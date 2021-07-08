package io.github.ycg000344.async.excel.constant;

public enum ParseEnum {

    IMPORT("import"),

    EXPORT("export");

    private final String value;

    ParseEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
