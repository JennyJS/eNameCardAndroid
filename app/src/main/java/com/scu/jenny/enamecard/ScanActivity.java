package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONException;
import org.json.JSONObject;

public class ScanActivity extends AppCompatActivity {
    IntentIntegrator integrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        System.out.println("QR content:" + scanResult.getContents());
        try {
            final JSONObject jsonObject = new JSONObject(scanResult.getContents());
            if (jsonObject.has("type") && jsonObject.get("type").equals("qrToken") && jsonObject.has("data")) {
                String qrToken = jsonObject.getString("data");
                JSONObject reqBody = new JSONObject();
                reqBody.put("qrToken", qrToken);
                new NetworkAsyncTask(ScanActivity.this, "Adding friend...", new ProcessResponse() {
                    @Override
                    public void process(String jsonRespose) {
                        try {
                            JSONObject res = new JSONObject(jsonRespose);
                            Toast.makeText(ScanActivity.this, "Request sent to " + res.getString("firstName"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(ScanActivity.this, "Cannot parse server repsonse", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).execute("POST", "/request-share", reqBody.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
