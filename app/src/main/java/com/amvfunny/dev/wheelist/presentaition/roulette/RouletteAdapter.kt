package com.amvfunny.dev.wheelist.presentaition.roulette

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppDrawable
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.loadImage
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.data.model.Wheel
import com.amvfunny.dev.wheelist.databinding.FieldRouletteAddItemBinding
import com.amvfunny.dev.wheelist.databinding.FieldRouletteItemBinding
import com.amvfunny.dev.wheelist.presentaition.getApplication

class RouletteAdapter : ListAdapter<Any, RecyclerView.ViewHolder>(RouletteDiffCallback()) {

    var listener: IRouletteListener? = null

    companion object {
        private const val TYPE_WHEEL = 0
        private const val TYPE_ADD = 1
        private const val NATIVE_ADS_TYPE = 2

        const val ROULETTE_ADD_DATA = "ROULETTE_ADD_DATA"
        private const val UPDATE_SHOW_ADS_PAYLOAD = "UPDATE_SHOW_ADS_PAYLOAD"
    }

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        return when (data) {
            is Wheel -> TYPE_WHEEL
            is String -> TYPE_ADD
            else -> TYPE_WHEEL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WHEEL -> RouletteVH(
                FieldRouletteItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> RouletteAddVH(
                FieldRouletteAddItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is RouletteVH -> {
                holder.onBind(getItem(position) as Wheel)
            }
        }
    }

    inner class RouletteVH(private val binding: FieldRouletteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var popupWindow: PopupWindow? = null
        private var tvEdit: TextView? = null
        private var tvDuplicate: TextView? = null
        private var tvShare: TextView? = null
        private var tvRemove: TextView? = null

        init {

            popWindow()

            binding.tvFieldRouletteSpin.setOnSafeClick {
                val item = getItem(bindingAdapterPosition) as? Wheel ?: return@setOnSafeClick
                if (item.getTypeWheel() == WHEEL_TYPE.EXAMPLE) {
                    Toast.makeText(
                        binding.root.context,
                        getAppString(R.string.file_sample_title, binding.root.context),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    listener?.onClickRoulette(item)
                }
            }

            binding.ivFieldRouletteMore.setOnSafeClick {
                val item = getItem(bindingAdapterPosition) as? Wheel ?: return@setOnSafeClick
                if (item.getTypeWheel() == WHEEL_TYPE.EXAMPLE) {
                    Toast.makeText(
                        binding.root.context,
                        getAppString(R.string.file_sample_title, binding.root.context),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.ivFieldRouletteMore.loadImage(getAppDrawable(R.drawable.ic_spin_more_active))
                    popupWindow?.showAsDropDown(binding.ivFieldRouletteMore)
                }
            }
        }

        fun onBind(data: Wheel) {
            setUpTitle(data)
            setBackGround(data)
            binding.tvFieldRouletteSpin.setGradientButton(getAppDimension(R.dimen.dimen_12))
        }

        private fun setUpTitle(data: Wheel) {
            when (data.getTypeWheel()) {
                WHEEL_TYPE.EAT, WHEEL_TYPE.EATOPTION -> {
                    binding.tvFieldRouletteTitle.text =
                        getAppString(R.string.wheel_init_1, binding.root.context)
                }

                WHEEL_TYPE.WINNER -> {
                    binding.tvFieldRouletteTitle.text =
                        getAppString(R.string.wheel_init_2, binding.root.context)
                }

                WHEEL_TYPE.YESNO -> {
                    binding.tvFieldRouletteTitle.text =
                        getAppString(R.string.wheel_init_3, binding.root.context)
                }

                WHEEL_TYPE.CUSTOM -> {
                    binding.tvFieldRouletteTitle.text = data.title
                }

                WHEEL_TYPE.EXAMPLE -> {
                    binding.tvFieldRouletteTitle.text =
                        getAppString(R.string.file_sample_title, binding.root.context)
                }
            }
        }

        private fun setBackGround(data: Wheel) {
            when (data.getTypeWheel()) {
                WHEEL_TYPE.EAT -> {
                    binding.ivFieldRouletteBackground.loadImage(getAppDrawable(R.drawable.bg_roulette_custom))
                }

                WHEEL_TYPE.WINNER -> {
                    binding.ivFieldRouletteBackground.loadImage(getAppDrawable(R.drawable.bg_roulette_custom))
                }

                WHEEL_TYPE.YESNO -> {
                    binding.ivFieldRouletteBackground.loadImage(getAppDrawable(R.drawable.bg_roulette_custom))
                }

                WHEEL_TYPE.EATOPTION -> {
                    binding.ivFieldRouletteBackground.loadImage(getAppDrawable(R.drawable.bg_roulette_custom))
                }

                else -> {
                    binding.ivFieldRouletteBackground.loadImage(getAppDrawable(R.drawable.bg_roulette_custom))
                }
            }
        }

        @SuppressLint("MissingInflatedId")
        private fun popWindow() {
            val inflater =
                getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.field_poup_crud_roulette, null)
            popupView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            tvEdit = popupView.findViewById(R.id.tvFieldPopupCrudEdit)
            tvDuplicate = popupView.findViewById(R.id.tvFieldPopupCrudDuplicate)
            tvShare = popupView.findViewById(R.id.tvFieldPopupCrudShare)
            tvRemove = popupView.findViewById(R.id.tvFieldPopupCrudRemove)

            tvEdit?.text = getAppString(R.string.edit_title, binding.root.context)
            tvDuplicate?.text = getAppString(R.string.duplicate_title, binding.root.context)
            tvShare?.text = getAppString(R.string.share_title, binding.root.context)
            tvRemove?.text = getAppString(R.string.remove_title, binding.root.context)


            tvEdit?.setOnSafeClick {
                val item = getItem(adapterPosition) as? Wheel
                if (item != null) {
                    listener?.onEdit(wheel = item)
                }
                popupWindow?.dismiss()
            }

            tvDuplicate?.setOnSafeClick {
                val item = getItem(adapterPosition) as? Wheel
                if (item != null) {
                    listener?.onDuplicate(wheel = item)
                }
                popupWindow?.dismiss()
            }

            tvShare?.setOnSafeClick {
                val item = getItem(adapterPosition) as? Wheel
                if (item != null) {
                    listener?.onShare(wheel = item)
                }
                popupWindow?.dismiss()
            }

            tvRemove?.setOnSafeClick {
                val item = getItem(adapterPosition) as? Wheel
                if (item != null) {
                    listener?.onShowPopup()
                    listener?.onRemove(item.id, item.title)
                }
                popupWindow?.dismiss()
            }

            popupWindow?.setOnDismissListener {
                listener?.onHidePopup()
                binding.ivFieldRouletteMore.loadImage(getAppDrawable(R.drawable.ic_more_inactive))
            }
        }
    }

    inner class RouletteAddVH(private val binding: FieldRouletteAddItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvFieldRouletteAdd.setOnSafeClick {
                listener?.onAdd()
            }
            binding.tvFieldRouletteAdd.setGradientButton(getAppDimension(R.dimen.dimen_12))
        }
    }

    class RouletteDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Wheel && newItem is Wheel -> {
                    oldItem.id == newItem.id
                }

                oldItem is String && newItem is String -> {
                    oldItem == newItem
                }

                oldItem is NativeDisplay && newItem is NativeDisplay -> {
                    true
                }

                else -> true
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Wheel && newItem is Wheel -> {
                    oldItem.title == newItem.title && oldItem.countRepeat == newItem.countRepeat && oldItem.wheelType == newItem.wheelType
                }

                oldItem is String && newItem is String -> {
                    oldItem == newItem
                }

                oldItem is NativeDisplay && newItem is NativeDisplay -> {
                    oldItem.isShow == newItem.isShow
                }

                else -> false
            }
        }

        override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
            val list: MutableList<Any> = arrayListOf()
            when {
                oldItem is NativeDisplay && newItem is NativeDisplay -> {
                    if (oldItem.isShow != newItem.isShow) {
                        list.add(UPDATE_SHOW_ADS_PAYLOAD)
                    }
                }
            }
            return list.ifEmpty { null }
        }
    }

    interface IRouletteListener {
        fun onAdd()
        fun onEdit(wheel: Wheel?)
        fun onDuplicate(wheel: Wheel?)
        fun onShare(wheel: Wheel?)
        fun onRemove(id: Int?, title: String?)
        fun onClickRoulette(wheel: Wheel)
        fun onShowPopup() {}
        fun onHidePopup() {}
    }
}