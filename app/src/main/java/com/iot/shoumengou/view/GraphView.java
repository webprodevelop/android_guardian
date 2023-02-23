package com.iot.shoumengou.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphView extends View {
    ArrayList<ItemHeartRate> valueList = new ArrayList<>();

    String[] marksVRate = {
            "", "", "", "", "40", "", "60", "", "80", "", "100", "", "120", "", "140", "", "160", "", "180", "", "200"
    };
    String[] marksVHighPressure = {
            "", "", "", "", "40", "", "", "", "80", "", "", "", "120", "", "", "", "160", "", "", "", "200"
    };
    String[] marksVTemperature = {
            "", "", "", "", "36", "", "38", "", "40", "", "42", "", "44"
    };
    int divideH = 12;   // Horizontal Dots Count
    int daysInRange = 7;
    int divideV = 14;   // Vertical Dots Count
    double limitLower = Prefs.DEFAULT_MIN_RATE;    // Heart Rate Lower Limit
    double limitUpper = Prefs.DEFAULT_MAX_RATE;   // Heart Rate Upper Limit

    double lowPressureLimitLower = Prefs.DEFAULT_MIN_RATE;
    double lowPressureLimitUpper = Prefs.DEFAULT_MAX_RATE;

    int vGap = 10;
    // Gap of values per Dot
    int hGap = 2;  // Gap of values per Dot
    int radiusDot = 3;

    int drawMask = 0;

    public GraphView(Context context) {
        super(context);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDaysInRange(int days) {
        this.daysInRange = days;
        if (days == 7) {
            divideH = 7;
        }
        else {
            divideH = 10;
        }
    }

    public void setDrawMask(int drawMask) {
        this.drawMask = drawMask;
    }

    public void setRateData(ArrayList<ItemHeartRate> rateData) {
        valueList = rateData;

        invalidate();
    }

    public void setHeartRateRange(double low, double high, double lpLow, double lpHigh) {
        limitLower = low;
        limitUpper = high;

        lowPressureLimitLower = lpLow;
        lowPressureLimitUpper = lpHigh;

        invalidate();
    }

    private float getDrawValue(int index, boolean isHighPressure) {
        float result = 0.0f;

        Date startDate = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(startDate);
        cl.add(Calendar.DAY_OF_YEAR, (index - divideH)  * (daysInRange == 7 ? 1 : daysInRange / 10));
        if (daysInRange != 7) {
            if (daysInRange / 2 > 0) {
                cl.add(Calendar.DAY_OF_YEAR, -daysInRange / 20);

                if (daysInRange % 2 != 0) {
                    cl.add(Calendar.HOUR, -12);
                }
            }
        }
        startDate = cl.getTime();

        Date endDate = new Date();
        cl = Calendar.getInstance();
        cl.setTime(endDate);
        cl.add(Calendar.DAY_OF_YEAR, (index - divideH)  * (daysInRange == 7 ? 1 : daysInRange / 10));
        if (daysInRange != 7) {
            if (daysInRange / 2 > 0) {
                cl.add(Calendar.DAY_OF_YEAR, daysInRange / 20);

                if (daysInRange % 2 != 0) {
                    cl.add(Calendar.HOUR, 12);
                }
            }
        }
        endDate = cl.getTime();

        String dateString = Util.getDateFormatStringIgnoreLocale(startDate);

        int count = 0;
        for(int i = 0; i < valueList.size(); i++) {
            if ((daysInRange == 7 && valueList.get(i).checkDate.equals(dateString)) ||
                    (daysInRange != 7 && startDate.before(new Date(valueList.get(i).checkTime)) && endDate.after(new Date(valueList.get(i).checkTime)))) {
                if (drawMask == 0) {
                    result += valueList.get(i).heartRate;
                }
                else if (drawMask == 1) {
                    if (isHighPressure)
                        result += valueList.get(i).highBloodPressure;
                    else
                        result += valueList.get(i).lowBloodPressure;
                }
                else if (drawMask == 2) {
                    result += (float)valueList.get(i).temperature;
                }

                count += 1;
            }
        }

        if (count > 0) {
            return result / count;
        }
        else {
            return -1.0f;
        }
    }

    @SuppressLint({"DefaultLocale", "DrawAllocation"})
    @Override
    public void onDraw(Canvas canvas) {
        divideV = 14;
        if (drawMask == 1) {
            divideV = 20;
        }
        else if (drawMask == 2) {
            divideV = 12;
        }
        Rect drawingRect = new Rect();
        getDrawingRect(drawingRect);

        int axisL = drawingRect.left + 60;
        int axisT = drawingRect.top + 40;
        int axisR = drawingRect.right - 60;
        int axisB = drawingRect.bottom - 40;
        int axisW = axisR - axisL;  // Width
        int axisH = axisB - axisT;  // Height
        int unitH = axisW /divideH; // Horizontal
        int unitV = axisH /divideV; // Vertical

        int x;
        int y;
        int count;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(20);
        paint.setStrokeWidth(2);

        //-- Draw Axis
        paint.setColor(getContext().getColor(R.color.color_tab_selected));
        canvas.drawLine(axisL, axisT, axisL, axisB, paint);
        canvas.drawLine(axisL, axisB, axisR, axisB, paint);

        //-- Horizontal Dots
        for (int i = 1; i <= divideH; i++) {
            x = axisL + i * unitH;
            drawDot(canvas, x, axisB, paint, true);
        }

        //-- Vertical Dots
        for (int i = 1; i <= divideV; i++) {
            y = axisB - i * unitV;
            if (divideV != 20 || i % 2 == 0) {
                drawDot(canvas, axisL, y, paint, true);
            }
        }

        paint.setColor(Color.BLACK);
        //-- Horizontal Marks
        for (int i = 1; i < divideH + 1; i++) {
            if (daysInRange != 7 && i % 2 != 1) {
                continue;
            }
            x = axisL + unitH * i;
            y = axisB;

            Date currentDate = new Date();
            Calendar cl = Calendar.getInstance();
            cl.setTime(currentDate);
            cl.add(Calendar.DAY_OF_YEAR, (i - divideH) * (daysInRange == 7 ? 1 : daysInRange / 10));
            currentDate = cl.getTime();

            String dayString = Util.getDayString(currentDate);

            @SuppressLint("DrawAllocation") Rect textBound = new Rect();
            paint.getTextBounds(dayString, 0, dayString.length(), textBound);
            canvas.drawText(dayString, x - textBound.width() / 2, y + textBound.height() + 10, paint);
        }

        //-- Veritical Marks
        String [] drawMaskV = marksVRate;
        if (drawMask == 1) {
            drawMaskV = marksVHighPressure;
        }
        else if (drawMask == 2) {
            drawMaskV = marksVTemperature;
        }
        count = Math.min(drawMaskV.length, (divideV + 1));
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (drawMaskV[i].isEmpty())    continue;
                x = axisL - 25;
                y = axisB - unitV * i - 10;

                Rect textBound = new Rect();
                paint.getTextBounds(drawMaskV[i], 0, drawMaskV[i].length(), textBound);
                canvas.drawText(drawMaskV[i], x - textBound.width() / 2, y + textBound.height(), paint);
            }
        }

//        //-- Lower Limit Line
//        if (drawMask != 1) {
//            paint.setColor(getContext().getColor(android.R.color.darker_gray));
//            x = axisL;
//            if (drawMask == 2) {
//                if (limitLower < 36) {
//                    vGap = 9;
//                    y = axisB - (int) (limitLower * unitV / vGap);
//                } else {
//                    vGap = 1;
//                    y = axisB - (4 * unitV / vGap) - (int) ((limitLower - 36) * unitV);
//                }
//            } else {
//                vGap = 10;
//                y = axisB - (int) (limitLower * unitV / vGap);
//            }
//            while (x <= axisR) {
//                canvas.drawLine(x, y, x + 3, y, paint);
//                x = x + 6;
//            }
//        }
//
//        //-- Upper Limit Line
//        if (drawMask != 1) {
//            x = axisL;
//            if (drawMask == 2) {
//                if (limitUpper < 36) {
//                    vGap = 9;
//                    y = axisB - (int) (limitUpper * unitV / vGap);
//                } else {
//                    vGap = 1;
//                    y = axisB - (4 * unitV / vGap) - (int) ((limitUpper - 36) * unitV);
//                }
//            } else {
//                vGap = 10;
//                y = axisB - (int) (limitUpper * unitV / vGap);
//            }
//            while (x <= axisR) {
//                canvas.drawLine(x, y, x + 3, y, paint);
//                x = x + 6;
//            }
//        }

        if (valueList == null || valueList.isEmpty()) {
            drawEmptyText(canvas, paint);
            return;
        }

        //-- Graph
        paint.setColor(getContext().getColor(R.color.color_tab_selected));

        count = valueList.size();
        if (count > 0) {
            canvas.save();

            paint.setStyle(Paint.Style.STROKE);
            Path path = new Path();

            int startIndex = 1;
            double baseValue = getDrawValue(startIndex, true);
            while(baseValue <= 0.0f && startIndex < count) {
                startIndex += 1;
                baseValue = getDrawValue(startIndex, true);
            }

            x = axisL + unitH * startIndex;
            if (drawMask == 2) {
                if (baseValue < 36) {
                    vGap = 9;
                    y = axisB - (int)(baseValue * unitV / vGap);
                }
                else {
                    vGap = 1;
                    y = axisB - (4 * unitV / vGap) - (int)((baseValue - 36) * unitV);
                }
            }
            else {
                vGap = 10;
                y = axisB - (int)(baseValue * unitV / vGap);
            }
            path.moveTo(x, y);
            drawDot(canvas, x, y, paint, false);
//            @SuppressLint("DefaultLocale") String strRate;
//            if (drawMask == 2) {
//                strRate = String.format("%.1f", baseValue);
//            } else {
//                strRate = String.format("%d", (int)Math.round(baseValue));
//            }
//            @SuppressLint("DrawAllocation") Rect textBound = new Rect();
//            paint.getTextBounds(strRate, 0, strRate.length(), textBound);
//            @SuppressLint("DrawAllocation") RectF rectRate = new RectF(x - textBound.width() / 2 - 5, y - textBound.height() - 20, x + textBound.width() / 2 + 5, y - 10);
//            if (baseValue < limitLower) {
//                rectRate.offset(0, textBound.height() + 30);
//            }
//            if (baseValue < limitLower || baseValue > limitUpper) {
//                drawAlertRect(canvas, rectRate, Color.RED, paint);
//                drawRateText(canvas, strRate, Color.WHITE, rectRate, paint);
//            }
//            else {
//                drawRateText(canvas, strRate, getContext().getColor(R.color.color_green), rectRate, paint);
//            }

            for (int i = startIndex + 1; i < divideH + 1; i++) {
                baseValue = getDrawValue(i, true);
                x = axisL + unitH * i;
                if (drawMask == 2) {
                    if (baseValue < 36) {
                        vGap = 9;
                        y = axisB - (int)(baseValue * unitV / vGap);
                    }
                    else {
                        vGap = 1;
                        y = axisB - (4 * unitV / vGap) - (int)((baseValue - 36) * unitV);
                    }
                }
                else {
                    vGap = 10;
                    y = axisB - (int)(baseValue * unitV / vGap);
                }

                if (baseValue < 0)  continue;
                path.lineTo(x, y);

                drawDot(canvas, x, y, paint, false);

//                if (drawMask == 2) {
//                    strRate = String.format("%.1f", baseValue);
//                } else {
//                    strRate = String.format("%d", (int)Math.round(baseValue));
//                }
//                textBound = new Rect();
//                paint.getTextBounds(strRate, 0, strRate.length(), textBound);
//                rectRate = new RectF(x - textBound.width() / 2 - 5, y - textBound.height() - 20, x + textBound.width() / 2 + 5, y - 10);
//                if (baseValue < limitLower) {
//                    rectRate.offset(0, textBound.height() + 30);
//                }
//                if (baseValue < limitLower || baseValue > limitUpper) {
//                    drawAlertRect(canvas, rectRate, Color.RED, paint);
//                    drawRateText(canvas, strRate, Color.WHITE, rectRate, paint);
//                }
//                else {
//                    drawRateText(canvas, strRate, getContext().getColor(R.color.color_green), rectRate, paint);
//                }
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(drawMask == 1 ? Color.MAGENTA : getContext().getColor(R.color.color_green));
            canvas.drawPath(path, paint);

            canvas.restore();
        }

        if (drawMask == 1) {
            if (count > 0) {
                canvas.save();

                paint.setStyle(Paint.Style.STROKE);
                Path path = new Path();

                int startIndex = 1;
                double baseValue = getDrawValue(startIndex, false);
                while(baseValue <= 0.0f && startIndex < count) {
                    startIndex += 1;
                    baseValue = getDrawValue(startIndex, false);
                }

                x = axisL + unitH * startIndex;

                vGap = 10;
                y = axisB - (int)(baseValue * unitV / vGap);

                path.moveTo(x, y);
                drawDot(canvas, x, y, paint, false);
//                @SuppressLint("DefaultLocale")
//                String strRate = String.format("%d", (int)Math.round(baseValue));
//                @SuppressLint("DrawAllocation") Rect textBound = new Rect();
//                paint.getTextBounds(strRate, 0, strRate.length(), textBound);
//                @SuppressLint("DrawAllocation") RectF rectRate = new RectF(x - textBound.width() / 2 - 5, y - textBound.height() - 20, x + textBound.width() / 2 + 5, y - 10);
//                if (baseValue < lowPressureLimitLower) {
//                    rectRate.offset(0, textBound.height() + 30);
//                }
//                if (baseValue < lowPressureLimitLower || baseValue > lowPressureLimitUpper) {
//                    drawAlertRect(canvas, rectRate, Color.RED, paint);
//                    drawRateText(canvas, strRate, Color.WHITE, rectRate, paint);
//                }
//                else {
//                    drawRateText(canvas, strRate, getContext().getColor(R.color.color_green), rectRate, paint);
//                }

                for (int i = startIndex + 1; i < divideH + 1; i++) {
                    baseValue = getDrawValue(i, false);
                    x = axisL + unitH * i;
                    vGap = 10;
                    y = axisB - (int)(baseValue * unitV / vGap);

                    if (baseValue < 0)  continue;
                    path.lineTo(x, y);

                    drawDot(canvas, x, y, paint, false);

//                    strRate = String.format("%d", (int)Math.round(baseValue));
//
//                    textBound = new Rect();
//                    paint.getTextBounds(strRate, 0, strRate.length(), textBound);
//                    rectRate = new RectF(x - textBound.width() / 2 - 5, y - textBound.height() - 20, x + textBound.width() / 2 + 5, y - 10);
//                    if (baseValue < lowPressureLimitLower) {
//                        rectRate.offset(0, textBound.height() + 30);
//                    }
//                    if (baseValue < lowPressureLimitLower || baseValue > lowPressureLimitUpper) {
//                        drawAlertRect(canvas, rectRate, Color.RED, paint);
//                        drawRateText(canvas, strRate, Color.WHITE, rectRate, paint);
//                    }
//                    else {
//                        drawRateText(canvas, strRate, getContext().getColor(R.color.color_green), rectRate, paint);
//                    }
                }

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getContext().getColor(R.color.color_red));
                canvas.drawPath(path, paint);

                canvas.restore();
            }

        }
    }

    private void drawDot(Canvas canvas, int x, int y, Paint paint, boolean isAxis) {
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        if (isAxis) {
            paint.setColor(getContext().getColor(R.color.color_green));
        }
        else {
            paint.setColor(drawMask == 1 ? Color.MAGENTA : getContext().getColor(R.color.color_green));
        }
        canvas.drawCircle(x, y, radiusDot, paint);
        canvas.restore();
    }


    private void drawAlertRect(Canvas canvas, RectF rect, int color, Paint paint) {
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRoundRect(rect, 2, 2, paint);
        canvas.restore();
    }


    private void drawRateText(Canvas canvas, String strRate, int color, RectF rect, Paint paint) {
        canvas.save();
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(color);
        canvas.drawText(strRate, rect.left + 5, rect.bottom - 5, paint);
        canvas.restore();
    }

    private void drawEmptyText(Canvas canvas, Paint paint) {
        canvas.save();
        paint.setStrokeWidth(1);
        paint.setTextSize(24);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(getContext().getColor(R.color.color_border));

        String strEmpty = getContext().getString(R.string.str_no_database);
        Rect textBound = new Rect();
        paint.getTextBounds(strEmpty, 0, strEmpty.length(), textBound);

        canvas.drawText(strEmpty, (getWidth() - textBound.width()) / 2, getHeight() / 2, paint);
        canvas.restore();
    }
}
