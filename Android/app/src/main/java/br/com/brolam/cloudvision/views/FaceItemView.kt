package br.com.brolam.cloudvision.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View

import br.com.brolam.cloudvision.R
import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.BitmapShader

class FaceItemView : View {
    private var faceDrawable: Drawable? = null
    private val borderPaint = Paint()
    private val facePaint = Paint()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val attributes = context.obtainStyledAttributes(
                attrs, R.styleable.FaceItemView, defStyle, 0)

        if (attributes.hasValue(R.styleable.FaceItemView_faceDrawable)) {
            faceDrawable = attributes.getDrawable(
                    R.styleable.FaceItemView_faceDrawable)
            faceDrawable!!.callback = this
        }
        borderPaint.style = Paint.Style.FILL
        borderPaint.isAntiAlias = true
        borderPaint.color = Color.TRANSPARENT
        facePaint.isAntiAlias = true
        facePaint.style = Paint.Style.FILL
        attributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        this.initializeFacePaint()
    }

    private fun initializeFacePaint() {
        if (faceDrawable == null) return
        val faceBitmapWithViewSize = getBitmapSameViewSize(drawable = faceDrawable!!, width = width, height = height)
        val bitmapShader = BitmapShader(faceBitmapWithViewSize, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        facePaint.shader = bitmapShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (faceDrawable == null) return
        val drawableRadius = Math.min(width / 2.0f, width / 2.0f)
        val centerWidth = (width / 2.0).toFloat()
        val centerHeight = (height / 2.0).toFloat()
        canvas.drawCircle(centerWidth, centerHeight, drawableRadius, borderPaint)
        canvas.drawCircle(centerWidth, centerHeight, drawableRadius - 1, facePaint)
    }

    private fun getBitmapSameViewSize(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
