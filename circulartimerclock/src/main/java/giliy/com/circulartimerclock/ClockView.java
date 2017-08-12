package giliy.com.circulartimerclock;

/**
 * Created by gili on 12/08/2017.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class ClockView extends View {

    private int height, width = 0;
    private int padding = 0;
    private int radius = 0;
    private Paint paint;
    private boolean isInit;
    private int[] numbers = {1,2,3,4,5,6,7,8,9,10,11,12};
    private Rect rect = new Rect();
    double cX = getX()+getWidth()/2;

    double cY = getY()+getHeight()/2;
    private int tickColor;
    private int hourColor;
    private int fontSize = 0;
    private int tickInterval;


    public ClockView(Context context) {
        this(context,null);

    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initProperties(context, attrs, defStyleAttr);
    }

    private void initProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0);
        int tickColor = a.getColor(R.styleable.CircularSlider_clock_tick_color, getResources().getColor(R.color.dialer_clock));
        int tickInterval = a.getInteger(R.styleable.CircularSlider_clock_tick_interval, 5);
        int hourColor = a.getColor(R.styleable.CircularSlider_clock_hour_color, getResources().getColor(R.color.dialer_clock));
        int fontSize = a.getDimensionPixelSize(R.styleable.CircularSlider_hours_size, 10);

        setTickInterval(tickInterval);
        setTickColor(tickColor);
        setHourColor(hourColor);
        setFontSize(fontSize);
        a.recycle();
    }

    private void initClock() {
        height = getHeight();
        width = getWidth();
        cX = width/2;
        cY = height/2;
        int numeralSpacing = 0;
        padding = numeralSpacing + 60;
        int min = Math.min(height, width);
        radius = min / 2 - padding;
        paint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initClock();
        }
        canvas.drawColor(getResources().getColor(R.color.transparent));
        drawNumeral(canvas);
        drawTickMarks(canvas);

        super.onDraw(canvas);
    }

    private void drawTickMarks(Canvas canvas) {

        paint.reset();
        paint.setColor(tickColor);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        for (float i = 0; i < 360; i += 0.5*tickInterval) {
            float angle = (float) Math.toRadians(i); // Need to convert to radians first

            float startX = (float) (cX + (radius+padding) * Math.sin(angle));
            float startY = (float) (cY - (radius+padding) * Math.cos(angle));
            int pad = 35;
            if(i%30 != 0){
                pad = 50;
            }

            float stopX = (float) (cX + ((radius)+pad) * Math.sin(angle));
            float stopY = (float) (cY - ((radius)+pad) * Math.cos(angle));

            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

    }


    private void drawNumeral(Canvas canvas) {
        paint.reset();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setColor(hourColor);
        paint.setTextSize(fontSize);

        for (int number : numbers) {
            String tmp = String.valueOf(number);
            paint.getTextBounds(tmp, 0, tmp.length(), rect);
            double angle = Math.PI / 6 * (number - 3);
            int x = (int) (width / 2 + Math.cos(angle) * radius - rect.width() / 2);
            int y = (int) (height / 2 + Math.sin(angle) * radius + rect.height() / 2);
            canvas.drawText(tmp, x, y, paint);
        }
    }


    public void setTickColor(int tickColor) {
        this.tickColor = tickColor;
    }

    public void setHourColor(int hourColor) {
        this.hourColor = hourColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }
}