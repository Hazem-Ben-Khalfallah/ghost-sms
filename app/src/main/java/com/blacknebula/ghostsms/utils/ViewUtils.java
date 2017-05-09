package com.blacknebula.ghostsms.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.blacknebula.ghostsms.R;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hazem
 */
public class ViewUtils {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static final float NORMAL = 0.1f;
    private static final float FOCUS = 1f;

    /**
     * Generate a value suitable for use in {@link View#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setFocus(View view, Boolean isFocused) {
        if (isFocused) {
            view.setAlpha(FOCUS);
        } else {
            view.setAlpha(NORMAL);
        }
    }

    public static Bitmap toBitmap(Context context, String uri, Bitmap defaultBitmap) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
        } catch (IOException e) {
            return defaultBitmap;
        }
    }

    public static Bitmap toBitmap(Context context, Uri uri, Bitmap defaultBitmap) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            return defaultBitmap;
        }
    }

    public static void showToast(final Context context, final String message) {
        showToast(context, message, false);
    }

    public static void showToast(final Context context, final String message, final Boolean isOnTop) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                if (isOnTop)
                    toast.setGravity(Gravity.TOP | Gravity.END, 0, 0);
                else
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }


    public static AlertDialog openUniqueActionDialog(Context context, int title, int message, final OnClickListener onClickListener) {
        final OnActionListener onActionListener = new OnActionListener() {
            @Override
            public void onPositiveClick() {
                onClickListener.onClick();
            }

            @Override
            public void onNegativeClick() {

            }
        };
        return openDialog(context, title, message, false, android.R.string.ok, android.R.string.no, R.mipmap.ic_info_outline, onActionListener);
    }

    public static AlertDialog openDialog(Context context, int title, int message, OnActionListener onActionListener) {
        return openDialog(context, title, message, true, android.R.string.ok, android.R.string.no, R.mipmap.ic_info_outline, onActionListener);
    }

    public static AlertDialog openDialog(Context context, int title, int message, int positiveText, int negativeText, OnActionListener onActionListener) {
        return openDialog(context, title, message, true, positiveText, negativeText, R.mipmap.ic_info_outline, onActionListener);
    }

    public static AlertDialog openDialog(Context context, int title, int message, boolean hasNegativeOption, int positiveText, int negativeText, int icon, final OnActionListener onActionListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setCancelable(false)
                .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onActionListener.onPositiveClick();
                    }
                });

        if (hasNegativeOption) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    onActionListener.onNegativeClick();
                }
            });
        }

        return builder.show();
    }

    public interface OnActionListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    public interface OnClickListener {
        void onClick();
    }


}