package com.scu.jenny.enamecard;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.scu.jenny.enamecard.network.SocketManager;
import com.scu.jenny.enamecard.storage.CurrentUser;
import com.scu.jenny.enamecard.storage.KVStore;

import org.json.JSONException;
import org.json.JSONObject;

public class QRActivity extends AppCompatActivity {
    private static final String QR_CHANNEL = "qr";
    ProgressDialog progress;

    ImageView qrImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
    }

    @Override
    public void onStart() {
        super.onStart();
        SocketManager.getInstance().connect();
        progress = new ProgressDialog(this);

        SocketManager.getInstance().registerListenerOnChannel(CurrentUser.get().phoneNumber, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                QRCodeWriter writer = new QRCodeWriter();
                try {
                    BitMatrix bitMatrix = writer.encode((String)args[0], BarcodeFormat.QR_CODE, 512, 512);
                    int width = bitMatrix.getWidth();
                    int height = bitMatrix.getHeight();
                    final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    qrImage = (ImageView) findViewById(R.id.qr_code_image);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            qrImage.setImageBitmap(bmp);
                        }
                    });
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("secret", KVStore.getInstance().get("secret", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progress.setMessage("Fetching QR code from server ...");
        progress.show();
        SocketManager.getInstance().emit("qr", jsonObject);
    }


    @Override
    public void onStop() {
        super.onStop();
        SocketManager.getInstance().disconnect();
        SocketManager.getInstance().unRegisterListenerFromChannel(CurrentUser.get().phoneNumber);
    }
}
