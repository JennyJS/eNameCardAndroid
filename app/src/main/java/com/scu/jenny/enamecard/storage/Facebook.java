package com.scu.jenny.enamecard.storage;

/**
 * Created by jenny on 3/9/16.
 */
public class Facebook {
    public final long userID;
    public final String fbId;
    public final String imageURL;

    public Facebook(long userID, String fbId, String imageURL) {
        this.fbId = fbId;
        this.imageURL = imageURL;
        this.userID = userID;
    }
}
