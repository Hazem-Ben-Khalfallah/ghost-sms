package com.blacknebula.ghostsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.Logger;
import com.blacknebula.ghostsms.utils.StringUtils;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            final StringBuffer message = new StringBuffer();
            String phone = "";
            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                phone = smsMessage.getOriginatingAddress();
                message.append(smsMessage.getMessageBody());

            }
            if (StringUtils.isNotEmpty(phone)) {
                String decrypted = "Could not decrypt";
                try {
                    decrypted = SmsEncryptionWrapper.decrypt(message.toString());
                } catch (Exception e) {
                    Logger.error(Logger.Type.GHOST_SMS, e, "Error while decrypting the SMS");
                }
                Toast.makeText(context, phone + ": " + decrypted, Toast.LENGTH_SHORT).show();
                Logger.info(Logger.Type.GHOST_SMS, "Sender: %s: \n Original: %s \n Decrypted: %s", phone, message, decrypted);
            }
        }
    }
}