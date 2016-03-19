package com.scu.jenny.enamecard.storage;

import java.util.List;

/**
 * Created by fengjiang on 3/13/16.
 */
public class CurrentUser {
    private static User currentUser;
    private static User.SocialMedia facebook;
    private static User.SocialMedia linkedIn;


    public static User.SocialMedia getFacebook() {
        return facebook;
    }
    public static User.SocialMedia getLinkedIn() {
        return linkedIn;
    }


    public static User get() { return currentUser; }

    public static String getUserFirstName(){
        return currentUser.firstName;
    }
    public static String getUserLastName(){return currentUser.lastName; }

    public static void refreshFromDB(User user) {
        facebook = null;

        currentUser = user;
        List<User.SocialMedia> socialMedias = user.socialMedias;
        for (User.SocialMedia socialMedia : socialMedias) {
            if (socialMedia.mediaType.equals("facebook")) {
                facebook = socialMedia;
            } else if (socialMedia.mediaType.equals("linkedIn")){
                linkedIn = socialMedia;
            }
        }
    }
}
