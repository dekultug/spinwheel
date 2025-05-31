package com.amvfunny.dev.wheelist.presentaition.main

import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.DialogScreen
import com.amvfunny.dev.wheelist.base.common.extention.getAppDimension
import com.amvfunny.dev.wheelist.base.common.extention.setGradientButton
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelDialog
import com.amvfunny.dev.wheelist.databinding.ExitAppDlgBinding

class ExitAppDlg: SpinWheelDialog<ExitAppDlgBinding>(R.layout.exit_app_dlg) {

    override fun getBackgroundId() = R.id.flExitApp
    var listener: IExitAppCallBack? = null

    override fun screen(): DialogScreen {
        return DialogScreen(
            mode = DialogScreen.DIALOG_MODE.NORMAL,
            isFullWidth = true,
            isFullHeight = true,
            isDismissByTouchOutSide = false,
            isDismissByOnBackPressed = false
        )
    }

    override fun onInitView() {
        super.onInitView()

        binding.flExitAppCancel.setGradientButton(getAppDimension(R.dimen.dimen_16))

        binding.tvExitAppCancel.setOnClickListener {
            listener?.onExit()
            dismiss()
        }

        binding.tvExitAppStay.setOnClickListener {
            listener?.onCancel()
            dismiss()
        }
    }

    interface IExitAppCallBack{
        fun onCancel()
        fun onExit(){}
    }
}