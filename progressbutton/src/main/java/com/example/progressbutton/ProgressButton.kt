package com.example.progressbutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText

import java.util.*


/**
 * Created by antonmakarenko on 03.07.17.
 */
class ProgressButton : AppCompatButton {
    private var animatedProgressDrawable: ProgressDrawable? = null
    private var backgroundDrawable: GradientDrawable? = null
    private var clipDrawable: ClipDrawable? = null
    private var layerDrawable: LayerDrawable? = null

    private var colorActive: Int = 0
    private var colorInactive: Int = 0
    private var colorProgress: Int = 0

    private var level: Float = 0f
    private var oldLevel: Float = level
    private var realText: CharSequence? = null
    private var realWidth: Int = 0

    private val views: ArrayList<EditText> = ArrayList()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0)

        try {
            colorInactive = a.getColor(R.styleable.ProgressButton_pb_inactiveColor, ContextCompat.getColor(context, R.color.colorBtnInactive))
            colorActive = a.getColor(R.styleable.ProgressButton_pb_activeColor, ContextCompat.getColor(context, R.color.colorBtnHalfProgress))
            colorProgress = a.getColor(R.styleable.ProgressButton_pb_progressColor, ContextCompat.getColor(context, R.color.colorBtnActive))
        } finally {
            a.recycle()
        }

        backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bg_btn_sign_in) as? GradientDrawable
        backgroundDrawable?.setColor(colorInactive)
        clipDrawable = ContextCompat.getDrawable(context, R.drawable.bg_btn_sign_in_clip) as? ClipDrawable
        layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, clipDrawable))
        background = layerDrawable

        isEnabled = false
    }

    fun startProgress() {
        val offset = (width - height) / 2
//        val left = offset + 0
//        val right = width - offset - 0
//        val bottom = height - 0
//        val top = 0

        val center = width/2
        val left = center - height /2
        val right = center + height /2
        val bottom = height - 0
        val top = 0


        if (animatedProgressDrawable == null) {
            animatedProgressDrawable = ProgressDrawable(colorProgress, context.dip(3))
        }

        realWidth = layoutParams.width
        layoutParams.width = layoutParams.height
        realText = text
        text = ""
        isEnableEditTexts(false)

        animatedProgressDrawable?.setBounds(left, top, right, bottom)
        animatedProgressDrawable?.callback = this
        background = animatedProgressDrawable
        animatedProgressDrawable?.start()
        requestLayout()
    }

    fun stopProgress() {
        isEnableEditTexts(true)
        animatedProgressDrawable?.stop()
        text = realText
        layoutParams.width = realWidth
        background = layerDrawable
        requestLayout()
    }

    fun setConnect(list: List<EditText>) {
        for (i in 0 until list.size) {
            list[i].addTextChangedListener(object : TextWatcher {
                var isNotEmpty = false
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isNotEmpty && list[i].text.isNotEmpty() && level < 100) {
                        isNotEmpty = true
                        level += 100f / views.size
                    } else if (isNotEmpty && list[i].text.isEmpty() && level > 0) {
                        isNotEmpty = false
                        level -= 100f / views.size
                    }

                    if (level != oldLevel) {
                        oldLevel = level
                        if (
                        isEditTextsNotEmpty()) {
                            level = Math.round(level).toFloat()
                        }

                        animation(level.toInt())

                        if (level == 100f && !isEnabled) {
                            isEnabled = true
                        } else if (level < 100 && isEnabled) {
                            isEnabled = false
                        }
                    }
                }
            })
            views.add(list[i])
        }
    }

    private fun animation(level: Int) {
        clipDrawable?.let { clipDrawable ->
            val anim = ValueAnimator.ofInt(clipDrawable.level, (level * 100))
            if (level == 0) {
                backgroundDrawable?.setColor(colorInactive)
            } else {
                backgroundDrawable?.setColor(colorActive)
            }
            anim.duration = 500
            anim.addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                clipDrawable.level = progress
            }
            anim.start()
            invalidate()
            requestLayout()
        }
    }

    private fun isEnableEditTexts(enable: Boolean) {
        for (i in 0 until views.size) {
            views[i].isEnabled = enable
        }
    }

    private fun isEditTextsNotEmpty(): Boolean {
        var count = 0
        views.forEach { editText ->
            if (editText.text.isNotEmpty()) {
                count++
            }
        }

        return views.size == count
    }

    private fun Context.dip(value: Int): Float = (value * resources.displayMetrics.density)
}