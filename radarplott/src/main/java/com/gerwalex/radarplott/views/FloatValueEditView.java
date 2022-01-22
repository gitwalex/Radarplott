package com.gerwalex.radarplott.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.gerwalex.radarplott.R;

import java.util.Objects;

/**
 * Eingabe eines Float-Wertes.
 */
public class FloatValueEditView extends AppCompatEditText {
    private int decimalPlaces;
    private InverseBindingListener mBindingListener;
    private float value;

    @InverseBindingAdapter(attribute = "value")
    public static float getValue(FloatValueEditView view) {
        return view.getValue();
    }

    @BindingAdapter(value = {"value", "valueAttrChanged"}, requireAll = false)
    public static void setValue(FloatValueEditView view, Float value, InverseBindingListener listener) {
        view.setValueChangeListener(listener);
        view.setValue(value == null ? 0 : value);
    }

    public FloatValueEditView(Context context) {
        super(context);
        init(context, null);
    }

    public FloatValueEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatValueEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public float getValue() {
        return value;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.FloatValueEditViewStyle, android.R.attr.editTextStyle,
                        R.style.FloatValueEditView);
        try {
            decimalPlaces = a.getInt(R.styleable.FloatValueEditViewStyle_decimalPlaces, 0);
            int type = InputType.TYPE_CLASS_NUMBER;
            if (decimalPlaces != 0) {
                type = type | InputType.TYPE_NUMBER_FLAG_DECIMAL;
            }
            setInputType(type);
        } finally {
            a.recycle();
        }
        if (isInEditMode()) {
            setValue(123_456_789);
        }
        setEms(7);
        setSelectAllOnFocus(true);
        setCursorVisible(false);
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
            String txt;
            if (decimalPlaces == 0) {
                int val = (int) value;
                txt = String.valueOf(val);
            } else {
                txt = String.valueOf(value);
            }
            setText(txt);
        }
    }

    public void setValueChangeListener(InverseBindingListener listener) {
        mBindingListener = listener;
        addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    FloatValueEditView.this.value = Float.parseFloat(s.toString());
                    if (mBindingListener != null) {
                        mBindingListener.onChange();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }
}
