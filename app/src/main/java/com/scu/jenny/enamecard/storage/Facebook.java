package com.scu.jenny.enamecard.storage;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jenny on 3/9/16.
 */
public class Facebook {
    public final long userID;
    public final String fbId;
    public final String imageURL;

    public Facebook(long userID, String fbId, String imageURL) {
        if (imageURL.equals("photoPath") || imageURL.equals("url")) {
            throw new IllegalArgumentException("HIIIIIII" + imageURL);
        }
        this.userID = userID;
        this.fbId = fbId;
        this.imageURL = imageURL;
    }

    public JSONObject toJsonObj() {
        JSONObject json = new JSONObject();
        try {
            json.put("mediaType", "facebook");
            json.put("mediaRecordId", this.fbId);
            json.put("imageURL", this.imageURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        return "userID:" + userID + " fbID:" + fbId + " imageURL:" + imageURL;
    }
}
