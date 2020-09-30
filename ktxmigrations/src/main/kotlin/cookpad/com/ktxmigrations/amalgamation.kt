package com.cookpad.ktxmigrations

import android.animation.Animator
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.location.Location
import android.net.Uri
import android.os.*
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.transition.Transition
import android.util.*
import android.util.AtomicFile
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.addListener
import androidx.core.content.contentValuesOf
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.content.withStyledAttributes
import androidx.core.database.*
import androidx.core.database.sqlite.transaction
import androidx.core.graphics.*
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.drawable.*
import androidx.core.location.component1
import androidx.core.location.component2
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.core.os.*
import androidx.core.text.*
import androidx.core.transition.*
import androidx.core.util.*
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.*
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.*
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.Serializable
import java.lang.Runnable
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * In order to make KTX migration easier, this file collects all the ktx functions alongside with the key calls of their implementation,
 * that way it will be easy searching for calls that can be potentially replaced by ktx.
 */

lateinit var context: Context

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/animation/Animator.kt
private fun migrateAnimator() {
    lateinit var animator: Animator
    lateinit var listener: Animator.AnimatorListener

    // Without KTX
    animator.addListener(listener)

    // With KTX
    animator.addListener(onEnd = {})
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/content/ContentValues.kt
private fun migrateContentValues() {
    // Without KTX
    ContentValues().apply { put("key", 1) }

    // With KTX
    contentValuesOf("key" to 1)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/content/Context.kt
private fun migrateContext() {
    // Without KTX
    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    // With KTX
    context.getSystemService<TelephonyManager>()

    //--//

    val attrs = intArrayOf(android.R.attr.listDivider)

    // Without KTX
    val style = context.obtainStyledAttributes(attrs)
    style.recycle()

    // With KTX
    context.withStyledAttributes(attrs = attrs, block = {

    })
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/content/SharedPreferences.kt
private fun migrateSharedPreferences() {
    lateinit var sharedPref: SharedPreferences

    // Without KTX
    val editor = sharedPref.edit()
    editor.putBoolean("key", true)
    editor.apply()

    // With KTX
    sharedPref.edit {
        putBoolean("key", true)
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/database/Cursor.kt
private fun migrateCursor() {
    lateinit var cursor: Cursor
    val columnIndex = cursor.getColumnIndexOrThrow("columnName")

    // Without KTX
    with(cursor) {
        getBlob(columnIndex)
        getDouble(columnIndex)
        getFloat(columnIndex)
        getInt(columnIndex)
        getLong(columnIndex)
        getShort(columnIndex)
        getString(columnIndex)
    }

    // With KTX
    with(cursor) {
        getBlobOrNull(columnIndex)
        getDoubleOrNull(columnIndex)
        getFloatOrNull(columnIndex)
        getIntOrNull(columnIndex)
        getLongOrNull(columnIndex)
        getShortOrNull(columnIndex)
        getStringOrNull(columnIndex)
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/database/sqlite/SQLiteDatabase.kt
private fun migrateSQLite() {
    lateinit var database: SQLiteDatabase

    // Without KTX
    database.beginTransaction()
    try {
        val newRowId = database.insert("table", null, ContentValues().apply { put("key", 1) })
        database.setTransactionSuccessful()
    } finally {
        database.endTransaction()
    }

    // With KTX
    database.transaction {
        val newRowId = database.insert("table", null, contentValuesOf("key" to 1))
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Bitmap.kt
private fun migrateBitmap() {
    lateinit var bitmap: Bitmap

    // Without KTX
    val canvas = Canvas(bitmap)
    canvas.apply {
        drawColor(Color.BLACK)
    }

    // With KTX
    bitmap.applyCanvas {
        drawColor(Color.BLACK)
    }

    //--//

    // Without KTX
    bitmap.getPixel(0, 0)
    bitmap.setPixel(0, 0, Color.BLACK)

    // With KTX
    bitmap[0, 0]
    bitmap[0, 0] = Color.BLACK

    //--//

    // Without KTX
    Bitmap.createScaledBitmap(bitmap, 10, 10, true)

    // With KTX
    bitmap.scale(width = 10, height = 10)

    //--//

    // Without KTX
    Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)

    // With KTX
    createBitmap(width = 10, height = 10)

    //--//

    val p = Point()

    // Without KTX
    p.x >= 0 && p.x < bitmap.width && p.y >= 0 && p.y < bitmap.height

    // With KTX
    bitmap.contains(p)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Canvas.kt
private fun migrateCanvas() {
    lateinit var canvas: Canvas
    lateinit var matrix: Matrix
    lateinit var rect: Rect

    // Without KTX
    val checkpoint = canvas.save()
    with(canvas) {
        translate(0f, 0f)
        rotate(0f, 0f, 0f)
        scale(0f, 0f)
        skew(0f, 0f)
        concat(matrix)
        clipRect(rect)
    }
    try {
        canvas.apply { drawColor(Color.BLACK) }
    } finally {
        canvas.restoreToCount(checkpoint)
    }

    // With KTX
    with(canvas) {
        withSave { drawColor(Color.BLACK) }
        withTranslation(0f, 0f) { drawColor(Color.BLACK) }
        withRotation(0f, 0f) { drawColor(Color.BLACK) }
        withScale(0f, 0f) { drawColor(Color.BLACK) }
        withSkew(0f, 0f) { drawColor(Color.BLACK) }
        withMatrix(matrix) { drawColor(Color.BLACK) }
        withClip(rect) { drawColor(Color.BLACK) }
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Color.kt
@RequiresApi(Build.VERSION_CODES.O)
fun migrateColor() {
    lateinit var color: Color

    // Without KTX
    color.getComponent(0)
    color.getComponent(1)
    color.getComponent(2)
    color.getComponent(3)

    // With KTX
    val (red, green, blue, alpha) = color

    //--//

    lateinit var color2: Color

    // Without KTX
    ColorUtils.compositeColors(color, color2)

    // With KTX
    color + color2

    //--//

    // Without KTX
    Color.alpha(Color.BLACK)
    Color.red(Color.BLACK)
    Color.green(Color.BLACK)
    Color.blue(Color.BLACK)

    // With KTX
    Color.BLACK.alpha
    Color.BLACK.red
    Color.BLACK.green
    Color.BLACK.blue

    //--//

    // Without KTX
    Color.luminance(Color.BLACK)

    // With KTX
    Color.BLACK.luminance

    //--//

    // Without KTX
    Color.valueOf(Color.BLACK)

    // With KTX
    Color.BLACK.toColor()

    //--//

    // Without KTX
    Color.pack(Color.BLACK)

    // With KTX
    Color.BLACK.toColorLong()

    //--//

    // Without KTX
    Color.convert(Color.BLACK, ColorSpace.get(ColorSpace.Named.ACES))

    // With KTX
    Color.BLACK.convertTo(ColorSpace.Named.ACES)

    //--//

    // Without KTX
    Color.convert(Color.BLACK, ColorSpace.get(ColorSpace.Named.ACES))

    // With KTX
    Color.BLACK.convertTo(ColorSpace.Named.ACES)

    //--//

    // Without KTX
    Color.parseColor("green")

    // With KTX
    "green".toColorInt()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/ImageDecoder.kt
@RequiresApi(Build.VERSION_CODES.P)
fun migrateImageDecoder() {
    lateinit var source: ImageDecoder.Source

    // Without KTX
    ImageDecoder.decodeBitmap(source) { decoder, info, source -> }
    ImageDecoder.decodeDrawable(source) { decoder, info, source -> }

    // With KTX
    source.decodeBitmap { info, source -> }
    source.decodeDrawable { info, source -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Matrix.kt
fun migrateMatrix() {
    lateinit var matrix: Matrix
    lateinit var matrix2: Matrix

    // Without KTX
    Matrix(matrix).apply { preConcat(matrix2) }

    // With KTX
    matrix.times(matrix2)

    //--//

    // Without KTX
    FloatArray(9).apply { matrix.getValues(this) }

    // With KTX
    matrix.values()

    //--//

    // Without KTX
    Matrix().apply { setTranslate(1f, 1f) }
    Matrix().apply { setScale(1f, 1f) }
    Matrix().apply { setRotate(1f) }

    // With KTX
    translationMatrix(tx = 1f, ty = 1f)
    scaleMatrix(sx = 1f, sy = 1f)
    rotationMatrix(degrees = 1f)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Paint.kt
@RequiresApi(Build.VERSION_CODES.Q)
fun migratePaint() {
    lateinit var paint: Paint

    // Without KTX
    PaintCompat.setBlendMode(paint, BlendModeCompat.CLEAR)

    // With KTX
    paint.blendMode = BlendMode.CLEAR
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Path.kt
@RequiresApi(Build.VERSION_CODES.O)
fun migratePath() {
    lateinit var path: Path
    lateinit var path2: Path

    // Without KTX
    PathUtils.flatten(path, 0.5f)

    // With KTX
    path.flatten(error = 0.5f)

    //--//

    // Without KTX
    Path(path).apply { op(path2, Path.Op.UNION) }
    Path(path).apply { op(path2, Path.Op.DIFFERENCE) }
    Path(path).apply { op(path2, Path.Op.INTERSECT) }
    Path(path).apply { op(path2, Path.Op.XOR) }

    // With KTX
    path + path2
    path - path2
    path.and(path2)
    path.xor(path2)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Picture.kt
fun migratePicture() {
    lateinit var picture: Picture

    // Without KTX
    val canvas = picture.beginRecording(1, 1)
    try {
        canvas.drawColor(Color.BLACK)
    } finally {
        picture.endRecording()
    }

    // With KTX
    picture.record(width = 1, height = 1) {
        drawColor(Color.BLACK)
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Point.kt
fun migratePoint() {
    lateinit var point: Point
    lateinit var pointF: PointF

    // Without KTX
    point.component1()
    point.component2()
    pointF.component1()
    pointF.component2()

    // With KTX
    point.x to point.y
    pointF.x to pointF.y


    //--//

    // Without KTX
    point.apply { offset(point.x, point.y) }
    pointF.apply { offset(pointF.x, pointF.y) }

    // With KTX
    point + point
    pointF + pointF

    //--//

    // Without KTX
    Point(pointF.x.toInt(), pointF.y.toInt())
    PointF(point)

    // With KTX
    pointF.toPoint()
    point.toPointF()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/PorterDuff.kt
fun migratePorterDuff() {
    lateinit var porterDuffMode: PorterDuff.Mode

    // Without KTX
    PorterDuffXfermode(porterDuffMode)

    // With KTX
    porterDuffMode.toXfermode()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Shader.kt
fun migrateShader() {
    lateinit var shader: Shader

    // Without KTX
    val matrix = Matrix()
    shader.getLocalMatrix(matrix)
    shader.setLocalMatrix(matrix)

    // With KTX
    shader.transform { }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Rect.kt
fun migrateRect() {
    lateinit var point: Point
    lateinit var pointf: PointF

    lateinit var rect: Rect
    lateinit var rect2: Rect

    lateinit var rectf: RectF
    lateinit var rectf2: RectF

    // Without KTX
    rect.left
    rect.top
    rect.right
    rect.bottom
    rectf.left
    rectf.top
    rectf.right
    rectf.bottom

    // With KTX
    val (_left, _top, _right, _bottom) = rect
    val (leftR, topR, rightR, bottomR) = rectf

    //--//

    // Without KTX
    Rect(rect).apply { union(rect2) }
    RectF(rectf).apply { union(rectf2) }
    Rect(rect).apply { offset(5, 5) }
    RectF(rect).apply { offset(5f, 5f) }

    // With KTX
    rect + rect2
    rectf + rectf2

    //--//

    // Without KTX
    Region(rect).apply { op(rect2, Region.Op.DIFFERENCE) }

    // With KTX
    rect - rect2

    //--//

    // Without KTX
    Rect(rect).apply {
        top *= 5
        left *= 5
        right *= 5
        bottom *= 5
    }

    // With KTX
    rect.times(factor = 5)

    //--//

    // Without KTX
    Rect(rect).apply { intersect(rect2) }
    RectF(rectf).apply { intersect(rectf2) }
    Region(rect).apply { op(rect2, Region.Op.XOR) }

    // With KTX
    rect.and(rect2)
    rectf.and(rectf2)
    rect.or(rect2)

    //--//

    // Without KTX
    rect.contains(point.x, point.y)
    rectf.contains(pointf.x, pointf.y)

    // With KTX
    rect.contains(point)
    rectf.contains(pointf)

    //--//

    // Without KTX
    RectF(rect)
    val r = Rect()
    rectf.roundOut(r)

    // With KTX
    rect.toRectF()
    rectf.toRect()

    //--//

    // Without KTX
    Region(rect)

    // With KTX
    rect.toRegion()

    //--//

    // Without KTX
    rectf.apply { Matrix().mapRect(rectf) }

    // With KTX
    rectf.transform(Matrix())
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/Region.kt
fun migrateRegion() {
    lateinit var region: Region
    lateinit var region2: Region
    lateinit var rect: Rect

    // Without KTX
    Region(region).apply { union(rect) }
    Region(region).apply { op(region2, Region.Op.UNION) }
    Region(region).apply { op(rect, Region.Op.DIFFERENCE) }
    Region(region).apply { op(region2, Region.Op.DIFFERENCE) }
    Region(region).apply { op(rect, Region.Op.INTERSECT) }
    Region(region).apply { op(region2, Region.Op.INTERSECT) }
    Region(region).apply { op(rect, Region.Op.XOR) }
    Region(region).apply { op(region2, Region.Op.XOR) }

    // With KTX
    region + rect
    region + region2
    region - rect
    region - region2
    region.and(rect)
    region.and(region2)
    region.xor(rect)
    region.xor(region2)

    //--//

    // Without KTX
    val iterator = RegionIterator(region)
    while (true) {
        val r = Rect()
        if (!iterator.next(r)) {
            break
        }
    }

    // With KTX
    region.forEach { r -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/drawable/BitmapDrawable.kt
fun migrateBitmapDrawable() {
    lateinit var bitmap: Bitmap
    lateinit var resources: Resources

    // Without KTX
    BitmapDrawable(resources, bitmap)

    // With KTX
    bitmap.toDrawable(resources)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/drawable/ColorDrawable.kt
@RequiresApi(Build.VERSION_CODES.O)
fun migrateColorDrawable() {
    lateinit var color: Color

    // Without KTX
    ColorDrawable(Color.BLACK)
    ColorDrawable(color.toArgb())

    // With KTX
    Color.BLACK.toDrawable()
    color.toDrawable()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/drawable/Drawable.kt
fun migrateDrawable() {
    lateinit var drawable: Drawable
    lateinit var bitmap: Bitmap

    // Without KTX
    // There is actually a lot of more of code in the implementation but this line is the key to spot potential replacements
    drawable.draw(Canvas(bitmap))

    // With KTX
    drawable.toBitmap()

    //--//

    // Without KTX
    drawable.setBounds(1, 1, 1, 1)

    // With KTX
    drawable.apply {
        updateBounds(left = 1)
        updateBounds(top = 1)
        updateBounds(right = 1)
        updateBounds(bottom = 1)
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/graphics/drawable/Icon.kt
@RequiresApi(Build.VERSION_CODES.O)
fun migrateIcon() {
    lateinit var bitmap: Bitmap
    lateinit var uri: Uri
    lateinit var byteArray: ByteArray

    // Without KTX
    Icon.createWithAdaptiveBitmap(bitmap)
    Icon.createWithBitmap(bitmap)
    Icon.createWithContentUri(uri)
    Icon.createWithData(byteArray, 0, byteArray.size)

    // With KTX
    bitmap.toAdaptiveIcon()
    bitmap.toIcon()
    uri.toIcon()
    byteArray.toIcon()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/location/Location.kt
fun migrateLocation() {
    lateinit var location: Location

    // Without KTX
    location.latitude
    location.longitude

    // With KTX
    val (lat, lon) = location
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/net/Uri.kt
fun migrateUri() {
    lateinit var uri: Uri
    lateinit var uriString: String

    // Without KTX
    Uri.parse(uriString)
    File(uri.path!!)

    // With KTX
    uriString.toUri()
    uri.toFile()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/os/Bundle.kt
fun migrateBundle() {
    lateinit var bundle: Bundle
    lateinit var key: String
    lateinit var parcelable: Parcelable
    lateinit var serializable: Serializable

    // Without KTX
    bundle.apply {
        putString(key, "value")
        putBoolean(key, true)
        putDouble(key, .0)
        putFloat(key, .0f)
        putInt(key, 0)
        putBundle(key, bundle)
        putParcelable(key, parcelable)
        putSerializable(key, serializable)
    }

    // With KTX
    bundleOf(
        key to "value",
        key to true,
        key to .0,
        key to .0f,
        key to 0,
        key to bundle,
        key to serializable
    )
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/os/Handler.kt
fun migrateHandler() {
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var token: Any
    val millis = 1L

    // Without KTX
    handler.postDelayed(runnable, millis)
    HandlerCompat.postDelayed(handler, runnable, token, millis)
    handler.postAtTime(runnable, token, millis)

    // With KTX
    handler.postDelayed(millis) {}
    handler.postAtTime(millis) {}
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/os/PersistableBundle.kt
fun migratePersistableBundle() {
    lateinit var bundle: PersistableBundle
    lateinit var key: String

    // Without KTX
    bundle.apply {
        putString(key, "value")
        putDouble(key, .0)
        putInt(key, 0)
    }

    // With KTX
    persistableBundleOf(
        key to "value",
        key to .0,
        key to 0
    )
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/os/Trace.kt
fun migrateTrace() {
    lateinit var sectionName: String

    // Without KTX
    TraceCompat.beginSection(sectionName)
    TraceCompat.endSection()

    // With KTX
    trace(sectionName) {

    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/text/CharSequence.kt
fun migrateCharSequence() {
    lateinit var content: CharSequence

    // Without KTX
    TextUtils.isDigitsOnly(content)
    TextUtils.getTrimmedLength(content)

    // With KTX
    content.isDigitsOnly()
    content.trimmedLength()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/text/Html.kt
fun migrateHTML() {
    lateinit var source: String
    lateinit var spanned: Spanned

    // Without KTX
    HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY, null, null)
    HtmlCompat.toHtml(spanned, HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)

    // With KTX
    source.parseAsHtml()
    spanned.toHtml()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/text/Locale.kt
fun migrateLocale() {
    lateinit var locale: Locale

    // Without KTX
    TextUtils.getLayoutDirectionFromLocale(locale)

    // With KTX
    locale.layoutDirection
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/text/SpannableString.kt
fun migrateSpannableString() {
    lateinit var spannable: Spannable
    lateinit var underlineSpan: UnderlineSpan

    // Without KTX
    spannable.apply {
        getSpans<Any>(0, spannable.length, Any::class.java).forEach { removeSpan(it) }
    }
    spannable.setSpan(underlineSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    // With KTX
    spannable.clearSpans()
    spannable[0, 5] = underlineSpan
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/text/String.kt
fun migrateString() {
    // Without KTX
    TextUtils.htmlEncode("value")

    // With KTX
    "value".htmlEncode()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/transition/Transition.kt
fun migrateTransition() {
    lateinit var listener: Transition.TransitionListener
    lateinit var transition: Transition

    // Without KTX
    transition.addListener(listener)

    // With KTX
    transition.doOnCancel { }
    transition.doOnEnd { }
    transition.doOnPause { }
    transition.doOnResume { }
    transition.doOnStart { }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/AtomicFile.kt
fun migrateAtomicFile() {
    lateinit var file: AtomicFile

    // Without KTX
    file.apply {
        val stream = startWrite()
        var success = false
        try {
            success = true
        } finally {
            if (success) {
                finishWrite(stream)
            } else {
                failWrite(stream)
            }
        }
    }

    // With KTX
    file.tryWrite { out -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/Half.kt
@RequiresApi(Build.VERSION_CODES.O)
fun migrateHalf() {
    val short: Short = 2
    val double: Float = .2f
    val string = "2"

    // Without KTX
    Half.valueOf(short)
    Half.valueOf(double)
    Half.valueOf(string)

    // With KTX
    short.toHalf()
    double.toHalf()
    string.toHalf()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/LongSparseArray.kt
fun migrateLongSparseArray() {
    lateinit var array: LongSparseArray<Int>
    lateinit var other: LongSparseArray<Int>

    // Without KTX
    array.size()
    array.indexOfKey(1) >= 0
    array.indexOfValue(1) >= 0
    array.put(1, 1)
    for (index in 0 until array.size()) {
        other.put(array.keyAt(index), array.valueAt(index))
    }
    array.size() == 0
    array.size() != 0
    val index = array.indexOfKey(1)
    if (index >= 0 && 1 == array.valueAt(index)) {
        array.removeAt(index)
    }

    // With KTX
    array.size
    array.containsKey(1)
    array.containsValue(1)
    array[1] = 1
    array + other
    array.isEmpty()
    array.isNotEmpty()
    array.remove(1)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/LruCache.kt
fun migrateLruCache() {
    val cacheSize = 4 * 1024 * 1024

    // Without KTX
    LruCache<String, Bitmap>(cacheSize)

    // With KTX
    lruCache<String, Bitmap>(cacheSize)
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/Pair.kt
fun migratePair() {
    lateinit var pair: android.util.Pair<String, String>

    // Without KTX
    pair.first
    pair.second
    kotlin.Pair(pair.first, pair.second)

    // With KTX
    val (first, second) = pair
    pair.toKotlinPair()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/Size.kt
fun migrateSize() {
    lateinit var size: android.util.Size
    lateinit var sizef: android.util.SizeF

    // Without KTX
    size.width
    size.height
    sizef.width
    sizef.height

    // With KTX
    val (width, height) = size
    val (widthF, heightF) = sizef
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/SparseArray.kt
fun migrateSparseArray() {
    lateinit var array: SparseArray<String>

    // Without KTX
    array.size()
    array.indexOfKey(0) >= 0
    array.put(0, "1")
    array.size() == 0
    array.size() != 0
    for (index in 0 until array.size()) {
        val key = array.keyAt(index)
        val value = array.valueAt(index)
    }

    // With KTX
    array.size
    array.contains(0)
    array[0] = "1"
    array.isEmpty()
    array.isNotEmpty()
    array.forEach { key, value -> }
}


// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/SparseArray.kt
@RequiresApi(Build.VERSION_CODES.P)
fun migrateSparseBooleanArray() {
    lateinit var array: SparseBooleanArray

    // Without KTX
    array.size()
    array.indexOfKey(0) >= 0
    array.put(0, true)
    val index = array.indexOfKey(1)
    if (index >= 0 && array.valueAt(index)) {
        array.removeAt(index)
    }
    array.size() == 0
    array.size() != 0
    for (index in 0 until array.size()) {
        val key = array.keyAt(index)
        val value = array.valueAt(index)
    }

    // With KTX
    array.size
    array.contains(0)
    array[0] = true
    array.remove(0, true)
    array.isEmpty()
    array.isNotEmpty()
    array.forEach { key, value -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/SparseIntArray.kt
fun migrateSparseIntArray() {
    lateinit var array: SparseIntArray

    // Without KTX
    array.size()
    array.indexOfKey(0) >= 0
    array.put(0, 1)
    val index = array.indexOfKey(1)
    if (index >= 0 && 1 == array.valueAt(index)) {
        array.removeAt(index)
    }
    array.size() == 0
    array.size() != 0
    for (index in 0 until array.size()) {
        val key = array.keyAt(index)
        val value = array.valueAt(index)
    }

    // With KTX
    array.size
    array.contains(0)
    array[0] = 1
    array.remove(0, 1)
    array.isEmpty()
    array.isNotEmpty()
    array.forEach { key, value -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/util/SparseLongArray.kt
fun migrateSparseLongArray() {
    lateinit var array: SparseLongArray

    // Without KTX
    array.size()
    array.indexOfKey(0) >= 0
    array.put(0, 1)
    val index = array.indexOfKey(1)
    if (index >= 0 && 1L == array.valueAt(index)) {
        array.removeAt(index)
    }
    array.size() == 0
    array.size() != 0
    for (index in 0 until array.size()) {
        val key = array.keyAt(index)
        val value = array.valueAt(index)
    }

    // With KTX
    array.size
    array.contains(0)
    array[0] = 1
    array.remove(0, 1L)
    array.isEmpty()
    array.isNotEmpty()
    array.forEach { key, value -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/view/Menu.kt
fun migrateMenu() {
    lateinit var menu: Menu
    lateinit var item: MenuItem

    // Without KTX
    for (index in 0 until menu.size()) {
        if (menu.getItem(index) == item) {
        }
    }
    menu.removeItem(item.itemId)
    menu.size()
    menu.size() == 0
    menu.size() != 0
    for (index in 0 until menu.size()) {
        val _item = menu.getItem(index)
    }


    // With KTX
    menu.contains(item)
    menu -= item
    menu.size
    menu.isEmpty()
    menu.isNotEmpty()
    menu.forEach { }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/view/View.kt
fun migrateView() {
    lateinit var view: View
    lateinit var listener: View.OnLayoutChangeListener
    lateinit var attachListener: View.OnAttachStateChangeListener

    // Without KTX
    view.addOnLayoutChangeListener(listener)
    OneShotPreDrawListener.add(view) { }
    view.addOnAttachStateChangeListener(attachListener)
    view.setPadding(0, 0, 0, 0)
    view.postDelayed(Runnable { }, 1000)
    view.postOnAnimationDelayed(Runnable { }, 1000)
    view.apply {
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).applyCanvas {
            translate(-scrollX.toFloat(), -scrollY.toFloat())
            draw(this)
        }
    }
    view.visibility = View.VISIBLE
    view.visibility = View.GONE
    view.visibility = View.INVISIBLE
    view.apply {
        val params = layoutParams
        layoutParams = params
    }
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
    view.layoutParams.let { lp ->
        if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginStart(lp) else 0
    }
    view.layoutParams.let { lp ->
        if (lp is ViewGroup.MarginLayoutParams) MarginLayoutParamsCompat.getMarginEnd(lp) else 0
    }

    // With KTX
    view.doOnNextLayout { }
    view.doOnLayout { }
    view.doOnPreDraw { }
    view.doOnAttach { }
    view.doOnDetach { }
    view.setPadding(0)
    view.postDelayed(1000) { }
    view.postOnAnimationDelayed(1000) { }
    view.drawToBitmap()
    view.isVisible = true
    view.isGone = true
    view.isInvisible = true
    view.updateLayoutParams { }
    view.marginLeft
    view.marginRight
    view.marginTop
    view.marginBottom
    view.marginStart
    view.marginEnd
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/view/ViewGroup.kt
fun migrateViewGroup() {
    lateinit var view: ViewGroup
    lateinit var otherView: ViewGroup

    // Without KTX
    view.getChildAt(0)
    view.indexOfChild(otherView) != -1
    view.addView(otherView)
    view.removeView(otherView)
    view.childCount
    view.childCount == 0
    view.childCount != 0
    view.apply {
        for (index in 0 until childCount) {
            val child = getChildAt(index)

        }
    }

    // With KTX
    view[0]
    view.contains(otherView)
    view += otherView
    view -= otherView
    view.size
    view.isEmpty()
    view.isNotEmpty()
    view.forEach { view -> }
    view.forEachIndexed { index, view -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/core/core-ktx/src/main/java/androidx/core/widget/TextView.kt
fun migrateTextView() {
    lateinit var textView: TextView
    lateinit var textWatcher: TextWatcher

    // Without KTX
    textView.addTextChangedListener(textWatcher)

    // With KTX
    textView.doBeforeTextChanged { text, start, count, after -> }
    textView.doOnTextChanged { text, start, before, count -> }
    textView.doAfterTextChanged { text -> }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/fragment/fragment-ktx/src/main/java/androidx/fragment/app/FragmentManager.kt
fun migrateFragmentManager() {
    lateinit var manager: FragmentManager

    // Without KTX
    val transaction = manager.beginTransaction()
    transaction.commit()
    transaction.commitAllowingStateLoss()
    transaction.commitNow()

    // With KTX
    manager.commit { }
    manager.commit(allowStateLoss = true) { }
    manager.commitNow { }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/fragment/fragment-ktx/src/main/java/androidx/fragment/app/FragmentViewModelLazy.kt
fun migrateFragmentViewModelLazy() {
    lateinit var fragment: Fragment

    // Without KTX
    ViewModelLazy(
        viewModelClass = ViewModel::class,
        storeProducer = { fragment.requireActivity().viewModelStore },
        factoryProducer = { fragment.requireActivity().defaultViewModelProviderFactory }
    )

    // With KTX
    object : Fragment() {
        val viewmodel: ViewModel by viewModels()
        val viewmodelActivityScope: ViewModel by activityViewModels()
    }
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-master-dev/fragment/fragment-ktx/src/main/java/androidx/fragment/app/View.kt
fun migrateFragmentView() {
    lateinit var view: View

    // Without KTX
    FragmentManager.findFragment<Fragment>(view)

    // With KTX
    view.findFragment<Fragment>()
}

// https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/lifecycle/lifecycle-livedata-ktx/src/main/java/androidx/lifecycle/CoroutineLiveData.kt
fun migrateCoroutineLiveData() {
    // Without KTX
    // ??

    // With KTX
    val data: LiveData<Int> = liveData {
        delay(3000)
        emit(3)
    }
}

@InternalCoroutinesApi
fun migrateFlowLiveData() {
    lateinit var _flow: Flow<Int>
    lateinit var liveData: LiveData<Int>

    // Without KTX
    liveData<Int>(EmptyCoroutineContext) {
        _flow.collect(object : FlowCollector<Int> {
            override suspend fun emit(value: Int) {
                emit(value)
            }
        })
    }
    liveData.apply {
        flow {
            val channel = Channel<Int>(Channel.CONFLATED)
            val observer = Observer<Int> {
                channel.offer(it)
            }
            withContext(Dispatchers.Main.immediate) {
                observeForever(observer)
            }
            try {
                for (value in channel) {
                    emit(value)
                }
            } finally {
                GlobalScope.launch(Dispatchers.Main.immediate) {
                    removeObserver(observer)
                }
            }
        }
    }

    // With KTX
    _flow.asLiveData()
    liveData.asFlow()
}

fun migrateTransformations() {
    lateinit var liveData: LiveData<Int>
    lateinit var other: LiveData<Int>
    val number = 1

    // Without KTX
    Transformations.map(liveData) { it + number }
    Transformations.switchMap(liveData) { other }
    Transformations.distinctUntilChanged(liveData)

    // With KTX
    liveData.map { it + number }
    liveData.switchMap { other }
    liveData.distinctUntilChanged()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-activity-release/navigation/navigation-fragment-ktx/src/main/java/androidx/navigation/fragment/Fragment.kt
fun migrateFragment() {
    lateinit var fragment: Fragment

    // Without KTX
    NavHostFragment.findNavController(fragment)

    // With KTX
    fragment.findNavController()
}

// https://android.googlesource.com/platform/frameworks/support/+/refs/heads/androidx-activity-release/navigation/navigation-fragment-ktx/src/main/java/androidx/navigation/fragment/FragmentNavArgsLazy.kt
fun migrateNavArgs() {
    lateinit var fragment: Fragment

    // Without KTX
    NavArgsLazy(NavArgs::class) { fragment.requireArguments() }

    object : Fragment() {
        // With KTX
        val navArgs by navArgs<NavArgs>()
    }
}
