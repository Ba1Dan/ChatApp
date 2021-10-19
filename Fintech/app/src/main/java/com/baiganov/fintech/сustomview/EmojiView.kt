package com.baiganov.fintech.сustomview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.withTranslation
import com.baiganov.fintech.R


class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var contentSize: Float
    private var emoji: String = "\uD83D\uDE04"
    private val tempFontMetrics = Paint.FontMetrics()
    private var reactionCountBounds = Rect()
    private val textCoordinate = PointF()

    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = context.resources.getDimension(R.dimen.small_size)
        color = Color.WHITE
    }

    private var emojiLayout = StaticLayout.Builder
        .obtain(emoji, 0, emoji.length, textPaint, EMOJI_LAYOUT_WIDTH)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setLineSpacing(0f, 1f)
        .setIncludePad(false)
        .build()

    var reactionCount: Int = 5
        set(value) {
            if (value != reactionCount) {
                field = value
                requestLayout()
            }
        }

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.EmojiView,
            defStyleAttr,
            defStyleRes
        )

        reactionCount = typedArray.getInt(R.styleable.EmojiView_reactionCount, DEF_REACTION_COUNT)
        contentSize = typedArray.getDimension(
            R.styleable.EmojiView_textSize,
            context.resources.getDimension(R.dimen.small_size)
        )
        textPaint.color = typedArray.getColor(R.styleable.EmojiView_textColor, Color.WHITE)

        typedArray.recycle()
    }

    fun setEmoji(newEmoji: String) {
        emoji = newEmoji
        emojiLayout = StaticLayout.Builder
            .obtain(emoji, 0, emoji.length, textPaint, EMOJI_LAYOUT_WIDTH)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        textPaint.getTextBounds(
            reactionCount.toString(),
            0,
            reactionCount.toString().length,
            reactionCountBounds
        )

        val totalWidth =
            resolveSize(
                emojiLayout.width + reactionCountBounds.width() + paddingStart + paddingEnd,
                widthMeasureSpec
            )
        val totalHeight =
            resolveSize(
                emojiLayout.height + paddingTop + paddingBottom,
                heightMeasureSpec
            )

        setMeasuredDimension(totalWidth, totalHeight)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textCoordinate.x = (w - reactionCountBounds.width() - paddingEnd).toFloat()
        textCoordinate.y = h / 2f + reactionCountBounds.height() / 2 - tempFontMetrics.descent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withTranslation(
            emojiLayout.width / 2f,
            (paddingTop.toFloat() + paddingBottom.toFloat()) / 2f
        ) {
            emojiLayout.draw(this)
        }
        canvas.drawText(
            reactionCount.toString(),
            textCoordinate.x,
            textCoordinate.y,
            textPaint
        )
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) {
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        }
        return drawableState
    }

//    override fun performClick(): Boolean {
//        super.performClick()
//        isSelected = !isSelected
//        return true
//    }

    fun updateEmojiViewOnClick() {
        if (isSelected) {
            reactionCount += 1
        } else {
            reactionCount -= 1
        }
    }

    companion object {
        private const val DEF_REACTION_COUNT = 0
        private const val EMOJI_LAYOUT_WIDTH = 50
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }
}
