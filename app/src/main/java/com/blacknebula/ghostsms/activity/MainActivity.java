package com.blacknebula.ghostsms.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blacknebula.ghostsms.GhostSmsApplication;
import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.DecryptionResult;
import com.blacknebula.ghostsms.encryption.Decryptor;
import com.blacknebula.ghostsms.encryption.EncryptionResult;
import com.blacknebula.ghostsms.encryption.Encryptor;
import com.blacknebula.ghostsms.encryption.KeyGenerator;
import com.blacknebula.ghostsms.utils.FileUtils;
import com.blacknebula.ghostsms.utils.Logger;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.message)
    EditText message;
    @InjectView(R.id.encryptedMessage)
    EditText encryptedMessage;
    @InjectView(R.id.encryptedSecretKey)
    EditText encryptedSecretKey;
    @InjectView(R.id.encrypt)
    Button encryptButton;
    @InjectView(R.id.decrypt)
    Button decryptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        generateKeys();
    }

    private void generateKeys() {
        if (!FileUtils.exists(KeyGenerator.PUBLIC_KEY_PATH) || !FileUtils.exists(KeyGenerator.PRIVATE_KEY_PATH)) {
            try {
                KeyGenerator.generateRSAKeys(GhostSmsApplication.getAppContext());
            } catch (Exception e) {
                Logger.error(Logger.Type.GHOST_SMS, e, "Error while generating keys");
            }
        }
    }

    @OnClick(R.id.encrypt)
    public void encrypt(View view) {
        try {
            final EncryptionResult encryptedResult = Encryptor.encrypt(message.getText().toString());
            encryptedMessage.setText(encryptedResult.getEncryptedMessage());
            encryptedSecretKey.setText(encryptedResult.getEncryptedSecretKey());
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while encrypting a message");
        }
    }

    @OnClick(R.id.decrypt)
    public void decrypt(View view) {
        try {
            final String encryptedMessage = this.encryptedMessage.getText().toString();
            final String encryptedSecret = encryptedSecretKey.getText().toString();
            message.setText("");
            final DecryptionResult decryptionResult = Decryptor.decrypt(encryptedMessage, encryptedSecret);
            message.setText(decryptionResult.getMessage());
        } catch (Exception e) {
            Logger.error(Logger.Type.GHOST_SMS, e, "Error while decrypting a message");
        }
    }


}
