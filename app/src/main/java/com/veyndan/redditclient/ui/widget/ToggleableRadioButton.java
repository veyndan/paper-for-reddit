package com.veyndan.redditclient.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * A {@link RadioButton} which can be deselected.
 */
public class ToggleableRadioButton extends RadioButton {

    public ToggleableRadioButton(final Context context) {
        super(context);
    }

    public ToggleableRadioButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleableRadioButton(final Context context, final AttributeSet attrs,
                                 final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void toggle() {
        if (isChecked()) {
            if (getParent() instanceof RadioGroup) {
                ((RadioGroup) getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }
}
