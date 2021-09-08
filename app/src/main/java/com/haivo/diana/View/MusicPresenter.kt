package com.haivo.diana.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.haivo.diana.R

class MusicPresenter : View {

    private var targetNotePaint: Paint = TextPaint()
    private var detectedNotePaint: Paint = TextPaint()
    private var targetNote = ""
    private var detectedNote = ""
    private var targetNoteScaleFactor = 1f
    private var detectedNoteScaleFactor = 1f
    private var targetNoteTextX: Float = 0.toFloat()
    private var targetNoteTextY = UNDEFINED.toFloat()
    private var detectedNoteTextX: Float = 0.toFloat()
    private var detectedNoteTextY = UNDEFINED.toFloat()

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calculateTextSizeAndPosition()
        targetNotePaint.color = -0xff2201
        detectedNotePaint.color = resources.getColor(R.color.colorRed)
        canvas.drawText(targetNote, targetNoteTextX, targetNoteTextY, targetNotePaint)
        canvas.drawText(detectedNote, targetNoteTextX, 800f, detectedNotePaint)
    }

    /**
     * Divide canvas into 2 equal part vertically.
     * Inside each part, text should be in the middle and take half of the space
     */
    private fun calculateTextSizeAndPosition() {
        // Target Note Calculation
        val bounds = Rect()
        if (!TextUtils.isEmpty(targetNote)) {
            targetNotePaint.getTextBounds(targetNote, 0, targetNote.length, bounds)
            val currentTextHeight = bounds.height().toFloat()
            var currentTextWidth = bounds.width().toFloat()
            val canvasWidth = measuredWidth.toFloat()
            targetNoteScaleFactor = canvasWidth / 2 / currentTextWidth
            var newTextWidth = 0f
            var newTextHeight = 0f
            if (Math.abs(targetNoteScaleFactor - 1) > SCALING_TOLERANCE
                    || targetNoteTextX == UNDEFINED.toFloat()
                    || targetNoteTextY == UNDEFINED.toFloat()) {
                targetNotePaint.textSize = targetNotePaint.textSize * targetNoteScaleFactor
                newTextWidth = currentTextWidth * targetNoteScaleFactor
                newTextHeight = currentTextHeight * targetNoteScaleFactor
                targetNoteTextX = (measuredWidth - newTextWidth) / 2
                targetNoteTextY = measuredHeight.toFloat() / 2 - (measuredHeight.toFloat() / 2 - newTextHeight / 2) / 2
            }
            // Detected Note Calculation
            if (!TextUtils.isEmpty(detectedNote) && detectedNote != "0") {
                detectedNotePaint.getTextBounds(detectedNote, 0, detectedNote.length, bounds)
                currentTextWidth = bounds.width().toFloat()
                detectedNoteScaleFactor = canvasWidth / 2 / currentTextWidth
                if (Math.abs(detectedNoteScaleFactor - 1) > SCALING_TOLERANCE
                        || detectedNoteTextX == UNDEFINED.toFloat()
                        || detectedNoteTextY == UNDEFINED.toFloat()) {
                    detectedNotePaint.textSize = detectedNotePaint.textSize * detectedNoteScaleFactor
                    newTextWidth = currentTextWidth * detectedNoteScaleFactor
                    detectedNoteTextX = (measuredWidth - newTextWidth) / 2
                    detectedNoteTextY = measuredHeight.toFloat() - (measuredHeight.toFloat() / 2 - newTextHeight / 2) / 2
                }
            }
        }
    }

    fun setTargetNote(note: String) {
        this.targetNote = note
        invalidate()
    }

    fun setDetectedNote(note: String) {
        this.detectedNote = note
        invalidate()
    }

    companion object {
        private const val SCALING_TOLERANCE = 0.05f//To avoid changing size too much;
        private const val UNDEFINED = -1
    }
}
