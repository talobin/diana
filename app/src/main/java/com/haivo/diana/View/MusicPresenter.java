package com.haivo.diana.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.haivo.diana.R;

public class MusicPresenter extends View {
    public MusicPresenter(Context context) {
        super(context);
    }
    public MusicPresenter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public MusicPresenter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public MusicPresenter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    Paint targetNotePaint = new TextPaint();
    Paint detectedNotePaint = new TextPaint();
    private String targetNote = "";
    private String detectedNote = "";
    private final static float SCALING_TOLERANCE = 0.05f;//To avoid changing size too much;
    private final static int UNDEFINED = -1;
    private float targetNoteScaleFactor = 1;
    private float detectedNoteScaleFactor = 1;
    private float targetNoteTextX, targetNoteTextY = UNDEFINED;
    private float detectedNoteTextX, detectedNoteTextY = UNDEFINED;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateTextSizeAndPosition();
        targetNotePaint.setColor(0xff00ddff);
        detectedNotePaint.setColor(getResources().getColor(R.color.colorRed));
        canvas.drawText(targetNote, targetNoteTextX, targetNoteTextY, targetNotePaint);
        canvas.drawText(detectedNote, targetNoteTextX, 800, detectedNotePaint);
    }

    /**
     * Divide canvas into 2 equal part vertically.
     * Inside each part, text should be in the middle and take half of the space
     */
    private void calculateTextSizeAndPosition() {
        // Target Note Calculation
        Rect bounds = new Rect();
        if (!TextUtils.isEmpty(targetNote)) {
            targetNotePaint.getTextBounds(targetNote, 0, targetNote.length(), bounds);
            float currentTextHeight = bounds.height();
            float currentTextWidth = bounds.width();
            float canvasWidth = getMeasuredWidth();
            targetNoteScaleFactor = (canvasWidth / 2) / currentTextWidth;
            float newTextWidth = 0;
            float newTextHeight = 0;
            if (Math.abs(targetNoteScaleFactor - 1) > SCALING_TOLERANCE
                || targetNoteTextX == UNDEFINED
                || targetNoteTextY == UNDEFINED) {
                targetNotePaint.setTextSize(targetNotePaint.getTextSize() * targetNoteScaleFactor);
                newTextWidth = currentTextWidth * targetNoteScaleFactor;
                newTextHeight = currentTextHeight * targetNoteScaleFactor;
                targetNoteTextX = (getMeasuredWidth() - newTextWidth) / 2;
                targetNoteTextY =
                    (float) getMeasuredHeight() / 2 - ((float) getMeasuredHeight() / 2 - newTextHeight / 2) / 2;
            }
            // Detected Note Calculation
            if (!TextUtils.isEmpty(detectedNote) && !detectedNote.equals("0")) {
                detectedNotePaint.getTextBounds(detectedNote, 0, detectedNote.length(), bounds);
                currentTextWidth = bounds.width();
                detectedNoteScaleFactor = (canvasWidth / 2) / currentTextWidth;
                if (Math.abs(detectedNoteScaleFactor - 1) > SCALING_TOLERANCE
                    || detectedNoteTextX == UNDEFINED
                    || detectedNoteTextY == UNDEFINED) {
                    detectedNotePaint.setTextSize(detectedNotePaint.getTextSize() * detectedNoteScaleFactor);
                    newTextWidth = currentTextWidth * detectedNoteScaleFactor;
                    detectedNoteTextX = (getMeasuredWidth() - newTextWidth) / 2;
                    detectedNoteTextY =
                        (float) getMeasuredHeight() - ((float) getMeasuredHeight() / 2 - newTextHeight / 2) / 2;
                }
            }
        }
    }

    public void setTargetNote(String note) {
        this.targetNote = note;
        invalidate();
    }

    public void setDetectedNote(String note) {
        this.detectedNote = note;
        invalidate();
    }
}
