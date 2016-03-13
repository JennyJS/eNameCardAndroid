package com.scu.jenny.enamecard.storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jenny on 3/9/16.
 */
public class User {

    public final String firstName;
    public final String lastName;
    public final String phoneNumber;
    public final String imageURL;
    public final List<SocialMedia> socialMediaList;

    public User(String firstName, String lastName, String phoneNumber, String imageURL, List<SocialMedia> socialMediaList) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
        this.socialMediaList = socialMediaList;
    }

    public static class SocialMedia {
        public final String mediaType;
        public final String mediaRecordId;
        public final String imageURL;

        public SocialMedia(String mediaType, String mediaRecordId, String imageURL) {
            this.mediaType = mediaType;
            this.mediaRecordId = mediaRecordId;
            this.imageURL = imageURL;
        }
    }

    public static User getUserFromJsonObj(JSONObject jsonObject) throws JSONException {
        String firstName = jsonObject.optString("firstName", null);
        String lastName = jsonObject.optString("lastName", null);
        String phoneNumber = jsonObject.getString("phoneNumber");
        String profileImageURL = jsonObject.optString("imageURL", null);

        List<SocialMedia> socialMedias = new ArrayList<>();
        if (jsonObject.has("socialMedias")) {
            JSONArray jsonArray = jsonObject.getJSONArray("socialMedias");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = (JSONObject) jsonArray.get(i);
                String mediaType = obj.getString("mediaType");
                String mediaRecordId = obj.getString("mediaRecordId");
                String imageURL = obj.getString("imageURL");
                SocialMedia socialMedia = new SocialMedia(mediaType, mediaRecordId, imageURL);
                socialMedias.add(socialMedia);
            }
        }

        return new User(firstName, lastName, phoneNumber, profileImageURL, socialMedias);
    }
}
