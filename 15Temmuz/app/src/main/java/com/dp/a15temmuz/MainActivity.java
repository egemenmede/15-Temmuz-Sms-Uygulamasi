package com.dp.a15temmuz;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String SENT = "SMS_SENT";
    Button btnGonder;
    ProgressDialog progress;
    PendingIntent sentPI;
    BroadcastReceiver smsSentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideTitle();

        sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);

        btnGonder = (Button) findViewById(R.id.button);
        btnGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Bilgi")
                        .setMessage("Bu mesajı gönderdiğinizde faturanıza 5TL yansıyacaktır. Kabul ediyor musunuz?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progress = ProgressDialog.show(MainActivity.this, "SMS Gönderiliyor", "Bekleyiniz..", true);
                                smsGonder();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (progress.isShowing()) {
                    progress.dismiss();
                }

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(), "SMS Gönderildi", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "SMS Gönderilemedi", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getApplicationContext(), "SMS Gönderilemedi", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "SMS Gönderilemedi", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getApplicationContext(), "SMS Gönderilemedi", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }

    public void hideTitle() {
        try {
            ((View) findViewById(android.R.id.title).getParent()).setVisibility(View.GONE);
        } catch (Exception e) {
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    private void smsGonder(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("1507", null, "15 Temmuz",sentPI,null);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(smsSentReceiver);
    }
}
