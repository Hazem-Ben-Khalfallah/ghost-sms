package com.blacknebula.ghostsms;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blacknebula.ghostsms.utils.Logger;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.message)
    EditText message;
    @InjectView(R.id.encryptedMessage)
    EditText encryptedMessage;
    @InjectView(R.id.encrypt)
    Button encryptButton;
    @InjectView(R.id.decrypt)
    Button decryptButton;

    KeyPair keyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        generateKeys();
    }

    private void generateKeys() {
        try {
            keyPair = Encryptor.generateRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while generating keys");
        }
    }

    @OnClick(R.id.encrypt)
    public void encrypt(View view) {
        try {
            final byte[] encrypted = Encryptor.RSAEncrypt(message.getText().toString(), keyPair.getPublic());
            //final String encryptedMessage = new String(encrypted);
            encryptedMessage.setText(fromBytesToString(encrypted));
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
    }

    @OnClick(R.id.decrypt)
    public void decrypt(View view) {
        try {
            final String encryptedText = encryptedMessage.getText().toString();
            message.setText("");
            final String decryptedText = Encryptor.RSADecrypt(fromStringToBytes(encryptedText), keyPair.getPrivate());
            message.setText(decryptedText);
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while decrypting a message");
        }
    }

    private byte[] fromStringToBytes(String message) {
        // String => byte []
        return message.getBytes(Charset.forName("ISO-8859-1"));
    }

    private String fromBytesToString(byte[] bytes) {
        // byte [] => String
        return new String(bytes, Charset.forName("ISO-8859-1"));
    }

}
