package com.baiganov.fintech.сustomview

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.*
import com.baiganov.fintech.R
import com.baiganov.fintech.model.Reaction
import com.google.android.material.imageview.ShapeableImageView

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val avatarView: ShapeableImageView
    private var avatarSize = 0

    private val messageLayout: LinearLayout
    private var messageWidth = 0
    private var messageHeight = 0

    private val messageText: TextView
    private val authorText: TextView

    private val flexBox: FlexBoxLayout
    private var flexBoxWidth = 0
    private var flexBoxHeight = 0

    private val addReactionButton: View

    var text: String
        get() = messageText.text.toString()
        set(value) {
            messageText.text = value
            requestLayout()
        }

    var author: String
        get() = authorText.text.toString()
        set(value) {
            authorText.text = value
            requestLayout()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.message_view_group_layout, this)
        avatarView = findViewById(R.id.avatar_view)
        messageLayout = findViewById(R.id.message_linear_layout)
        authorText = findViewById(R.id.profile_name_text_view)
        messageText = findViewById<TextView>(R.id.message_text_incoming).apply {
            movementMethod = LinkMovementMethod.getInstance()
        }
        flexBox = findViewById(R.id.flex_box)
        addReactionButton = findViewById(R.id.add_reaction_button_incoming)

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MessageViewGroup,
            defStyleAttr,
            defStyleRes
        )

        text = typedArray.getString(R.styleable.MessageViewGroup_text).orEmpty()
        author = typedArray.getString(R.styleable.MessageViewGroup_author).orEmpty()

        typedArray.recycle()
    }

    fun setReactions(reactions: List<Reaction>) {
        flexBox.setReactions(reactions)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        avatarSize =
            maxOf(
                avatarView.measuredWidth + avatarView.marginStart + avatarView.marginEnd,
                avatarView.measuredHeight + avatarView.marginTop + avatarView.marginBottom
            )
        messageWidth =
            messageLayout.measuredWidth + messageLayout.marginStart + messageLayout.marginEnd
        messageHeight =
            messageLayout.measuredHeight + messageLayout.marginTop + messageLayout.marginBottom
        flexBoxWidth = flexBox.measuredWidth + flexBox.marginStart + flexBox.marginEnd
        flexBoxHeight = flexBox.measuredHeight + flexBox.marginTop + flexBox.marginBottom

        setMeasuredDimension(
            resolveSize(
                avatarSize + maxOf(messageWidth, flexBoxWidth) + paddingStart + paddingEnd,
                widthMeasureSpec
            ),
            resolveSize(
                maxOf(avatarSize, messageHeight + flexBoxHeight) + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatarView.layout(
            avatarView.marginStart + paddingStart,
            avatarView.marginTop + paddingTop,
            paddingStart + avatarView.marginStart + avatarView.measuredWidth,
            paddingTop + avatarView.marginTop + avatarView.measuredHeight
        )

        messageLayout.layout(
            paddingStart + avatarSize + messageLayout.marginStart,
            messageLayout.marginTop + paddingTop,
            paddingStart + avatarSize + maxOf(0, messageWidth - messageLayout.marginEnd),
            paddingTop + maxOf(0, messageHeight - messageLayout.marginBottom)
        )

        flexBox.layout(
            paddingStart + avatarSize + flexBox.marginStart,
            paddingTop + messageHeight + flexBox.marginTop,
            paddingStart + avatarSize + maxOf(0, flexBoxWidth - flexBox.marginEnd),
            paddingTop + messageHeight + maxOf(0, flexBoxHeight - flexBox.marginBottom)
        )
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
}
