package com.amvfunny.dev.wheelist.presentaition.roulette.addeditroulette

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.eventbus.EventBusManager
import com.amvfunny.dev.wheelist.base.common.eventbus.IEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.ReLoadNativeAllRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllAddEdit
import com.amvfunny.dev.wheelist.base.common.eventbus.ReloadNativeAllSpinRoulette
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateOptionWheel
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateWheelEvent
import com.amvfunny.dev.wheelist.base.common.eventbus.UpdateWheelRouletteEvent
import com.amvfunny.dev.wheelist.base.common.extention.IStateData
import com.amvfunny.dev.wheelist.base.common.extention.coroutinesLaunch
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.gone
import com.amvfunny.dev.wheelist.base.common.extention.handleStateData
import com.amvfunny.dev.wheelist.base.common.extention.hideKeyBoard
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.extention.setGradientMain
import com.amvfunny.dev.wheelist.base.common.extention.setGradientPurple
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.extention.show
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelActivity
import com.amvfunny.dev.wheelist.data.model.OptionWheel
import com.amvfunny.dev.wheelist.data.model.WHEEL_TYPE
import com.amvfunny.dev.wheelist.databinding.AddNewRouletteActivityBinding
import com.amvfunny.dev.wheelist.presentaition.roulette.addeditdlg.AddEditRouletteDlg
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.DATA_SPIN_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.REPEAT_COUNT_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.TITLE_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.TYPE_WHEEL_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.preview.PreviewRouletteActivity.Companion.WHEEL_PREVIEW_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.REPEAT_KEY
import com.amvfunny.dev.wheelist.presentaition.roulette.spinwheel.SpinRouletteActivity.Companion.WHEEL_DATA_KEY
import com.amvfunny.dev.wheelist.presentaition.widget.slider.SPIN_SLIDER_TYPE
import com.amvfunny.dev.wheelist.presentaition.widget.slider.SpinSliderView
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


@AndroidEntryPoint
class AddEditRouletteActivity : SpinWheelActivity<AddNewRouletteActivityBinding>() {

    companion object {
        const val WHEEL_KEY = "WHEEL_KEY"
        const val IS_EDIT_FROM_SPIN_KEY = "IS_EDIT_FROM_SPIN_KEY"
    }

    private val viewModel by viewModels<AddEditRouletteViewModel>()

    private val adapter by lazy { AddRouletteAdapter() }

