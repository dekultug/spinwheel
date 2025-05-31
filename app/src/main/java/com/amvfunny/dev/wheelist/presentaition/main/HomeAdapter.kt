package com.amvfunny.dev.wheelist.presentaition.main

import android.annotation.SuppressLint
import android.util.Log
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
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.setUpGradient
import com.amvfunny.dev.wheelist.databinding.FieldChooseHomograftItemBinding
import com.amvfunny.dev.wheelist.databinding.FieldRouletteRankingItemBinding
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences

const val UPDATE_TEXT_LANGUAGE_PAYLOAD = "UPDATE_TEXT_LANGUAGE_PAYLOAD"

class HomeAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(HomeDiffCallback()) {
    companion object {
        private const val TYPE_CHOOSER_HOMOGRAFT = 1
        private const val TYPE_ROULETTE_RANKING = 2
    }

    var listener: IHomeListener? = null

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            HOME_TYPE.CHOOSER, HOME_TYPE.HOMOGRAFT -> TYPE_CHOOSER_HOMOGRAFT
            HOME_TYPE.ROULETTE, HOME_TYPE.RANKING -> TYPE_ROULETTE_RANKING
            else -> TYPE_ROULETTE_RANKING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ROULETTE_RANKING -> RouletteRankingVH(
                FieldRouletteRankingItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            TYPE_CHOOSER_HOMOGRAFT -> HomograftChooserVH(
                FieldChooseHomograftItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> RouletteRankingVH(
                FieldRouletteRankingItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is RouletteRankingVH -> holder.onBind(getItem(position) as HOME_TYPE)
            holder is HomograftChooserVH -> holder.onBind(getItem(position) as HOME_TYPE)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when {
                holder is RouletteRankingVH -> holder.onBind(
                    getItem(position) as HOME_TYPE,
                    payloads
                )

                holder is HomograftChooserVH -> holder.onBind(
                    getItem(position) as HOME_TYPE,
                    payloads
                )
            }
        }
    }

    inner class RouletteRankingVH(private val binding: FieldRouletteRankingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvFieldRouletteRankingPlay.setOnSafeClick {
                val item = getItem(adapterPosition) as? HOME_TYPE
                item?.let {
                    listener?.onPlay(it)
                }
            }
        }

        fun onBind(data: HOME_TYPE) {
            Log.d("dsadasdádsa", "onBind: ânsjdasdasd")
            when {
                data == HOME_TYPE.ROULETTE -> {
                    binding.tvFieldRouletteRankingTitle.text =
                        getAppString(R.string.roulette_title, binding.root.context)
                    binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_roulette))
                    binding.tvFieldRouletteRankingContent.text =
                        getAppString(R.string.roulette_content, binding.root.context)
                }

                data == HOME_TYPE.RANKING -> {
                    binding.tvFieldRouletteRankingTitle.text =
                        getAppString(R.string.ranking_title, binding.root.context)
                    binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_ranking))
                    binding.tvFieldRouletteRankingContent.text =
                        getAppString(R.string.ranking_content, binding.root.context)
                }
            }
            binding.tvFieldRouletteRankingPlay.text =
                getAppString(R.string.play_title, binding.root.context)
            binding.tvFieldRouletteRankingPlay.background = setUpGradient(
                intArrayOf(
                    getAppColor(R.color.orange_gradient_first),
                    getAppColor(R.color.orange_gradient_second),
                ),
                getAppDimension(R.dimen.dimen_12),
                strokeColor = getAppColor(R.color.white),
                strokeWidth = getAppDimension(R.dimen.dimen_1).toInt()
            )
        }

        fun onBind(data: HOME_TYPE, payload: MutableList<Any>) {
            (payload.first() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_TEXT_LANGUAGE_PAYLOAD -> {
                        when {
                            data == HOME_TYPE.ROULETTE -> {
                                binding.tvFieldRouletteRankingTitle.text =
                                    getAppString(R.string.roulette_title, binding.root.context)
                                binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_roulette))
                                binding.tvFieldRouletteRankingContent.text =
                                    getAppString(R.string.roulette_content, binding.root.context)
                            }

                            data == HOME_TYPE.RANKING -> {
                                binding.tvFieldRouletteRankingTitle.text =
                                    getAppString(R.string.ranking_title, binding.root.context)
                                binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_ranking))
                                binding.tvFieldRouletteRankingContent.text =
                                    getAppString(R.string.ranking_content, binding.root.context)
                            }
                        }
                        binding.tvFieldRouletteRankingPlay.text =
                            getAppString(R.string.play_title, binding.root.context)
                        binding.tvFieldRouletteRankingPlay.background = setUpGradient(
                            intArrayOf(
                                getAppColor(R.color.orange_gradient_first),
                                getAppColor(R.color.orange_gradient_second),
                            ),
                            getAppDimension(R.dimen.dimen_12),
                            strokeColor = getAppColor(R.color.white),
                            strokeWidth = getAppDimension(R.dimen.dimen_1).toInt()
                        )
                    }
                }
            }
        }
    }

    inner class HomograftChooserVH(private val binding: FieldChooseHomograftItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvFieldChooseHomograftPlay.setOnSafeClick {
                val item = getItem(adapterPosition) as? HOME_TYPE
                item?.let {
                    listener?.onPlay(it)
                }
            }
        }

        fun onBind(data: HOME_TYPE) {

            when {
                data == HOME_TYPE.HOMOGRAFT -> {
                    binding.tvFiledChooseHomograftTitle.text =
                        getAppString(R.string.homograft_title, binding.root.context)
                    binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_split_team))
                    binding.tvFieldChooseHomograftContent.text =
                        getAppString(R.string.homograft_content, binding.root.context)
                }

                data == HOME_TYPE.CHOOSER -> {
                    binding.tvFiledChooseHomograftTitle.text =
                        getAppString(R.string.chooser_title, binding.root.context)
                    binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_winner))
                    binding.tvFieldChooseHomograftContent.text =
                        getAppString(R.string.chooser_content, binding.root.context)
                }
            }

            binding.tvFieldChooseHomograftPlay.text =
                getAppString(R.string.play_title, binding.root.context)

            binding.tvFieldChooseHomograftPlay.background = setUpGradient(
                intArrayOf(
                    getAppColor(R.color.orange_gradient_first),
                    getAppColor(R.color.orange_gradient_second),
                ),
                getAppDimension(R.dimen.dimen_12),
                strokeColor = getAppColor(R.color.white),
                strokeWidth = getAppDimension(R.dimen.dimen_1).toInt()
            )
        }

        fun onBind(data: HOME_TYPE, payload: MutableList<Any>) {
            (payload.first() as? List<*>)?.forEach {
                when (it) {
                    UPDATE_TEXT_LANGUAGE_PAYLOAD -> {
                        when {
                            data == HOME_TYPE.HOMOGRAFT -> {
                                binding.tvFiledChooseHomograftTitle.text =
                                    getAppString(R.string.homograft_title, binding.root.context)
                                binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_split_team))
                                binding.tvFieldChooseHomograftContent.text =
                                    getAppString(R.string.homograft_content, binding.root.context)
                            }

                            data == HOME_TYPE.CHOOSER -> {
                                binding.tvFiledChooseHomograftTitle.text =
                                    getAppString(R.string.chooser_title, binding.root.context)
                                binding.ivFieldRouletteRankingBackground.loadImage(getAppDrawable(R.drawable.bg_winner))
                                binding.tvFieldChooseHomograftContent.text =
                                    getAppString(R.string.chooser_content, binding.root.context)
                            }
                        }

                        binding.tvFieldChooseHomograftPlay.text =
                            getAppString(R.string.play_title, binding.root.context)

                        binding.tvFieldChooseHomograftPlay.background = setUpGradient(
                            intArrayOf(
                                getAppColor(R.color.orange_gradient_first),
                                getAppColor(R.color.orange_gradient_second),
                            ),
                            getAppDimension(R.dimen.dimen_12),
                            strokeColor = getAppColor(R.color.white),
                            strokeWidth = getAppDimension(R.dimen.dimen_1).toInt()
                        )
                    }
                }
            }
        }
    }

    class HomeDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return true
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return false
        }

        override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
            val list: MutableList<Any> = arrayListOf()
            if (!areContentsTheSame(oldItem, newItem)) {
                list.add(UPDATE_TEXT_LANGUAGE_PAYLOAD)
                SpinWheelPreferences.updateText = false
            }
            return list.ifEmpty { null }
        }
    }

    interface IHomeListener {
        fun onPlay(type: HOME_TYPE)
    }
}