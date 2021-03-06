package com.scu.jenny.enamecard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.network.SocketManager;
import com.scu.jenny.enamecard.storage.CurrentUser;
import com.scu.jenny.enamecard.storage.DrawableManager;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONException;
import org.json.JSONObject;

public class QRActivity extends AppCompatActivity {
    private static final String QR_CHANNEL = "qr";
    ProgressDialog progress;

    ImageView qrImage;
    ImageView profileImage;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        textView = (TextView) findViewById(R.id.qr_text_view);
        profileImage = (ImageView) findViewById(R.id.qr_profile_image);
        textView.setText(CurrentUser.getUserFirstName() + "'s QR code");
        loadProfileImage();

    }

    private void loadProfileImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (CurrentUser.get().imageURL != null) {
                    DrawableManager.getInstance().fetchDrawableOnThread(CurrentUser.get().imageURL, profileImage);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        SocketManager.getInstance().connect();
        progress = new ProgressDialog(this);

        SocketManager.getInstance().registerListenerOnChannel(CurrentUser.get().phoneNumber, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String res = (String)args[0];
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    final String type = jsonObject.getString("type");
                    final String data = jsonObject.getString("data");

                    if (type.equals("qrToken")) {
                        QRCodeWriter writer = new QRCodeWriter();
                        try {
                            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
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
                    } else if (type.equals("shareRequest")) {
                        JSONObject reqUserJson = new JSONObject(data);
                        final User reqUser = User.getUserFromJsonObj(reqUserJson);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(QRActivity.this);
                        builder.setMessage(reqUser.firstName + " requests your name card" + ". Accept?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Send server that invitation is accepted
                                JSONObject reqJson = new JSONObject();
                                try {
                                    reqJson.put("phoneNumber", reqUser.phoneNumber);
                                    new NetworkAsyncTask(QRActivity.this, "Accepting Invitation", new ProcessResponse() {
                                        @Override
                                        public void process(String jsonRespose) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                }
                                            });
                                        }
                                    }).execute("POST", "/accept-share", reqJson.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                } catch (JSONException e) {
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
