package com.haivo.diana.Model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
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

    Paint textPaint = new TextPaint();
    private String targetNote = "";
    private String detectedNote = "";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textPaint.setTextSize(500);
        textPaint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawText(targetNote, 0, getHeight() / 4, textPaint);
        canvas.drawText(detectedNote, 0, getHeight() / 2, textPaint);
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
