package com.example.mehranarvanm2;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import java.util.Arrays;

public class CloudView extends View {
    Path path = new Path();
    PathMeasure pathMeasure = new PathMeasure();
    private Paint white;
    private Path cloud;
    private Paint paintCloud;
    private float trans = 0f;
    private float[] ds = new float[2];

    public CloudView(Context context) {
        super(context);
        init();
    }

    public CloudView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CloudView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CloudView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private float dpToPx(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }

    private void init() {
        white = new Paint();
        white.setColor(Color.WHITE);
        white.setStrokeWidth(dpToPx(2));
        white.setStyle(Paint.Style.STROKE);


        paintCloud = new Paint();
        paintCloud.setShader(new LinearGradient(0, 0, 0, 0, Color.parseColor("#FFDCEAFD"), Color.parseColor("#FFDAE7FC"), Shader.TileMode.MIRROR));

        cloud = PathParser.createPathFromPathData("M408.4,343H111.46C82.66,342.98 54.98,331.81 34.22,311.83C13.46,291.85 1.23,264.61 0.09,235.8C-1.05,207.01 8.98,178.88 28.1,157.32C47.21,135.76 73.92,122.43 102.64,120.13C106.73,91.19 119.79,64.28 139.97,43.16C160.15,22.04 186.44,7.78 215.14,2.4C243.84,-2.99 273.51,0.76 299.97,13.12C326.43,25.48 348.35,45.84 362.64,71.31C388.86,62.23 417.2,61.17 444.02,68.26C470.85,75.36 494.96,90.29 513.27,111.15C531.58,132.01 543.27,157.86 546.84,185.39C550.41,212.93 545.7,240.9 533.32,265.75C526.05,288.2 511.86,307.77 492.79,321.64C473.72,335.52 450.74,343 427.16,343H408.4Z");

        trans = 0f;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setDuration(4000);
        valueAnimator.addUpdateListener(animation -> {
            trans = (float) animation.getAnimatedValue();
            invalidate();
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        float radius = (Math.min(getWidth(), getHeight()) / 2) * 0.3f;
        float cx = getWidth() * 0.97f;
        float cy = getHeight() * 0.3f;
        canvas.drawCircle(cx, cy, radius, white);
        radius *= 2.5f;
        canvas.drawCircle(cx, cy, radius, white);
        radius *= 1.7f;
        canvas.drawCircle(cx, cy, radius, white);
        radius *= 1.6f;
        path.reset();
        path.addCircle(cx, cy, radius, Path.Direction.CW);
        pathMeasure.setPath(path, false);
        canvas.drawPath(path, white);
        RectF bounds = new RectF();
        RectF bounds2 = new RectF();
        cloud.computeBounds(bounds, true);
        path.computeBounds(bounds2, true);

        pathMeasure.getPosTan(pathMeasure.getLength() * trans, ds, new float[2]);
        Matrix matrix = new Matrix();
        matrix.setTranslate(ds[0]-(bounds.centerX()), ds[1]-(bounds.centerY()));
        cloud.transform(matrix);
        canvas.drawPath(cloud, paintCloud);

    }
}
