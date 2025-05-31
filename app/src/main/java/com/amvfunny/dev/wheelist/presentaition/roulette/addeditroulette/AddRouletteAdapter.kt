package com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.databinding.FieldAddNewOptionItemBinding
import com.amvfunny.dev.wheelist.databinding.FieldOptionWheelItemBinding

class AddRouletteAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(AddRouletteDiffCallback()){

    companion object {
        private const val TYPE_OPTION = 0
        private const val TYPE_ADD = 1

        const val DATA_ADD_OPTION = "DATA_ADD_OPTION"
    }

    var listener: IAddRouletteListener? = null

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        return when (data) {
            is OptionWheel -> TYPE_OPTION
            is String -> TYPE_ADD
            else -> TYPE_OPTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_OPTION -> FieldOptionWheelVH(FieldOptionWheelItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> FieldOptionWheelAddVH(FieldAddNewOptionItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when{
            holder is FieldOptionWheelVH -> {
                holder.onBind(getItem(position) as OptionWheel)
            }
        }
    }

    inner class FieldOptionWheelVH(private val binding: FieldOptionWheelItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

            init {
                binding.cardView.setOnSafeClick {
                    val item = getItem(adapterPosition) as? OptionWheel
                    item?.let {
                        listener?.onUpdateOptionWheel(it)
                    }
                }

                binding.ivFieldOptionWheelDelete.setOnSafeClick {
                    val item = getItem(adapterPosition) as? OptionWheel
                    item?.let {
                        listener?.onDeleteOptionWheel(it.id)
                    }
                }
            }

        fun onBind(data: OptionWheel) {
            binding.tvFieldOptionWheelContent.text = data.content
            binding.flFieldOptionWheelBackground.setBackgroundColor(data.color ?: Color.TRANSPARENT)
        }
    }

    inner class FieldOptionWheelAddVH(private val binding: FieldAddNewOptionItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnSafeClick {
                listener?.onAddOption()
            }
        }
    }

    class AddRouletteDiffCallback: DiffUtil.ItemCallback<Any>(){
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when{
                oldItem is OptionWheel && newItem is OptionWheel -> {
                    oldItem.id == newItem.id
                }

                oldItem is String && newItem is String -> {
                    oldItem == newItem
                }

                else -> true
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when{
                oldItem is OptionWheel && newItem is OptionWheel -> {
                    oldItem.hashCode() == newItem.hashCode()
                }

                oldItem is String && newItem is String -> {
                    oldItem == newItem
                }

                else -> true
            }
        }
    }

    interface IAddRouletteListener {
        fun onUpdateOptionWheel(optionWheel: OptionWheel)
        fun onAddOption()
        fun onDeleteOptionWheel(id: Int)
    }
}