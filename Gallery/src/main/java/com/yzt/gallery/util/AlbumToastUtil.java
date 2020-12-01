package com.yzt.gallery.util;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.yzt.gallery.Album;
import com.yzt.gallery.R;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Toast
 *
 * @author yzt 2020/1/30
 */
public class AlbumToastUtil {

    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private static Toast toast;

    static {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {//7.0或7.1
            try {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);
                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception ignored) {

            }
        }
    }

    private static void hook(Toast toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                Object tn = sField_TN.get(toast);
                Handler preHandler = (Handler) sField_TN_Handler.get(tn);
                sField_TN_Handler.set(tn, new SafelyHandlerWrapper(preHandler));
            } catch (Exception ignored) {

            }
        }
    }

    private static class SafelyHandlerWrapper extends Handler {

        private Handler impl;

        SafelyHandlerWrapper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception ignored) {

            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }

    }

    public static void shortTop(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void shortTop(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void shortCenter(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void shortCenter(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void shortBottom(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void shortBottom(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longTop(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longTop(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longCenter(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longCenter(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longBottom(String text) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(text);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(text);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        hook(toast);
        toast.show();
    }

    public static void longBottom(@StringRes int resId) {
        if (Album.get() == null)
            return;

        if (toast != null) {
            AppCompatTextView tv = toast.getView().findViewById(R.id.tv);
            tv.setText(resId);
        } else {
            View view = View.inflate(Objects.requireNonNull(Album.get()).getAlbumContext(), R.layout.layout_toast_album, null);
            AppCompatTextView tv = view.findViewById(R.id.tv);
            tv.setText(resId);
            toast = new Toast(Objects.requireNonNull(Album.get()).getAlbumContext());
            toast.setView(view);
        }
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        hook(toast);
        toast.show();
    }

}