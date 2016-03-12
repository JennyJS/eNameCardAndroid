package com.scu.jenny.enamecard;

import android.view.View;

import com.scu.jenny.enamecard.thirdparty.MediaType;

/**
 * Created by jenny on 2/21/16.
 */
public class AdapterConnector {
    public final String url;
    public final View.OnClickListener listener;
    public final MediaType mediaType;

    public AdapterConnector(MediaType mediaType, String url, View.OnClickListener listener){
        this.mediaType = mediaType;
        this.url = url;
        this.listener = listener;
    }
}
