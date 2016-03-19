package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
        if (scanResult.getContents() == null) {
            finish();
            Intent newIntent = new Intent(ScanActivity.this, MainPageActivity.class);
            startActivity(newIntent);
        } else {
            System.out.println("QR content:" + scanResult.getContents());
            final String qrToken = scanResult.getContents();
            JSONObject reqBody = new JSONObject();
            try {
                reqBody.put("qrToken", qrToken);
                new NetworkAsyncTask(ScanActivity.this, "Adding friend...", new ProcessResponse() {
                    @Override
                    public void process(String jsonRespose) {
                        try {
                            final JSONObject res = new JSONObject(jsonRespose);
                            final String firstName = res.getString("firstName");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanActivity.this, "Request sent to " + firstName, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ScanActivity.this, "Cannot parse server repsonse", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).execute("POST", "/request-share", reqBody.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
