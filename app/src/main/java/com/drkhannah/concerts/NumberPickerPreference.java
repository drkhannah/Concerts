package com.drkhannah.concerts;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by dhannah on 3/24/17.
 */

public class NumberPickerPreference extends DialogPreference {

    private int mDefaultValue;
    private int mMinValue;
    private int mMaxValue;
    private int mValue;

    private NumberPicker mNumberPicker;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set the dialog's layout resource
        setDialogLayoutResource(R.layout.number_picker_layout);

        //get attribute values in a TypedArray
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPickerPreference, 0, 0);

        try {
            //get values from attributes
            mMinValue = typedArray.getInteger(R.styleable.NumberPickerPreference_minValue, 1);
            mMaxValue = typedArray.getInteger(R.styleable.NumberPickerPreference_maxValue, 10);
        } finally {
            //a TypedArray is a shared resource
            //and must be recycled so it can be reused
            typedArray.recycle();
        }
    }

    //bind data to views in dialog
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.pref_num_picker);
        mNumberPicker.setMinValue(mMinValue);
        mNumberPicker.setMaxValue(mMaxValue);
        mNumberPicker.setValue((int) Utils.getSyncInterval(getContext()));
        mNumberPicker.setWrapSelectorWheel(false);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        mDefaultValue = a.getInteger(index, 1);
        return a.getInteger(index, mDefaultValue);
    }

    //set initial value in default shared preferences
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(mDefaultValue);
        } else {
            mValue = (Integer) defaultValue;
            persistInt(mValue);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        //was positive button clicked?
        if (positiveResult) {
            //save data to default shared preferences
            mValue = mNumberPicker.getValue();
            persistInt(mValue);
        }
    }
}
