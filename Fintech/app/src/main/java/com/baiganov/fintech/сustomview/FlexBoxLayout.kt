package com.baiganov.fintech.сustomview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.*
import com.baiganov.fintech.R
import com.baiganov.fintech.model.Reaction

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var layoutWidth = 0

    fun setReactions(reactions: List<Reaction>) {
        reactions.forEach { reaction ->
            addEmojiViewByReaction(reaction)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingEnd - paddingStart
        var heightUsed = 0
        var widthUsed = 0
        var maxHeightInLine = 0
        var maxWidthUsed = 0
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        children.forEach { child ->
            val childWidth = if (child.isGone) 0 else child.measuredWidth + child.marginStart + child.marginEnd
            val childHeight = if (child.isGone) 0 else child.measuredHeight + child.marginTop + child.marginBottom
            if (widthUsed + childWidth > layoutWidth) {
                maxWidthUsed = maxOf(widthUsed, maxWidthUsed)
                widthUsed = 0
                heightUsed += maxHeightInLine
                maxHeightInLine = 0
            }
            maxHeightInLine =
                maxOf(maxHeightInLine, childHeight)
            widthUsed +=
                childWidth
        }

        setMeasuredDimension(
            resolveSize(maxWidthUsed + paddingStart + paddingEnd, widthMeasureSpec),
            resolveSize(
                maxHeightInLine + heightUsed + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var heightUsed = 0
        var widthUsed = 0
        var maxHeightInLine = 0
        children.forEach { child ->
            val childWidth = child.measuredWidth + child.marginStart + child.marginEnd
            val childHeight = child.measuredHeight + child.marginTop + child.marginBottom
            maxHeightInLine =
                maxOf(maxHeightInLine, childHeight)
            if (widthUsed + childWidth > layoutWidth) {
                widthUsed = 0
                heightUsed += maxHeightInLine
                maxHeightInLine = 0
            }
            child.layout(
                widthUsed + child.marginStart + paddingStart,
                heightUsed + child.marginTop + paddingTop,
                widthUsed + child.measuredWidth + child.marginStart + paddingStart,
                heightUsed + child.measuredHeight + child.marginTop + paddingTop
            )
            widthUsed += childWidth
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    private fun addEmojiViewByReaction(reaction: Reaction) {
        addView(
            (LayoutInflater.from(context).inflate(R.layout.emoji_view_item, this, false) as EmojiView).apply {
                setEmoji(reaction.emoji)
                reactionCount = reaction.count
                isSelected = false
                setOnClickListener{
                    it.isSelected = !it.isSelected
                }
            }, childCount - 1
        )
    }
}
