package com.fengda.app.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;


public class RadiusBackgroundSpan extends ReplacementSpan {

    private int mSize;
    private int mColor;
    private int mRadius;
    private int mTextColor;
    private int height;
    private int paddingLF;
    /**
     * @param color        背景颜色
     * @param textColor    文字颜色
     * @param radius       圆角半径
     * @param baseTextSize baseTextSize
     * @param padding 文字左右间距
     */
    public RadiusBackgroundSpan(int color, int textColor, int radius, int baseTextSize,int padding) {
        mColor = color;
        mRadius = radius;
        mTextColor = textColor;
        height = baseTextSize;
        paddingLF = padding;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mSize = (int) (paint.measureText(text, start, end) + 2 * mRadius+paddingLF);
        //mSize就是span的宽度
        return mSize;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {

        paint.setColor(mColor);//设置背景颜色
        paint.setAntiAlias(true);// 设置画笔的锯齿效果

        float descent = paint.descent();
        float ascent = paint.ascent();
        float centHeight = paint.descent() - paint.ascent();

        float diffHeight = height - centHeight;  //这个是后面的字体的大小的高度 -  这个画上的字体的大小的高度  这个地方要设置圆角居中显示
        //left  top  right  bottom    y + paint.ascent()  (float) (y + (paint.descent() * mScale))-7
        RectF oval = new RectF(x, y + paint.descent()-height-5, x + mSize, y + paint.descent()+5);
        //设置文字背景矩形，x为span其实左上角相对整个TextView的x值，y为span左上角相对整个View的y值。paint.ascent()获得文字上边缘，paint.descent()获得文字下边缘
        canvas.drawRoundRect(oval, mRadius, mRadius, paint);//绘制圆角矩形，第二个参数是x半径，第三个参数是y半径
        paint.setColor(mTextColor);//恢复画笔的文字颜色

        canvas.drawText(text, start, end, x + mRadius + paddingLF/2, y - diffHeight / 2, paint);//绘制文字
    }
}