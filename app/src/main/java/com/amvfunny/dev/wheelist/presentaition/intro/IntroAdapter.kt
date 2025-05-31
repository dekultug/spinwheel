package com.amvfunny.dev.wheelist.presentaition.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.hide
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.databinding.IntroItemBinding

class IntroAdapter : ListAdapter<INTRO_TYPE, IntroAdapter.IntroVH>(IntroDiffCallback()) {

    inner class IntroVH(private val binding: IntroItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: INTRO_TYPE) {
            when (data) {
                INTRO_TYPE.FIRST -> {
                    binding.tvIntroTitle.text =
                        getAppString(R.string.app_tile, binding.root.context)
                    binding.tvIntroContent.text =
                        binding.root.context.getString(R.string.content_intro_first)
                    binding.ivIntro.loadImage(getAppDrawable(R.drawable.bg_intro_first))
                }

                INTRO_TYPE.SECOND -> {
                    binding.tvIntroContent.text =
                        getAppString(R.string.content_intro_4, binding.root.context)
                    binding.tvIntroTitle.text =
                        getAppString(R.string.title_intro4, binding.root.context)
                    binding.ivIntro.loadImage(getAppDrawable(R.drawable.bg_intro_4))
                }

                INTRO_TYPE.THIRD -> {
                    binding.tvIntroContent.text =
                        getAppString(R.string.content_intro_second, binding.root.context)
                    binding.tvIntroTitle.text =
                        getAppString(R.string.title_intro_second, binding.root.context)
                    binding.ivIntro.loadImage(getAppDrawable(R.drawable.bg_intro_second))
                }

                INTRO_TYPE.FOUR -> {
                    binding.tvIntroContent.text =
                        getAppString(R.string.content_intro_third, binding.root.context)
                    binding.tvIntroTitle.text =
                        getAppString(R.string.title_intro_third, binding.root.context)
                    binding.ivIntro.loadImage(getAppDrawable(R.drawable.bg_intro_4_update))
                }
            }
        }
    }

    class IntroDiffCallback : DiffUtil.ItemCallback<INTRO_TYPE>() {
        override fun areItemsTheSame(oldItem: INTRO_TYPE, newItem: INTRO_TYPE): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: INTRO_TYPE, newItem: INTRO_TYPE): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroVH {
        return IntroVH(IntroItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: IntroVH, position: Int) {
        holder.onBind(getItem(position))
    }
}