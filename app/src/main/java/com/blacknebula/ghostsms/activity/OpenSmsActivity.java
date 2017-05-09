package com.blacknebula.ghostsms.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blacknebula.ghostsms.R;
import com.blacknebula.ghostsms.encryption.SmsEncryptionWrapper;
import com.blacknebula.ghostsms.utils.SmsUtils;
import com.blacknebula.ghostsms.utils.StringUtils;
import com.blacknebula.ghostsms.utils.ViewUtils;
import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.common.base.Optional;

import org.parceler.Parcels;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenSmsActivity extends AppCompatActivity {

    public static final String SMS_DETAILS = "sms_details";

    @BindView(R.id.chat_view)
    ChatView mChatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opensms);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        final Parcelable parcelable = intent.getParcelableExtra(SMS_DETAILS);
        final SmsDto smsDto = Parcels.unwrap(parcelable);
        final List<SmsDto> conversations = SmsUtils.getConversation(this, smsDto.getThreadId(), Optional.<Long>absent());

        //User id
        int myId = 0;
        //User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        //User name
        String myName = "Me";
        final User me = new User(myId, myName, myIcon);

        int otherId = 1;
        Bitmap otherIcon = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        if (StringUtils.isNotEmpty(smsDto.getPhotoUri())) {
            otherIcon = ViewUtils.toBitmap(this, smsDto.getPhotoUri(), otherIcon);
        }
        String otherName = smsDto.getDisplayName();
        final User otherUser = new User(otherId, otherName, otherIcon);

        configureChatView();


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


        //Click Send Button
        mChatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRightMessage(true)
                        .setMessageText(mChatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                mChatView.send(message);
                //Reset edit text
                mChatView.setInputText("");
            }

        });

    }

    private String readMessage(SmsDto smsDto) {
        String message;
        if (smsDto.isEncrypted()) {
            try {
                message = StringUtils.getEmojiByUnicode(StringUtils.locked_emoji) + " " + SmsEncryptionWrapper.decrypt(smsDto.getMessage());
            } catch (Exception e) {
                message = StringUtils.getEmojiByUnicode(StringUtils.warn_emoji) + " oops, something went wrong!";
            }
        } else {
            message = smsDto.getMessage();
        }
        return message;
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
        mChatView.setInputTextHint("new message ...");
        mChatView.setMessageMarginTop(5);
        mChatView.setMessageMarginBottom(5);
        mChatView.setSendIcon(R.drawable.ic_action_send);
        mChatView.setSendButtonColor(ContextCompat.getColor(this, R.color.send_icon));
        mChatView.setOptionIcon(R.mipmap.ic_mode_edit);
        mChatView.setOptionButtonColor(ContextCompat.getColor(this, R.color.option_icon));
    }

}
