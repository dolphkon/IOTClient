package com.jn.chart.renderer.scatter;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.jn.chart.buffer.ScatterBuffer;
import com.jn.chart.interfaces.datasets.IScatterDataSet;
import com.jn.chart.utils.Utils;
import com.jn.chart.utils.ViewPortHandler;

/**
 * Created by wajdic on 15/06/2016.
 * Created at Time 09:08
 */
public class ChevronDownShapeRenderer implements ShapeRenderer {


    @Override
    public void renderShape(
            Canvas c, IScatterDataSet dataSet,
            ViewPortHandler mViewPortHandler, ScatterBuffer buffer, Paint mRenderPaint, final float shapeSize) {

        final float shapeHalf = shapeSize / 2f;

        mRenderPaint.setStyle(Paint.Style.STROKE);
        mRenderPaint.setStrokeWidth(Utils.convertDpToPixel(1f));

        for (int i = 0; i < buffer.size(); i += 2) {

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[i]))
                break;

            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[i])
                    || !mViewPortHandler.isInBoundsY(buffer.buffer[i + 1]))
                continue;

            mRenderPaint.setColor(dataSet.getColor(i / 2));

            c.drawLine(
                    buffer.buffer[i],
                    buffer.buffer[i + 1] + (2 * shapeHalf),
                    buffer.buffer[i] + (2 * shapeHalf),
                    buffer.buffer[i + 1],
                    mRenderPaint);

            c.drawLine(
                    buffer.buffer[i],
                    buffer.buffer[i + 1] + (2 * shapeHalf),
                    buffer.buffer[i] - (2 * shapeHalf),
                    buffer.buffer[i + 1],
                    mRenderPaint);
        }

    }
}