    override fun setBinding(layoutInflater: LayoutInflater): AddNewRouletteActivityBinding {
        return AddNewRouletteActivityBinding.inflate(layoutInflater)
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
            is UpdateOptionWheel -> {
                viewModel.addOrUpdateOptionWheel(event.optionWheel)
                EventBusManager.instance?.removeSticky(event)
            }

            is ReloadNativeAllAddEdit -> {
                loadNative()
                EventBusManager.instance?.removeSticky(event)
            }
        }
    }

    override fun setUpView() {
        super.setUpView()

        binding.clAddEditNewRouletteHeader.setOnSafeClick {
            it.hideKeyBoard()
            binding.edtAddEditNewRouletteName.clearFocus()
        }
        binding.clAddEditNewRouletteBody.setOnSafeClick {
            it.hideKeyBoard()
            binding.edtAddEditNewRouletteName.clearFocus()
        }

        binding.clAddEditNewRouletteHeader.setGradientMain()
        binding.tvAddEditNewRouletteSaveSpin.setGradientButton(getAppDimension(R.dimen.dimen_12))
        binding.tvAddEditNewRoulettePreview.setGradientPurple(getAppDimension(R.dimen.dimen_12))

        binding.slAddEditNewRouletteRepeat.setStep(1, 10)
        binding.slAddEditNewRouletteRepeat.setType(SPIN_SLIDER_TYPE.DURATION)

        if (viewModel.wheel != null) {
            binding.tvAddEditNewRouletteTitle.text =
                getAppString(R.string.edit_title, this)
            when (viewModel.wheel!!.getTypeWheel()) {
                WHEEL_TYPE.EAT, WHEEL_TYPE.EATOPTION -> binding.edtAddEditNewRouletteName.setText(
                    getAppString(
                        R.string.wheel_init_1,
                        this
                    )
                )

                WHEEL_TYPE.WINNER -> binding.edtAddEditNewRouletteName.setText(
                    getAppString(
                        R.string.wheel_init_2,
                        this
                    )
                )

                WHEEL_TYPE.YESNO -> binding.edtAddEditNewRouletteName.setText(
                    getAppString(
                        R.string.wheel_init_3,
                        this
                    )
                )

                else -> binding.edtAddEditNewRouletteName.setText(viewModel.wheel?.title)
            }
        } else {
            binding.tvAddEditNewRouletteTitle.text =
                getAppString(R.string.add_new_roulette_title, this)
        }

        binding.ivAddEditNewRouletteClose.setOnSafeClick {
            finish()
        }

        binding.slAddEditNewRouletteRepeat.setValue(viewModel.wheel?.countRepeat?.toInt() ?: 1)

        binding.tvAddEditNewRouletteSaveSpin.setOnSafeClick {
            if (checkHasSpin()) {
                loadInter()
            }
        }

        binding.tvAddEditNewRoulettePreview.setOnSafeClick {
            if (checkHasSpin()) {
                navigateTo(
                    PreviewRouletteActivity::class.java, bundleOf(
                        DATA_SPIN_KEY to viewModel.listOptionWheelTemp,
                        REPEAT_COUNT_KEY to binding.slAddEditNewRouletteRepeat.getValueStep(),
                        TITLE_KEY to binding.edtAddEditNewRouletteName.text.toString(),
                        TYPE_WHEEL_KEY to -1,
                        WHEEL_PREVIEW_KEY to viewModel.wheel
                    )
                )
            }
        }

        binding.slAddEditNewRouletteRepeat.listener = object : SpinSliderView.ISpinSliderListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onUp() {
                super.onUp()
                binding.svAddNewRoulette.setScrollingEnabled(true)
            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onMove() {
                super.onMove()
                binding.svAddNewRoulette.setScrollingEnabled(false)
            }
        }

        setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    binding.rlAddEditNewRouletteAdmob.gone()
                } else {
                    binding.rlAddEditNewRouletteAdmob.show()
                }
            }
        })

        loadNative()
    }

    private fun checkHasSpin(): Boolean {
        if (binding.edtAddEditNewRouletteName.text.trim().isEmpty()) {
            Toast.makeText(
                this,
                getAppString(R.string.ask_title_must_be_not_null, this),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (viewModel.listOptionWheelTemp.size < 2) {
            Toast.makeText(
                this,
                getAppString(R.string.ask_more_option, this),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        EventBusManager.instance?.postPending(ReLoadNativeAllRoulette())
        if (viewModel.editSpinFromSpinRoulette) {
            EventBusManager.instance?.postPending(ReloadNativeAllSpinRoulette())
        }
        super.onDestroy()
    }

    override fun removeListener() {
        adapter.listener = null
    }

    override fun observerData() {
        super.observerData()
        coroutinesLaunch(viewModel.optionWheelListState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    adapter.submitList(it.data)
                    scrollIndex()
                }
            })
        }

        coroutinesLaunch(viewModel.saveAndSpinState) {
            handleStateData(it, object : IStateData {
                override fun onSuccess() {
                    EventBusManager.instance?.postPending(UpdateWheelRouletteEvent(viewModel.wheel?.copy()))
                    if (viewModel.wheel != null) {
                        if (viewModel.editSpinFromSpinRoulette) {
                            EventBusManager.instance?.postPending(UpdateWheelEvent(viewModel.wheel?.copy()))
                            finish()
                        } else {
                            navigateTo(
                                SpinRouletteActivity::class.java, bundleOf(
                                    WHEEL_DATA_KEY to viewModel.wheel!!,
                                    REPEAT_KEY to viewModel.wheel!!.countRepeat,
                                    SpinRouletteActivity.TITLE_KEY to binding.edtAddEditNewRouletteName.text.toString()
                                )
                            )
                        }
                    }
                    viewModel.resetStateSaveOrEdit()
                }
            })
        }
    }

    override fun setUpAdapter() {
        super.setUpAdapter()
        binding.rvAddEditNewRouletteOption.adapter = adapter
        binding.rvAddEditNewRouletteOption.layoutManager = LinearLayoutManager(this)

        adapter.listener = object : AddRouletteAdapter.IAddRouletteListener {
            override fun onUpdateOptionWheel(optionWheel: OptionWheel) {
                val addEditDlg = AddEditRouletteDlg.getInstance(data = optionWheel, countSize = null, wheelId = null)
                addEditDlg.show(supportFragmentManager, addEditDlg.tag)
                binding.rlAddEditNewRouletteAdmob.gone()
                addEditDlg.listener = object : AddEditRouletteDlg.IAddEditDlgCallback {
                    override fun onDismiss() {
                        binding.rlAddEditNewRouletteAdmob.show()
                    }
                }
            }

            override fun onAddOption() {
                val add = AddEditRouletteDlg.getInstance(
                    data = null,
                    countSize = viewModel.getCountSize(),
                    wheelId = viewModel.wheel?.id
                )
                add.show(supportFragmentManager, add.tag)

                binding.rlAddEditNewRouletteAdmob.gone()
                add.listener = object : AddEditRouletteDlg.IAddEditDlgCallback {
                    override fun onDismiss() {
                        binding.rlAddEditNewRouletteAdmob.show()
                    }
                }
            }

            override fun onDeleteOptionWheel(id: Int) {
                viewModel.deleteOptionWheel(id)
            }
        }
    }

    private fun scrollIndex() {
        Handler(Looper.getMainLooper()).post {
            if (viewModel.indexScroll != -1) {
                binding.rvAddEditNewRouletteOption.scrollToPosition(viewModel.indexScroll)
                viewModel.indexScroll = -1
            }
        }
    }

    private fun loadInter() {
        viewModel.addWheel(
            title = binding.edtAddEditNewRouletteName.text.toString(),
            repeatCount = binding.slAddEditNewRouletteRepeat.getValueStep()
        )
    }

    private fun loadNative() {

    }
}