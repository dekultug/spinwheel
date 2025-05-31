package com.amvfunny.dev.wheelist.base.common.extention

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Insets
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.health.connect.datatypes.AppInfo
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amvfunny.dev.wheelist.R
import com.amvfunny.dev.wheelist.base.common.loader.image.LoadImageFactory
import com.amvfunny.dev.wheelist.base.common.state.STATE_TYPE
import com.amvfunny.dev.wheelist.base.common.state.StateData
import com.amvfunny.dev.wheelist.presentaition.SpinWheelPreferences
import com.amvfunny.dev.wheelist.presentaition.getApplication
import com.amvfunny.dev.wheelist.presentaition.widget.spinview.ripple.RippleBackground
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale


const val DEFAULT_DEBOUNCE_INTERVAL = 500L

fun View.setOnSafeClick(
    delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL,
    click: (view: View) -> Unit
) {
    setOnClickListener(object : DebouncedOnClickListener(delayBetweenClicks) {
        override fun onDebouncedClick(v: View) = click(v)
    })
}

fun View.setOnSafeGlobalClick(
    delayBetweenClicks: Long = DEFAULT_DEBOUNCE_INTERVAL,
    click: (view: View) -> Unit
) {
    setOnClickListener(object : GlobalDebouncedOnClickListener(delayBetweenClicks) {
        override fun onDebouncedClick(v: View) = click(v)
    })
}

fun View.setOnScaleClick(action: (() -> Unit)) {
    setOnSafeClick {
        scaleAnimation()
        action()
    }
}

fun View.scaleAnimation() {
    animate()
        .scaleX(1.3f)
        .scaleY(1.3f)
        .setDuration(150)
        .setInterpolator(DecelerateInterpolator())
        .withEndAction {
            ViewCompat.animate(this)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(50).interpolator = AccelerateInterpolator()
        }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

const val BOOLEAN_DEFAULT = false
const val INT_DEFAULT = 0
const val LONG_DEFAULT = 0L
const val DOUBLE_DEFAULT = 0.0
const val FLOAT_DEFAULT = 0f
const val STRING_DEFAULT = ""

const val TIME_DELAY_CHANGE_STATUS_BAR = 150L

fun View.hideKeyBoard() {
    val imm = context.applicationContext.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}
fun View.showKeyBoard() {
    val imm = context.applicationContext.getSystemService(
        Context.INPUT_METHOD_SERVICE
    ) as? InputMethodManager
    imm?.showSoftInput(this,0)
}

fun TextView.setImageLeft(left: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(left, null, null, null)
}

fun TextView.setImageTop(top: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, top, null, null)
}

fun TextView.setImageRight(right: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, right, null)
}

fun TextView.setImageBottom(bottom: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom)
}

fun TextView.setImageLeftRight(left: Drawable?, right: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(left, null, right, null)
}

fun TextView.clearImage() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

fun getAppString(
    @StringRes stringId: Int,
    context: Context? = getApplication()
): String {
    return context?.getString(stringId) ?: ""
}

fun getAppString(
    @StringRes stringId: Int,
    context: Context? = getApplication(),
    vararg params: Any,
): String {
    return context?.getString(stringId, *params) ?: ""
}

fun getAppSpannableString(
    @StringRes stringId: Int,
    context: Context? = getApplication()
): SpannableString {
    return SpannableString(context?.getString(stringId))
}

fun getAppFont(
    @FontRes fontId: Int,
    context: Context? = getApplication()
): Typeface? {
    return context?.let {
        ResourcesCompat.getFont(it, fontId)
    }
}

fun getAppDrawable(
    @DrawableRes drawableId: Int,
    context: Context? = getApplication()
): Drawable? {
    return context?.let {
        ContextCompat.getDrawable(it, drawableId)
    }
}

fun getAppDimensionPixel(
    @DimenRes dimenId: Int,
    context: Context? = getApplication()
): Int {
    return context?.resources?.getDimensionPixelSize(dimenId) ?: -1
}

fun getAppDimension(
    @DimenRes dimenId: Int,
    context: Context? = getApplication()
): Float {
    return context?.resources?.getDimension(dimenId) ?: -1f
}

fun getAppColor(
    @ColorRes colorRes: Int,
    context: Context? = getApplication()
): Int {
    return context?.let {
        ContextCompat.getColor(it, colorRes)
    } ?: Color.TRANSPARENT
}

fun Activity.getScreenHeight(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}

private var lastPress: Long = 0

private const val EXIT_APP_DELAY = 1500

interface IStateData {
    fun onInit() {}
    fun onError() {}
    fun onSuccess()
}

fun <Data> handleStateData(stateData: StateData<Data>, listener: IStateData) {
    if (stateData.data == null) return
    when (stateData.status) {
        STATE_TYPE.INIT -> listener.onInit()
        STATE_TYPE.ERROR -> listener.onError()
        STATE_TYPE.SUCCESS -> listener.onSuccess()
    }
}

fun <Data> AppCompatActivity.coroutinesLaunch(
    data: Flow<StateData<Data>>,
    launch: suspend (stateData: StateData<Data>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            data.collect {
                launch.invoke(it)
            }
        }
    }
}

fun <Data> Fragment.coroutinesLaunch(
    data: Flow<StateData<Data>>,
    launch: suspend (stateData: StateData<Data>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            data.collect {
                launch.invoke(it)
            }
        }
    }
}

fun ImageView.loadImage(url: String) {
    LoadImageFactory.getLoadImage().loadImage(
        view = this,
        url = url,
        placeHolder = getAppDrawable(R.drawable.ic_launcher_background)
    )
}

