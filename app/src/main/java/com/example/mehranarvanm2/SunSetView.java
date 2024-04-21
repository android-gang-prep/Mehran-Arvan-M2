package com.example.mehranarvanm2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SunSetView extends View {

    Path path;
    Path visible;
    Paint white;
    Paint visiblePaint;
    float progress = 0f;
    float[] pos = new float[2];
    private PathMeasure pathMeasure;
    private Path start;
    private Path end;

    public SunSetView(Context context) {
        super(context);
        init();
    }

    public SunSetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SunSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SunSetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private void init() {
        white = new Paint();
        white.setStyle(Paint.Style.STROKE);
        white.setColor(Color.WHITE);
        white.setStrokeWidth(dpToPx(5));
        visible = new Path();

        visiblePaint = new Paint();
        visiblePaint.setStyle(Paint.Style.STROKE);
        visiblePaint.setStrokeWidth(dpToPx(5));


    }

    private void path() {
        path = new Path();
        path.moveTo(dpToPx(20), getHeight() - dpToPx(40));
        path.cubicTo(dpToPx(20), getHeight() - dpToPx(40), (getWidth() - dpToPx(20)) / 2f, dpToPx(20), getWidth() - dpToPx(20), getHeight() - dpToPx(40));

        pathMeasure = new PathMeasure(path, false);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (path == null) path();

        canvas.drawPath(path, white);

        visible.reset();
        pathMeasure.getSegment(0, pathMeasure.getLength() * progress, visible, true);
        visiblePaint.setShader(new LinearGradient(0, 0, getWidth(), 0, Color.parseColor("#ffdc39"), Color.parseColor("#fe6d01"), Shader.TileMode.CLAMP));

        canvas.drawPath(visible, visiblePaint);
        pathMeasure.getPosTan(pathMeasure.getLength() * progress, pos, new float[2]);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.sunn);


        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) dpToPx(30), (int) dpToPx(30), false), pos[0]-dpToPx(15), pos[1]-dpToPx(15), white);



    }
}
