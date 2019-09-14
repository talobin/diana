package com.haivo.diana.View

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.haivo.diana.R

/**
 * TODO: document your custom view class.
 */
class Button : LinearLayout {
    private var buttonText: String?
    private var buttonVariant: String?
    private var buttonGhost: Boolean

    private val buttonView: View
    private val textView: TextView

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr, 0)

        buttonText = a.getString(R.styleable.Button_text)

        buttonVariant = a.getString(R.styleable.Button_variant)

        buttonGhost = a.getBoolean(R.styleable.Button_ghost, false)

        a.recycle()

        buttonView = View.inflate(context, R.layout.view_button, this)
        textView = buttonView.findViewById(R.id.button)
        textView.text = buttonText
        textView.setOnTouchListener { _, motionEvent ->
            buttonView.onTouchEvent(motionEvent)
            false
        }

        buttonView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        updateStyle()
    }

    fun updateStyle() {
        if (!buttonGhost) {
            when (buttonVariant) {
                context.getString(R.string.button_variant_white) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_white)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
                context.getString(R.string.button_variant_accent) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_accent)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
                context.getString(R.string.button_variant_danger) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_danger)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorDanger))
                }
                context.getString(R.string.button_variant_fade) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_white)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                }
            }
        } else {
            when (buttonVariant) {
                context.getString(R.string.button_variant_white) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_ghost_white)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
                }
                context.getString(R.string.button_variant_accent) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_ghost_accent)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                }
                context.getString(R.string.button_variant_danger) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_ghost_danger)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorDanger))
                }
                context.getString(R.string.button_variant_fade) -> {
                    buttonView.background = context.getDrawable(R.drawable.button_bg_ghost_white)
                    textView.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary))
                }
            }
        }
    }

    fun setText(text: String) {
        textView.text = text
    }
}