fun ImageView.loadImage(drawable: Drawable?) {
    this.setImageDrawable(drawable)
}

fun List<RippleBackground.RippleView>.getValueColor(): Int {
    var color = 0
    this.forEach {
        color = it.getColor()
    }
    return color
}

fun List<RippleBackground.RippleView>.setColor(color: Int) {
    this.forEach {
        it.setColor(color)
    }
}

fun List<RippleBackground.RippleView>.resetColor() {
    this.forEach {
        it.setColor(null)
    }
}

fun Context.setLocale(code: String): Context {
    val myLocale = Locale(code)
    val res = resources
    val conf: Configuration = res.configuration
    conf.setLocale(myLocale)
    this.createConfigurationContext(conf)
    return this
}

fun Activity.getStringResourceByName(aString: String?): String {
    val conf: Configuration = this.resources.configuration
    conf.locale = Locale(SpinWheelPreferences.valueCodeLanguage)
    val metrics = DisplayMetrics()
    this.windowManager?.defaultDisplay?.getMetrics(metrics)
    val resources = Resources(this.assets, metrics, conf)
    val stringWithOutSpaces = aString?.replace(" ", "")
    val stringWithOut = stringWithOutSpaces?.replace("-", "")
    val stirngTolowerCase = stringWithOut?.lowercase(Locale.ROOT)
    //
    val packageName: String = this.packageName
    val resId: Int = resources.getIdentifier(stirngTolowerCase, "string", packageName)

    return if (resId == 0) {
        aString ?: ""
    } else resources.getString(resId)
}

fun Context.copyToClipboard(data: String?) {
    val clipboardManager = getSystemService(ClipboardManager::class.java)
    clipboardManager.setPrimaryClip(
        ClipData.newPlainText("Miwiz", data)
    )
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun Context.shareLink(message: String, appInfo: AppInfo) {
    try {
        val share = Intent(Intent.ACTION_SEND)
        share.setPackage(appInfo.packageName)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)

        startActivity(Intent.createChooser(share, ""))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.shareDefault(data: String) {
    val appPackageName = this.packageName
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(
        Intent.EXTRA_TEXT,
        data
    )
    sendIntent.type = "text/plain"
    this.startActivity(sendIntent)
}

fun setUpGradient(
    values: IntArray,
    radius: Float? = null,
    strokeColor: Int? = null,
    strokeWidth: Int? = null
): Drawable {
    return GradientDrawable().apply {
        colors = values
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradientType = GradientDrawable.LINEAR_GRADIENT
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius ?: FLOAT_DEFAULT
        if (strokeColor != null && strokeWidth != null) {
            setStroke(strokeWidth,strokeColor)
        }
    }
}

fun View.setGradientMain(radius: Float? = null) {
    val gradient = GradientDrawable().apply {
        colors = intArrayOf(
            getAppColor(R.color.orange_primary_light),
            getAppColor(R.color.orange_primary_bold),
        )
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradientType = GradientDrawable.LINEAR_GRADIENT
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius ?: FLOAT_DEFAULT
    }
    this.background = gradient
}

fun View.setGradientButton(radius: Float? = null) {
    val gradient = GradientDrawable().apply {
        colors = intArrayOf(
            getAppColor(R.color.orange_gradient_first),
            getAppColor(R.color.orange_gradient_second),
        )
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradientType = GradientDrawable.LINEAR_GRADIENT
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius ?: FLOAT_DEFAULT
        setStroke(getAppDimension(R.dimen.dimen_1).toInt(), getAppColor(R.color.stroke_button_orange))
    }
    this.background = gradient
}

fun View.setGradientDivider(radius: Float? = null) {
    val gradient = GradientDrawable().apply {
        colors = intArrayOf(
            getAppColor(R.color.m100),
            getAppColor(R.color.m100_2),
        )
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradientType = GradientDrawable.LINEAR_GRADIENT
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius ?: FLOAT_DEFAULT
    }
    this.background = gradient
}

fun View.setGradientPurple(radius: Float?){
    val gradient = GradientDrawable().apply {
        colors = intArrayOf(
            getAppColor(R.color.m60_first),
            getAppColor(R.color.m60_second),
        )
        orientation = GradientDrawable.Orientation.TOP_BOTTOM
        gradientType = GradientDrawable.LINEAR_GRADIENT
        shape = GradientDrawable.RECTANGLE
        cornerRadius = radius ?: FLOAT_DEFAULT
        setStroke(getAppDimension(R.dimen.dimen_1).toInt(), getAppColor(R.color.stroke_button_purple))
    }
    this.background = gradient
}

fun screenShot(view: View, customHeight: Int? = null): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, customHeight?:view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun Context.shareImage(bitmap: Bitmap) {

    // save bitmap to cache directory
    try {
        val cachePath: File = File(this.getCacheDir(), "images")
        cachePath.mkdirs() // don't forget to make the directory
        val stream = FileOutputStream("$cachePath/image.png") // overwrites this image every time
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val imagePath: File = File(this.getCacheDir(), "images")
    val newFile = File(imagePath, "image.png")
    val contentUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", newFile)

    if (contentUri != null) {
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(shareIntent, "Choose an app"))
    }
}

fun Context.hasNetworkConnection(): Boolean {
    var haveConnectedWifi = false
    var haveConnectedMobile = false
    val cm =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.allNetworkInfo
    for (ni in netInfo) {
        if (ni.typeName.equals("WIFI", ignoreCase = true))
            if (ni.isConnected) haveConnectedWifi = true
        if (ni.typeName.equals("MOBILE", ignoreCase = true))
            if (ni.isConnected) haveConnectedMobile = true
    }
    return haveConnectedWifi || haveConnectedMobile
}

