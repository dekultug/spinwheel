package com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.DialogScreen
import com.amvfunny.dev.wheelist.base.common.eventbus.CustomColorEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateOptionWheel
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.hideKeyBoard
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelDialog
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelFragment
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.databinding.AddEditOptionWheelDlgBinding
import com.amvfunny.dev.wheelist.presentaition.roulette.colorcustom.ColorCustomFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditRouletteDlg :
    SpinWheelDialog<AddEditOptionWheelDlgBinding>(R.layout.add_edit_option_wheel_dlg) {

    var listener: IAddEditDlgCallback? = null

    companion object {
        const val OPTION_WHEEL_DATA_KEY = "OPTION_WHEEL_DATA_KEY"
        const val SIZE_OPTION_TEMP_KEY = "SIZE_OPTION_TEMP_KEY"
        const val WHEEL_ID_KEY = "WHEEL_ID_KEY"
        fun getInstance(
            data: OptionWheel? = null,
            countSize: Int? = null,
            wheelId: Int? = null
        ): AddEditRouletteDlg {
            val fra = AddEditRouletteDlg()
            fra.arguments = bundleOf(
                OPTION_WHEEL_DATA_KEY to data,
                SIZE_OPTION_TEMP_KEY to countSize,
                WHEEL_ID_KEY to wheelId
            )
            return fra
        }
    }

    private val viewModel by viewModels<AddEditViewModel>()

    private val adapter by lazy { ColorAdapter() }

    override fun getBackgroundId() = R.id.flAddEditOptionWheel

    override fun screen(): DialogScreen {
        return DialogScreen(
            isDismissByTouchOutSide = false,
            isDismissByOnBackPressed = false
        )
    }

    override fun onStart() {
        super.onStart()
        EventBusManager.instance?.register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusManager.instance?.unregister(this)
    }

    override fun onEvent(event: IEvent) {
        super.onEvent(event)
        when (event) {
            is CustomColorEvent -> {
                if (event.color != null) {
                    binding.ivAddEditOptionWheelDlgColorCustom.show()
                    binding.ivAddEditOptionWheelDlgColorCustom.setBackgroundColor(event.color)
                    viewModel.colorCustom = event.color
                    binding.ivAddEditOptionWheelDlgColorCustomSelect.show()
                    viewModel.setSelectColor(event.color)
                }
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }


    override fun onInitView() {
        super.onInitView()
        binding.clAddEditOptionWheelDlg.setOnSafeClick {
            it.hideKeyBoard()
            binding.edtAddEditOptionWheelDlgNameTitle.clearFocus()
        }
        binding.flAddEditOptionWheel.setOnSafeClick {
            it.hideKeyBoard()
            binding.edtAddEditOptionWheelDlgNameTitle.clearFocus()
        }

        binding.flAddEditOptionWheelDlgAdd.setGradientButton(getAppDimension(R.dimen.dimen_12))

        binding.cvAddEditOptionWheelDlgCancel.setOnSafeClick {
            listener?.onDismiss()
            dismiss()
        }

        binding.cvAddEditOptionWheelDlgAdd.setOnSafeClick {
            if (checkHasTitle()) {
                viewModel.addEditOptionWheel(binding.edtAddEditOptionWheelDlgNameTitle.text.toString())
            } else {
                Toast.makeText(
                    requireContext(),
                    getAppString(R.string.ask_title_must_be_not_null, requireContext()),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (viewModel.optionWheel != null) {
            binding.edtAddEditOptionWheelDlgNameTitle.setText(viewModel.optionWheel!!.content)

            binding.edtAddEditOptionWheelDlgNameTitle.setText(viewModel.optionWheel?.content)
            binding.tvAddEditOptionWheelDlgTitle.text =
                getAppString(R.string.edit_title, requireContext())

            viewModel.checkColorInList(viewModel.optionWheel!!.color)
        } else {
            binding.tvAddEditOptionWheelDlgTitle.text =
                getAppString(R.string.new_option_title, requireContext())
            binding.ivAddEditOptionWheelDlgColorCustomSelect.gone()
            binding.ivAddEditOptionWheelDlgColorCustom.gone()
            binding.ivAddEditOptionWheelDlgColorCustom.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.llAddEditOptionWheelDlgColorCustom.setOnSafeClick {
            val colorCustomFragment = ColorCustomFragment.getInstance(viewModel.optionWheel?.color)
            colorCustomFragment.show(parentFragmentManager, colorCustomFragment.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.listener = null
    }

    override fun setUpAdapter() {
        super.setUpAdapter()
        binding.rvAddEditOptionWheelDlgColor.adapter = adapter
        binding.rvAddEditOptionWheelDlgColor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        adapter.listener = object : ColorAdapter.IColorListener {
            override fun onSelect(color: Int?) {
                binding.ivAddEditOptionWheelDlgColorCustomSelect.gone()
                binding.ivAddEditOptionWheelDlgColorCustom.gone()
                viewModel.colorCustom = null
                viewModel.setSelectColor(color)
            }
        }
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.listColor) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    adapter.submitList(it.data)
                }
            })
        }

        coroutinesLaunch(viewModel.addOrEditState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    EventBusManager.instance?.postPending(UpdateOptionWheel(it.data!!))
                    viewModel.resetStateAddEdit()
                    dismiss()
                    listener?.onDismiss()
                }
            })
        }

        coroutinesLaunch(viewModel.colorStateOriginal) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    if (it.data == true) {
                        binding.ivAddEditOptionWheelDlgColorCustomSelect.gone()
                        binding.ivAddEditOptionWheelDlgColorCustom.gone()
                        binding.ivAddEditOptionWheelDlgColorCustom.setBackgroundColor(Color.TRANSPARENT)
                    } else {
                        binding.ivAddEditOptionWheelDlgColorCustomSelect.show()
                        binding.ivAddEditOptionWheelDlgColorCustom.show()
                        binding.ivAddEditOptionWheelDlgColorCustom.setBackgroundColor(
                            viewModel.optionWheel?.color ?: Color.TRANSPARENT
                        )
                    }
                    viewModel.resetColorInList()
                }
            })
        }
    }

    private fun checkHasTitle() = binding.edtAddEditOptionWheelDlgNameTitle.text.trim().isNotEmpty()

    interface IAddEditDlgCallback {
        fun onDismiss()
    }
}