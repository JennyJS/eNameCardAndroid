package com.scu.jenny.enamecard.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by jenny on 2/28/16.
 */
public class NetworkAsyncTask extends AsyncTask<String, Integer, Double> {
    private Activity activity;
    private ProgressDialog dialog;
    private final String progressMessage;
    private final ProcessResponse callback;

    public NetworkAsyncTask(Activity activity, String progressMessage, ProcessResponse callback) {
        this.activity = activity;
        dialog = new ProgressDialog(this.activity);
        this.progressMessage = progressMessage;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage(progressMessage);
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(Double result){
        if (this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
    }

    @Override
    protected Double doInBackground(String... params) {
        String httpMethod = params[0];
        String response = null;
        try {
            switch (httpMethod) {
                case "GET":
                    response = HttpHelper.sendGet(params[1]);
                    break;
                case "POST":
                    response = HttpHelper.sendPost(params[1], params[2]);
                    break;
                case "PUT":
                    respone = HttpHelper.sendP
                case "DELETE":
            }
            if (response != null) {
                this.callback.process(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress){
        System.out.println("Progress:" + progress[0]);
    }
}
