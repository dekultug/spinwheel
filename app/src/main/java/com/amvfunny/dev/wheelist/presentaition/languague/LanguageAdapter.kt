package com.amvfunny.dev.wheelist.presentaition.languague

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.databinding.LanguageItemBinding

private const val UPDATE_STATE_SELECT_LANGUAGE_PAYLOAD = "UPDATE_STATE_SELECT_LANGUAGE_PAYLOAD"

class LanguageAdapter :
    ListAdapter<LanguageDisplay, LanguageAdapter.LanguageVH>(LanguageDiffCallback()) {

    var listener: ILanguageListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageVH {
        return LanguageVH(
            LanguageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LanguageVH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onBindViewHolder(holder: LanguageVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(getItem(position), payloads)
        }
    }

    inner class LanguageVH(private val binding: LanguageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnSafeClick {
                listener?.onSelectType(getItem(adapterPosition).type)
            }
        }

        fun onBind(data: LanguageDisplay) {
            setUpView(data.type)
            setState(data)
        }

        fun onBind(data: LanguageDisplay, payloads: List<Any>) {
            (payloads.first() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_STATE_SELECT_LANGUAGE_PAYLOAD -> setState(data)
                }
            }
        }

        private fun setState(data: LanguageDisplay) {
            if (data.isSelect) {
                binding.rbLanguage.loadImage(getAppDrawable(R.drawable.ic_radio_active))
                binding.clLanguageRoot.background = setUpGradient(
                    intArrayOf(
                        getAppColor(R.color.orange_background),
                        getAppColor(R.color.orange_gradient_second),
                    ),
                    getAppDimension(R.dimen.dimen_12)
                )
            } else {
                binding.rbLanguage.loadImage(getAppDrawable(R.drawable.ic_radio_inactive))
                binding.clLanguageRoot.background =
                    getAppDrawable(R.drawable.shape_bg_black_light_corner_12)
            }
        }

        private fun setUpView(type: LANGUAGE_TYPE) {
            when (type) {
                LANGUAGE_TYPE.ENGLISH -> {
                    binding.tvLanguage.text = getAppString(R.string.en_title, binding.root.context)
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_en))
                }

                LANGUAGE_TYPE.SPANISH -> {
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_spanish))
                    binding.tvLanguage.text =
                        getAppString(R.string.spanish_title, binding.root.context)
                }

                LANGUAGE_TYPE.HINDI -> {
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_hindi))
                    binding.tvLanguage.text =
                        getAppString(R.string.hindi_title, binding.root.context)
                }

                LANGUAGE_TYPE.FRENCH -> {
                    binding.tvLanguage.text =
                        getAppString(R.string.french_title, binding.root.context)
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_french))
                }

                LANGUAGE_TYPE.PORTUGUESE -> {
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_por))
                    binding.tvLanguage.text =
                        getAppString(R.string.portuguese_title, binding.root.context)
                }

                LANGUAGE_TYPE.VIETNAMESE ->{
                    binding.ivLanguage.loadImage(getAppDrawable(R.drawable.ic_language_por))
                    binding.tvLanguage.text =
                        getAppString(R.string.vietnameese_title, binding.root.context)
                }
            }
        }
    }

    class LanguageDiffCallback : DiffUtil.ItemCallback<LanguageDisplay>() {
        override fun areItemsTheSame(oldItem: LanguageDisplay, newItem: LanguageDisplay): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: LanguageDisplay,
            newItem: LanguageDisplay
        ): Boolean {
            return oldItem.isSelect == newItem.isSelect
        }

        override fun getChangePayload(oldItem: LanguageDisplay, newItem: LanguageDisplay): Any? {
            val list: MutableList<Any> = arrayListOf()
            if (oldItem.isSelect != newItem.isSelect) {
                list.add(UPDATE_STATE_SELECT_LANGUAGE_PAYLOAD)
            }
            return list.ifEmpty { null }
        }
    }

    interface ILanguageListener {
        fun onSelectType(type: LANGUAGE_TYPE)
    }
}