package com.scu.jenny.enamecard.storage;

import java.util.List;

/**
 * Created by fengjiang on 3/13/16.
 */
public class CurrentUser {
    private static User.SocialMedia facebook;

    public static User.SocialMedia getFacebook() {
        return facebook;
    }

    public static void refreshFromDB(User user) {
        facebook = null;
        List<User.SocialMedia> socialMedias = user.socialMediaList;
        for (User.SocialMedia socialMedia : socialMedias) {
            if (socialMedia.mediaType.equals("facebook")) {
                facebook = socialMedia;
            }
        }
    }
}
