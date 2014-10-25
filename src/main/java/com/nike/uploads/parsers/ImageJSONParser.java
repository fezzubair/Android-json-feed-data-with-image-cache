package com.nike.uploads.parsers;

import android.util.Log;

import com.nike.uploads.model.ImageUpload;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fez on 7/28/2014.
 */
public class ImageJSONParser {

    private static final String LOGTAG = "NIKE";

    public static List<ImageUpload> parseFeed(String content) {

        try {
            int j = content.indexOf("{");
            if(j == -1)
                return null;
            JSONObject mainObj = new JSONObject(content.substring(j));
            JSONArray ar = new JSONArray(mainObj.getString("items"));
            List<ImageUpload> imageList = new ArrayList<ImageUpload>();

            for (int i = 0; i < ar.length(); i++) {

                JSONObject obj = ar.getJSONObject(i);
                ImageUpload image = new ImageUpload();

//                image.setId(i);
                image.setTitle(obj.getString("title"));
                image.setLink(obj.getString("link"));

                JSONObject media = obj.getJSONObject("media");
                String mUrl = media.getString("m");
                image.setMediaUrl(mUrl);

                URL mediaURL = null;
                try {
                    mediaURL = new URL(mUrl);
                }
                catch (MalformedURLException e){
                    Log.i(LOGTAG, "----------------///////////////// Malformed URL EXCEPTION ");
                }

                int length = mediaURL.getFile().length();

                String fileStr = mediaURL.getFile();

                int z=0;
                for(z=length-1 ; fileStr.charAt(z) != '/' ; z-- );

                String str = fileStr.substring((length - z)/2);
                String MediaNameForKey = str.replace(".jpg","");
//                String[] tokens = mediaURL.getFile().split("/");
                image.setMediaName(MediaNameForKey);

                Log.i(LOGTAG,"----------------" + MediaNameForKey);

                image.setDateTaken(obj.getString("date_taken"));
                image.setDescription(obj.getString("description"));
                image.setPublished(obj.getString("published"));
                image.setAuthor(obj.getString("author"));
                image.setAuthorId(obj.getString("author_id"));
                image.setTags(obj.getString("tags"));

                imageList.add(image);
            }

            return imageList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

}
