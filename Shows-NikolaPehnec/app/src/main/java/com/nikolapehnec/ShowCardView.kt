package com.nikolapehnec

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.nikolapehnec.databinding.ViewShowItemBinding

class ShowCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: ViewShowItemBinding

    init {
        binding = ViewShowItemBinding.inflate(LayoutInflater.from(context), this)

        val pixelPadding = context.resources.getDimensionPixelSize(R.dimen.card_padding)
        setPadding(pixelPadding, 0, pixelPadding, 0)
    }

    fun setTitle(title: String) {
        binding.showName.text = title
    }

    fun setDescription(description: String?) {
        binding.showDescription.text = description
    }

    fun setImage(imgUrl: String) {
            Glide.with(this).load(imgUrl).into(binding.showImage)
    }

    fun setClickListener(
        onClickCallback: (String, String, String, String) -> Unit,
        id: String,
        title: String,
        description: String,
        imgUrl: String
    ) {
        binding.root.setOnClickListener {
            onClickCallback(id, title, description, imgUrl)
        }
    }
}