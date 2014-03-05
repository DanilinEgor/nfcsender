package com.example.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

import static android.nfc.NdefRecord.createMime;

public class MyActivity extends Activity implements View.OnClickListener {
    TextView textView;
    EditText editText;
    Button b;
    NfcAdapter mNfcAdapter;

    private AutoUpdateApk aua;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        aua = new AutoUpdateApk(getApplicationContext());
        aua.setUpdateInterval(5 * AutoUpdateApk.DAYS);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(this);
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public NdefMessage createNdefMessage() {
        String text = editText.getText().toString();
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/com.example.nfctest", text.getBytes())
                });
        return msg;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textView.setText("Received: " + new String(msg.getRecords()[0].getPayload()));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button) {
            mNfcAdapter.setNdefPushMessage(createNdefMessage(), this);
        }
    }

}
