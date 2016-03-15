package com.scu.jenny.enamecard.storage;

import java.util.List;

/**
 * Created by fengjiang on 3/13/16.
 */
public class CurrentUser {
    private static User currentUser;
    private static User.SocialMedia facebook;

    public static User.SocialMedia getFacebook() {
        return facebook;
    }

    public static User get() { return currentUser; }

    public static void refreshFromDB(User user) {
        facebook = null;

        currentUser = user;
        List<User.SocialMedia> socialMedias = user.socialMedias;
        for (User.SocialMedia socialMedia : socialMedias) {
            if (socialMedia.mediaType.equals("facebook")) {
                facebook = socialMedia;
            }
        }
    }
}
