package com.amvfunny.dev.wheelist.presentaition.roulette.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.DialogScreen
import com.amvfunny.dev.wheelist.base.common.extention.getAppFont
import com.amvfunny.dev.wheelist.base.common.extention.getAppString
import com.amvfunny.dev.wheelist.base.common.extention.setOnSafeClick
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelDialog
import com.amvfunny.dev.wheelist.base.common.screen.SpinWheelFragment
import com.amvfunny.dev.wheelist.base.common.string.FontSpan
import com.amvfunny.dev.wheelist.base.common.string.SpannableBuilder
import com.amvfunny.dev.wheelist.databinding.DeleteRouletteDlgBinding

class DeleteRouletteFragment: SpinWheelDialog<DeleteRouletteDlgBinding>(R.layout.delete_roulette_dlg) {

    companion object{
        private const val TITLE_KEY = "TITLE_KEY"
        fun getInstance(title: String?): DeleteRouletteFragment{
            val fra = DeleteRouletteFragment()
            fra.arguments = bundleOf(TITLE_KEY to title)
            return fra
        }
    }

    private var title: String? = null

    private val spannerBuilder by lazy { SpannableBuilder() }

    var listener: IDeleteListener? = null

    override fun getBackgroundId() = R.id.flDeleteRouletteRoot

    override fun screen(): DialogScreen {
        return DialogScreen(
            isDismissByOnBackPressed = false,
            isDismissByTouchOutSide = false
        )
    }

    override fun onInitView() {
        title = arguments?.getString(TITLE_KEY)
        spannerBuilder.spannedText?.clear()

        spannerBuilder.appendText(getAppString(R.string.warning_pre_delete_roulette,requireContext()))
            .appendText("\n$title")
            .withSpan(FontSpan(getAppFont(R.font.bold)))

        binding.tvDeleteRouletteDlgNameTitle.text = spannerBuilder.spannedText

        binding.cvDeleteRouletteDlgDelete.setOnSafeClick {
            listener?.onDelete()
            dismiss()
        }

        binding.cvDeleteRouletteDlgCancel.setOnSafeClick {
            listener?.onDismiss()
            dismiss()
        }
    }

    interface IDeleteListener{
        fun onDelete()
        fun onDismiss()
    }

}