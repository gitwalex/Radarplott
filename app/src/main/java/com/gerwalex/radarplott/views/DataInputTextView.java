package com.gerwalex.radarplott.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import java.util.Objects;

/**
 * Zeigt einen Betrag in der jeweiligen Waehrung an. Als Defult wird bei negativen Werten der Text
 * in rot gezeigt.
 */
public class DataInputTextView extends AppCompatEditText {
    private InverseBindingListener mBindingListener;
    private float value;

    @InverseBindingAdapter(attribute = "value")
    public static float getValue(DataInputTextView view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(DataInputTextView view, float value, InverseBindingListener listener) {
        view.mBindingListener = listener;
        view.setValue(value);
    }

    public DataInputTextView(Context context) {
        super(context);
        init();
    }

    public DataInputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DataInputTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public float getValue() {
        return value;
    }

    /**
     * Setzt einen long-Wert als Text. Dieser wird in das entsprechende Currency-Format
     * umformatiert.
     *
     * @param value Wert zur Anzeige
     */
    @CallSuper
    @MainThread
    public void setValue(float value) {
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            if (mBindingListener != null) {
                mBindingListener.onChange();
            }
            setText(String.valueOf(value));
        }
    }

    private void init() {
        if (isInEditMode()) {
            setValue(123_456_789);
        }
        setEms(7);
        setSelectAllOnFocus(true);
        setCursorVisible(false);
    }

    public void postValue(float value) {
        post(() -> setValue(value));
    }

    public void setValueChangeListener(InverseBindingListener listener) {
        mBindingListener = listener;
    }
}
