package com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.databinding.FieldColorItemBinding

class ColorAdapter : ListAdapter<ColorDisplay, ColorAdapter.ColorVH>(ColorDiffCallback()) {

    var listener: IColorListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorVH {
        return ColorVH(
            FieldColorItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ColorVH, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ColorVH(private val binding: FieldColorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivFieldColor.setOnSafeClick {
                listener?.onSelect(getItem(adapterPosition).color)
            }
        }

        fun onBind(data: ColorDisplay) {
            Log.d("tunglvv", "onBind: ${data.color}")
            binding.ivFieldColor.setBackgroundColor(data.color ?: Color.TRANSPARENT)
            stateSelect(data)
        }

        private fun stateSelect(data: ColorDisplay) {
            binding.ivFieldColorSelect.isVisible = data.isSelect
        }
    }

    class ColorDiffCallback : DiffUtil.ItemCallback<ColorDisplay>() {
        override fun areItemsTheSame(oldItem: ColorDisplay, newItem: ColorDisplay): Boolean {
            return oldItem.color == newItem.color
        }

        override fun areContentsTheSame(oldItem: ColorDisplay, newItem: ColorDisplay): Boolean {
            return oldItem.isSelect == newItem.isSelect
        }
    }

    interface IColorListener {
        fun onSelect(color: Int?)
    }
}