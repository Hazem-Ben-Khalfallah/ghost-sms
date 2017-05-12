package com.blacknebula.ghostsms.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.dto.SmsDto;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.common.base.Optional;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenSmsActivity extends AbstractCustomToolbarActivity {

    public static final String SMS_DETAILS = "sms_details";

    @BindView(R.id.chat_view)
    ChatView mChatView;

    private User me;
    private User otherUser;
    private SmsDto smsDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensms);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        final Parcelable parcelable = intent.getParcelableExtra(SMS_DETAILS);
        smsDto = Parcels.unwrap(parcelable);
        configureChatView();
        loadConversation();

        //Click Send Button
        mChatView.setOnClickSendButtonListener(getOnClickSendButtonListener());

    }

    private void loadConversation() {
        final List<SmsDto> conversations = SmsUtils.getConversation(this, smsDto.getThreadId(), Optional.absent());

        //User icon
        final Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        me = new User(0, getString(R.string.chat_username_me), myIcon);

        Bitmap otherIcon = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        if (StringUtils.isNotEmpty(smsDto.getPhotoUri())) {
            otherIcon = ViewUtils.toBitmap(this, smsDto.getPhotoUri(), otherIcon);
        }
        otherUser = new User(1, smsDto.getDisplayName(), otherIcon);

        for (SmsDto sms : conversations) {
            final String messageText = readMessage(sms);
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(sms.getDate());
            final Message message;
            if (sms.getType() == SmsUtils.SmsTypes.TYPE_SENT) {
                message = new Message.Builder()
                        .setUser(me)
                        .setRightMessage(true)
                        .setMessageText(messageText)
                        .setCreatedAt(c)
                        .hideIcon(true)
                        .build();
                mChatView.send(message);
            } else {
                message = new Message.Builder()
                        .setUser(otherUser)
                        .setRightMessage(false)
                        .setMessageText(messageText)
                        .setCreatedAt(c)
                        .hideIcon(false)
                        .build();
                mChatView.receive(message);
            }

        }
    }

    private String readMessage(SmsDto smsDto) {
        String message;
        if (smsDto.isEncrypted()) {
            try {
                message = StringUtils.getEmojiByUnicode(StringUtils.locked_emoji) + " " + SmsEncryptionWrapper.decrypt(smsDto.getMessage());
            } catch (Exception e) {
                message = StringUtils.getEmojiByUnicode(StringUtils.warn_emoji) + getString(R.string.message_decryption_fail);
            }
        } else {
            message = smsDto.getMessage();
        }
        return message;
    }

    private View.OnClickListener getOnClickSendButtonListener() {
        return v -> {
            final String messageText = mChatView.getInputText();
            if (StringUtils.isEmpty(messageText)) {
                return;
            }
            if (!SmsSender.isEncryptionEnabled()) {
                sendSms(messageText, null, false);
            } else {
                // custom dialog
                final Dialog dialog = new Dialog(OpenSmsActivity.this);
                dialog.setContentView(R.layout.public_key_fragment);

                final Button okButton = (Button) dialog.findViewById(R.id.ok);
                okButton.setOnClickListener(b -> {
                    // remember key
                    final CheckBox rememberKeyCheckBox = (CheckBox) dialog.findViewById(R.id.rememberKey);
                    final boolean rememberKey = rememberKeyCheckBox.isChecked();
                    // public key value
                    final FloatLabeledEditText rsaKeyWrapper = (FloatLabeledEditText) dialog.findViewById(R.id.rsaKeyWrapper);
                    final String publicKeyBase64 = rsaKeyWrapper.getEditText().getText().toString();
                    //send sms
                    sendSms(messageText, publicKeyBase64, rememberKey);
                    dialog.dismiss();
                });
                final Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
                cancelButton.setOnClickListener(b -> dialog.dismiss());
                dialog.show();
            }

        };
    }

    private void sendSms(String messageText, String publicKey, boolean rememberKey) {
        // send sms
        final boolean result = SmsSender.sendSms(OpenSmsActivity.this, smsDto.getPhone(), messageText, publicKey, rememberKey);
        if (result) {
            //new message
            final Message message = new Message.Builder()
                    .setUser(me)
                    .setRightMessage(true)
                    .setMessageText(messageText)
                    .hideIcon(true)
                    .build();
            //Set to chat view
            mChatView.send(message);
            //Reset edit text
            mChatView.setInputText("");
        }
    }

    private void configureChatView() {
        mChatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.green500));
        mChatView.setLeftBubbleColor(Color.WHITE);
        mChatView.setBackgroundColor(ContextCompat.getColor(this, R.color.chat_background));
        mChatView.setRightMessageTextColor(Color.WHITE);
        mChatView.setLeftMessageTextColor(Color.BLACK);
        mChatView.setUsernameTextColor(Color.WHITE);
        mChatView.setSendTimeTextColor(Color.WHITE);
        mChatView.setDateSeparatorColor(Color.WHITE);
        mChatView.setInputTextHint(getString(R.string.new_message));
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.send_icon));
        mChatView.setOptionIcon(R.mipmap.ic_mode_edit);
        mChatView.setOptionButtonColor(ContextCompat.getColor(this, R.color.option_icon));
    }

}
