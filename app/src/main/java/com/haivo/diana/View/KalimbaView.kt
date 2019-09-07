package com.haivo.diana.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.haivo.diana.Model.BaseNote.KALIMBA_NOTES
import com.haivo.diana.R
import java.util.HashMap

class KalimbaView : View {

    private val targetNotePaint = TextPaint()
    private val detectedNotePaint = TextPaint()
    private val backgroundPaint = Paint()
    private val drawable: Drawable by lazy { context.getDrawable(R.drawable.kalimba) }
    private var leftBound: Int = 0
    private var topBound: Int = 0
    private var rightBound: Int = 0
    private var bottomBound: Int = 0
    private var hintRadius: Int = 0
    private var kalimbaActualHeight: Float = 0.toFloat()
    private var kalimbaActualWidght: Float = 0.toFloat()

    private val noteToIndexMap: MutableMap<String, Int> = HashMap() // todo immutable map
    private var currentTargetNoteIndex = -1
    private var currentDetectedNoteIndex = -1
    private var detectedTime: Long = -1

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)


    init {

        //final LayoutInflater inflater = LayoutInflater.from(context);
        //inflater.inflate(R.layout.view_kalimba, this);
        for (i in KALIMBA_NOTES.indices) {
            noteToIndexMap[KALIMBA_NOTES[i]] = i
        }
        backgroundPaint.color = Color.WHITE
        backgroundPaint.style = Paint.Style.FILL
        targetNotePaint.color = resources.getColor(R.color.colorBlue)
        detectedNotePaint.color = resources.getColor(R.color.colorRed)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw background
        canvas.drawPaint(backgroundPaint)
        // Draw Kalimba
        calculatePositionForDrawable()
        drawable.setBounds(leftBound, topBound, rightBound, bottomBound)
        drawable.draw(canvas)

        // Draw hint
        if (currentTargetNoteIndex != -1) {
            //canvas.clipRect(leftBound, topBound, rightBound, bottomBound);
            canvas.drawCircle(xPositionArray[currentTargetNoteIndex] * kalimbaActualWidght + leftBound,
                    yPositionArray[currentTargetNoteIndex] * kalimbaActualHeight + topBound,
                    hintRadius.toFloat(),
                    targetNotePaint)
        }
        if (shouldShowDetectedNote()) {
            //canvas.clipRect(leftBound, topBound, rightBound, bottomBound);
            canvas.drawCircle(xPositionArray[currentDetectedNoteIndex] * kalimbaActualWidght + leftBound,
                    yPositionArray[currentDetectedNoteIndex] * kalimbaActualHeight + topBound,
                    hintRadius.toFloat(),
                    detectedNotePaint)
        }
    }

    private fun calculatePositionForDrawable() {
        val canvasHeight = height.toDouble()
        val canvasWidth = width.toDouble()
        val drawableHeight = drawable.intrinsicHeight.toDouble()
        val drawableWidth = drawable.intrinsicWidth.toDouble()
        val canvasRatio = canvasHeight / canvasWidth
        val drawableRation = drawableHeight / drawableWidth
        if (canvasRatio < drawableRation) {
            topBound = 0
            bottomBound = 0
            val scaledDrawableWidth = drawableWidth * (canvasHeight / drawableHeight)
            leftBound = ((canvasWidth - scaledDrawableWidth) / 2).toInt()
            rightBound = (canvasWidth - leftBound).toInt()
            hintRadius = (scaledDrawableWidth / 34).toInt()
            //Log.d("Hai", "1left" + leftBound + "topBound" + topBound + "rightBound" + rightBound + "bottomBound" + bottomBound);
            kalimbaActualHeight = canvasHeight.toFloat()
            kalimbaActualWidght = scaledDrawableWidth.toFloat()
        } else {
            leftBound = 0
            rightBound = canvasWidth.toInt()
            val scaledDrawableHeight = drawableHeight * (canvasWidth / drawableWidth)
            topBound = ((canvasHeight - scaledDrawableHeight) / 2).toInt()
            bottomBound = (canvasHeight - topBound).toInt()
            hintRadius = (canvasWidth / 34).toInt()
            //Log.d("Hai", "2left" + leftBound + "topBound" + topBound + "rightBound" + rightBound + "bottomBound" + bottomBound);
            kalimbaActualHeight = scaledDrawableHeight.toFloat()
            kalimbaActualWidght = canvasWidth.toFloat()
        }
    }

    fun setTargetNote(note: String): Boolean {
        if (noteToIndexMap.containsKey(note)) {
            currentTargetNoteIndex = noteToIndexMap[note]!!
            invalidate()
            return true
        } else {
            currentTargetNoteIndex = -1
            invalidate()
            return false
        }
    }

    fun setDetectedNote(note: String): Boolean {
        if (noteToIndexMap.containsKey(note)) {
            detectedTime = System.currentTimeMillis()
            currentDetectedNoteIndex = noteToIndexMap[note]!!
            invalidate()
            return true
        } else {
            //currentDetectedNoteIndex = -1;
            invalidate()
            return false
        }
    }

    /**
     * Check weather we should should detected note or not.
     * Based on time of detect and value of index.
     * If it has been too long or an invalid index, we will not show
     */
    private fun shouldShowDetectedNote(): Boolean {
        if (System.currentTimeMillis() - detectedTime >= DETECTED_NOTE_EXPIRATION_TIME) {
            return false
        }
        if (currentDetectedNoteIndex == -1) {
            return false
        }
        return currentDetectedNoteIndex != currentTargetNoteIndex
    }

    companion object {
        private val xPositionArray = floatArrayOf(0.505f, 0.435f, 0.568f, 0.375f, 0.624f, 0.314f, 0.68f, 0.262f, 0.74f, 0.20f, 0.80f, 0.142f, 0.85f, 0.09f, 0.91f, 0.03f, 0.97f)
        private val yPositionArray = floatArrayOf(0.95f, 0.9f, 0.85f, 0.832f, 0.786f, 0.753f, 0.723f, 0.705f, 0.668f, 0.642f, 0.63f, 0.60f, 0.58f, 0.56f, 0.54f, 0.52f, 0.50f)
        private const val DETECTED_NOTE_EXPIRATION_TIME = (2 * 1000).toLong()// 3 seconds
    }
}