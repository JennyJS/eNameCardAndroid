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

    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String imageURL;
    public final List<SocialMedia> socialMedias;

    public User(String firstName, String lastName, String phoneNumber, String imageURL, List<SocialMedia> socialMedias) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imageURL = imageURL;
        this.socialMedias = socialMedias;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();

        try {
            if (firstName != null) {
                jsonObject.put("firstName", firstName);
            }

            if (lastName != null) {
                jsonObject.put("lastName", lastName);
            }

            if (phoneNumber != null) {
                jsonObject.put("phoneNumber", phoneNumber);
            }

            if (imageURL != null) {
                jsonObject.put("imageURL", imageURL);
            }

            if (socialMedias != null && socialMedias.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                jsonObject.put("socialMedias", jsonArray);
                for (SocialMedia socialMedia : socialMedias) {
                    jsonArray.put(socialMedia.toJsonObj());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
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

        public JSONObject toJsonObj() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("imageURL", imageURL);
                jsonObject.put("mediaType", mediaType);
                jsonObject.put("mediaRecordId", mediaRecordId);
            } catch (JSONException e) {
                return null;
            }
            return jsonObject;
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
