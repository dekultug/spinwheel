package com.amvfunny.dev.wheelist.presentaition.main.spinplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppColor
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.databinding.FieldSpinPlayItemBinding

class SpinAdapter : ListAdapter<SpinPlayDisplay, SpinAdapter.SpinVH>(SpinDiffCallback()) {

    companion object {
        const val UPDATE_STATE_PAYLOAD = "UPDATE_STATE_PAYLOAD"
    }

    var listener: ISpinListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinVH {
        return SpinVH(
            FieldSpinPlayItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SpinVH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onBindViewHolder(holder: SpinVH, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.onBind(data = getItem(position), payloads)
        }
    }

    inner class SpinVH(private val binding: FieldSpinPlayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnSafeClick {
                val item = getItem(adapterPosition)
                listener?.onSelectSize(item.size)
            }
        }

        fun onBind(data: SpinPlayDisplay) {
            binding.tvFieldSpinPlay.text = data.size.toString()
            if (bindingAdapterPosition == currentList.lastIndex) {
                binding.vFieldSpinPlayDivider.gone()
            } else {
                binding.vFieldSpinPlayDivider.show()
            }
            stateSelect(data)
        }

        fun onBind(data: SpinPlayDisplay, payload: List<Any>) {
            payload.forEach {
                when (it) {
                    UPDATE_STATE_PAYLOAD -> {
                        stateSelect(data)
                    }
                }
            }
        }

        private fun stateSelect(data: SpinPlayDisplay) {
            if (data.isSelect) {
                binding.tvFieldSpinPlay.setTextColor(getAppColor(R.color.orange_primary_bold))
            } else {
                binding.tvFieldSpinPlay.setTextColor(getAppColor(R.color.n8))
            }
        }
    }

    class SpinDiffCallback : DiffUtil.ItemCallback<SpinPlayDisplay>() {
        override fun areItemsTheSame(oldItem: SpinPlayDisplay, newItem: SpinPlayDisplay): Boolean {
            return oldItem.size == newItem.size
        }

        override fun areContentsTheSame(
            oldItem: SpinPlayDisplay,
            newItem: SpinPlayDisplay
        ): Boolean {
            return oldItem.isSelect == newItem.isSelect
        }

        override fun getChangePayload(oldItem: SpinPlayDisplay, newItem: SpinPlayDisplay): Any? {
            return if (oldItem.isSelect != newItem.isSelect) {
                UPDATE_STATE_PAYLOAD
            } else {
                null
            }
        }
    }

    interface ISpinListener {
        fun onSelectSize(size: Int?)
    }
}