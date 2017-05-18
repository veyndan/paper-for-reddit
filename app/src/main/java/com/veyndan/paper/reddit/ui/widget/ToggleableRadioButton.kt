package com.veyndan.paper.reddit.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 * A [RadioButton] which can be deselected.
 */
class ToggleableRadioButton : RadioButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun toggle() {
        if (isChecked) {
            if (parent is RadioGroup) {
                (parent as RadioGroup).clearCheck()
            }
        } else {
            isChecked = true
        }
    }
}
