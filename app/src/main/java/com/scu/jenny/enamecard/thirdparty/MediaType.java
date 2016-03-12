package com.scu.jenny.enamecard.thirdparty;

/**
 * Created by fengjiang on 3/12/16.
 */
public enum MediaType {
    FACEBOOK("#47639C", "icon_facebook.png"),
    TWITTER("#2AA3EF",  "icon_twitter.png"),
    LINKEDIN("#2386BA", "icon_linkedin.png"),
    QUORA("#B72D2C",    "icon_qora.png");

    public final String color;
    public final String iconName;

    private MediaType(String color, String iconName) {
        this.color = color;
        this.iconName = iconName;
    }
}
