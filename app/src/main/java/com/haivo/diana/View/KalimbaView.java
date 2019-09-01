package com.haivo.diana.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.haivo.diana.R;
import java.util.HashMap;
import java.util.Map;

import static com.haivo.diana.Model.BaseNote.KALIMBA_NOTES;

public class KalimbaView extends View {

    private Paint targetNotePaint = new TextPaint();
    private Paint detectedNotePaint = new TextPaint();
    private Paint backgroundPaint = new Paint();
    Drawable drawable = null;
    private int left, top, right, bottom, hintRadius;
    private float kalimbaActualHeight, kalimbaActualWidght;
    private static float[] xPositionArray = new float[] {
        0.505f,
        0.435f,
        0.568f,
        0.375f,
        0.624f,
        0.314f,
        0.68f,
        0.262f,
        0.74f,
        0.20f,
        0.80f,
        0.142f,
        0.85f,
        0.09f,
        0.91f,
        0.03f,
        0.97f
    };
    private static float[] yPositionArray = new float[] {
        0.95f,
        0.9f,
        0.85f,
        0.832f,
        0.786f,
        0.753f,
        0.723f,
        0.705f,
        0.668f,
        0.642f,
        0.63f,
        0.60f,
        0.58f,
        0.56f,
        0.54f,
        0.52f,
        0.50f
    };

    Map<String, Integer> noteToIndexMap;
    private int currentTargetNoteIndex = -1;
    private int currentDetectedNoteIndex = -1;
    private long detectedTime = -1;
    private static final long DETECTED_NOTE_EXPIRATION_TIME = 2 * 1000;// 3 seconds

    public KalimbaView(@NonNull Context context) {
        this(context, null);
    }
    public KalimbaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }
    public KalimbaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, null, 0, 0);
    }
    public KalimbaView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        //final LayoutInflater inflater = LayoutInflater.from(context);
        //inflater.inflate(R.layout.view_kalimba, this);
        noteToIndexMap = new HashMap<>();
        for (int i = 0; i < KALIMBA_NOTES.length; i++) {
            noteToIndexMap.put(KALIMBA_NOTES[i], i);
        }
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);
        targetNotePaint.setColor(getResources().getColor(R.color.colorBlue));
        detectedNotePaint.setColor(getResources().getColor(R.color.colorRed));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw background
        canvas.drawPaint(backgroundPaint);
        // Draw Kalimba
        if (drawable == null) {
            drawable = getResources().getDrawable(R.drawable.kalimba);
        }
        calculatePositionForDrawable();
        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);

        // Draw hint
        if (currentTargetNoteIndex != -1) {
            //canvas.clipRect(left, top, right, bottom);
            canvas.drawCircle(xPositionArray[currentTargetNoteIndex] * kalimbaActualWidght + left,
                              yPositionArray[currentTargetNoteIndex] * kalimbaActualHeight + top,
                              hintRadius,
                              targetNotePaint);
        }
        if (shouldShowDetectedNote()) {
            //canvas.clipRect(left, top, right, bottom);
            canvas.drawCircle(xPositionArray[currentDetectedNoteIndex] * kalimbaActualWidght + left,
                              yPositionArray[currentDetectedNoteIndex] * kalimbaActualHeight + top,
                              hintRadius,
                              detectedNotePaint);
        }
    }

    private void calculatePositionForDrawable() {
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double drawableHeight = drawable.getIntrinsicHeight();
        double drawableWidth = drawable.getIntrinsicWidth();
        double canvasRatio = canvasHeight / canvasWidth;
        double drawableRation = drawableHeight / drawableWidth;
        if (canvasRatio < drawableRation) {
            top = 0;
            bottom = 0;
            double scaledDrawableWidth = drawableWidth * (canvasHeight / drawableHeight);
            left = (int) ((canvasWidth - scaledDrawableWidth) / 2);
            right = (int) (canvasWidth - left);
            hintRadius = (int) (scaledDrawableWidth / 34);
            //Log.d("Hai", "1left" + left + "top" + top + "right" + right + "bottom" + bottom);
            kalimbaActualHeight = (float) canvasHeight;
            kalimbaActualWidght = (float) scaledDrawableWidth;
        } else {
            left = 0;
            right = (int) canvasWidth;
            double scaledDrawableHeight = drawableHeight * (canvasWidth / drawableWidth);
            top = (int) ((canvasHeight - scaledDrawableHeight) / 2);
            bottom = (int) (canvasHeight - top);
            hintRadius = (int) (canvasWidth / 34);
            //Log.d("Hai", "2left" + left + "top" + top + "right" + right + "bottom" + bottom);
            kalimbaActualHeight = (float) scaledDrawableHeight;
            kalimbaActualWidght = (float) canvasWidth;
        }
    }

    public boolean setTargetNote(String note) {
        if (noteToIndexMap.containsKey(note)) {
            currentTargetNoteIndex = noteToIndexMap.get(note);
            invalidate();
            return true;
        } else {
            currentTargetNoteIndex = -1;
            invalidate();
            return false;
        }
    }

    public boolean setDetectedNote(String note) {
        if (noteToIndexMap.containsKey(note)) {
            detectedTime = System.currentTimeMillis();
            currentDetectedNoteIndex = noteToIndexMap.get(note);
            invalidate();
            return true;
        } else {
            //currentDetectedNoteIndex = -1;
            invalidate();
            return false;
        }
    }

    /**
     * Check weather we should should detected note or not.
     * Based on time of detect and value of index.
     * If it has been too long or an invalid index, we will not show
     */
    private boolean shouldShowDetectedNote() {
        if ((System.currentTimeMillis() - detectedTime) >= DETECTED_NOTE_EXPIRATION_TIME) {
            return false;
        }
        if (currentDetectedNoteIndex == -1) {
            return false;
        }
        if (currentDetectedNoteIndex == currentTargetNoteIndex) {
            return false;
        }
        return true;
    }
}
