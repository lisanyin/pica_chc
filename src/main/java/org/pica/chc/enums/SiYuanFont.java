package org.pica.chc.enums;

/**
 * @description
 * @author: lisanyin
 * @create: 2019/12/10
 **/
public enum SiYuanFont {
    BOLD(1, "BOLD"), EXTRALIGHT(2, "EXTRALIGHT"), HEAVY(3, "HEAVY"),
    LIGHT(4, "LIGHT"), MEDIUM(5, "MEDIUM"), REGULAR(6, "REGULAR"), SEMIBOLD(7, "SEMIBOLD");
    private int code;
    private String fontName;

    SiYuanFont(int code, String fontName) {
        this.code = code;
        this.fontName = fontName;
    }

    public int code() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
