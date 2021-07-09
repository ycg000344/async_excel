package io.github.ycg000344.async.excel.constant;

/**
 * @author lusheng
 * @since 2021-07-09
 */
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
