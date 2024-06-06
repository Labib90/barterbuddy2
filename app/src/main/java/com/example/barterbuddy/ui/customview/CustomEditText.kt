package com.example.barterbuddy.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.barterbuddy.R


class CustomEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = ContextCompat.getDrawable(context, R.drawable.custom_edit_text) as Drawable
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }


    private fun init() {
        addTextChangedListener(onTextChanged = { s,_,_,_ ->
            if (s.toString().length < 8) {
                error = "Password tidak boleh kurang dari 8 karakter"
            }
        })
    }

}