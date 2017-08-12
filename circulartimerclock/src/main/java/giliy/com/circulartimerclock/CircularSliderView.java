package giliy.com.circulartimerclock;

/**
 * Created by gili on 12/08/2017.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircularSliderView extends View {

    private int startGradientColor = 0;
    private int endGradientColor=0;
    private int startHourAngle;
    private float startMinutesAngle;
    private int endHourAngle;
    private float endMinutesAngle;
    private Drawable backgoundImage;
    private int largestCenteredSquareLeft;
    private int largestCenteredSquareTop;
    private int largestCenteredSquareRight;
    private int largestCenteredSquareBottom;

    public void setBackgroundImage(Drawable backgroundImage) {
        this.backgoundImage = backgroundImage;
    }

    /**
     * Listener interface used to detect when slider moves around.
     */
    public interface OnSliderRangeMovedListener {

        /**
         * This method is invoked when start thumb is moved, providing position of the start slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br>
         *            {@code pos = (Angle - StartingAngle) / (2 * Pi)}
         */
        void onStartSliderMoved(double pos, boolean isThumbSlide);

        /**
         * This method is invoked when end thumb is moved, providing position of the end slider thumb.
         *
         * @param pos Value between 0 and 1 representing the current angle.<br>
         *            {@code pos = (Angle - StartingAngle) / (2 * Pi)}
         */
        void onEndSliderMoved(double pos, boolean isThumbSlide);

        /**
         * This method is invoked when start slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        void onStartSliderEvent(ThumbEvent event);

        /**
         * This method is invoked when end slider is pressed/released.
         *
         * @param event Event represent state of the slider, it can be in two states: Pressed or Released.
         */
        void onEndSliderEvent(ThumbEvent event);
    }

    private int mThumbStartX;
    private int mThumbStartY;

    private int mThumbEndX;
    private int mThumbEndY;

    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mCircleRadius;
    private int mArcColor;
    private Drawable mStartThumbImage;
    private Drawable mEndThumbImage;
    private int mPadding;
    private int mStartThumbSize;
    private int mEndThumbSize;
    private int mStartThumbColor;
    private int mEndThumbColor;
    private int mBorderColor;
    private int mBorderThickness;
    private int mArcDashSize;
    private double mAngle;
    private double mAngleEnd;
    private boolean mIsThumbSelected = false;
    private boolean mIsThumbEndSelected = false;

    private Paint mPaint = new Paint();
    private Paint mLinePaint = new Paint();
    private RectF arcRectF = new RectF();
    private Rect arcRect = new Rect();
    private OnSliderRangeMovedListener mListener;
    private static final int THUMB_SIZE_NOT_DEFINED = -1;
    private boolean isStartTimeAM = true;
    private boolean isEndTimeAM = true;
    private LinearGradient linearGradient;

    private float prevSelectedStartAngle = 0;
    private float currentSelectedStartAngle = 0;
    private float prevSelectedEndAngle = 0;
    private float currentSelectedEndAngle = 0;
    private double currentTouchAngle = 0;
    private double prevTouchAngle = 0;

    private enum Thumb {
        START, END
    }

    public CircularSliderView(Context context) {
        this(context, null);

    }

    public CircularSliderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public float getPrevSelectedEndAngle() {
        return prevSelectedEndAngle;
    }

    public boolean isStartTimeAM() {
        return isStartTimeAM;
    }

    public void setStartTimeAM(boolean startTimeAM) {
        isStartTimeAM = startTimeAM;
    }

    public boolean isEndTimeAM() {
        return isEndTimeAM;
    }

    public void setEndTimeAM(boolean endTimeAM) {
        isEndTimeAM = endTimeAM;
    }

    // common initializer method
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0);

        // read all available attributes
        int startHour = a.getInteger(R.styleable.CircularSlider_start_hour, 0);
        int endHour = a.getInteger(R.styleable.CircularSlider_end_hour, 0);
        float startMinutes = a.getFloat(R.styleable.CircularSlider_start_minutes, 0);
        float endMinutes = a.getFloat(R.styleable.CircularSlider_end_minutes, 0);
        int thumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_thumb_size, 50);
        int startThumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_start_thumb_size, THUMB_SIZE_NOT_DEFINED);
        int endThumbSize = a.getDimensionPixelSize(R.styleable.CircularSlider_end_thumb_size, THUMB_SIZE_NOT_DEFINED);
        int thumbColor = a.getColor(R.styleable.CircularSlider_start_thumb_color, Color.GRAY);
        int thumbEndColor = a.getColor(R.styleable.CircularSlider_end_thumb_color, Color.GRAY);
        int borderThickness = a.getDimensionPixelSize(R.styleable.CircularSlider_border_thickness, 20);
        int arcDashSize = a.getDimensionPixelSize(R.styleable.CircularSlider_arc_dash_size, 60);
        int arcColor = a.getColor(R.styleable.CircularSlider_arc_color, 0);
        int startGradientColor = a.getColor(R.styleable.CircularSlider_arc_gradient_color_start, 0);
        int endGradientColor = a.getColor(R.styleable.CircularSlider_arc_gradient_color_end, 0);
        int borderColor = a.getColor(R.styleable.CircularSlider_border_color, Color.RED);
        Drawable thumbImage = a.getDrawable(R.styleable.CircularSlider_start_thumb_image);
        Drawable thumbEndImage = a.getDrawable(R.styleable.CircularSlider_end_thumb_image);
        Drawable backgroundDrawable = a.getDrawable(R.styleable.CircularSlider_clock_background_image);

        startHourAngle = hourToHourAngle(startHour);
        startMinutesAngle = minutesToMinutesAngle(startMinutes);
        endHourAngle = hourToHourAngle(endHour);
        endMinutesAngle = minutesToMinutesAngle(endMinutes);

        setStartAngle(startHourAngle + startMinutesAngle);
        setEndAngle(endHourAngle + endMinutesAngle);

        setBorderThickness(borderThickness);
        setBorderColor(borderColor);
        setThumbSize(thumbSize);
        setStartThumbSize(startThumbSize);
        setEndThumbSize(endThumbSize);
        setStartThumbImage(thumbImage);
        setEndThumbImage(thumbEndImage);
        setBackgroundImage(backgroundDrawable);
        setStartThumbColor(thumbColor);
        setArcColor(arcColor);
        setEndThumbColor(thumbEndColor);
        setArcDashSize(arcDashSize);
        setArcGradient(startGradientColor, endGradientColor);
        setDrawingCacheEnabled(true);
        // assign padding - check for version because of RTL layout compatibility
        int padding;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int all = getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop() + getPaddingEnd() + getPaddingStart();
            padding = all / 6;
        } else {
            padding = (getPaddingLeft() + getPaddingRight() + getPaddingBottom() + getPaddingTop()) / 4;
        }
        setPadding(padding);

        if (isInEditMode()) {
            return;
        }
        a.recycle();


    }

    private void setArcGradient(int startGradientColor, int endGradientColor) {
        this.startGradientColor= startGradientColor;
        this.endGradientColor = endGradientColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // use smaller dimension for calculations (depends on parent size)
        int smallerDim = w > h ? h : w;

        // find circle's rectangle points
        largestCenteredSquareLeft = (w - smallerDim) / 2;
        largestCenteredSquareTop = (h - smallerDim) / 2;
        largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        // save circle coordinates and radius in fields
        mCircleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        mCircleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        mCircleRadius = smallerDim / 2 - mBorderThickness / 2 - mPadding;

        // works well for now, should we call something else here?
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // outer circle (ring)
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderThickness);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mCircleCenterX, mCircleCenterY, mCircleRadius, mPaint);

        // find thumb start position
        mThumbStartX = (int) (mCircleCenterX + mCircleRadius * Math.cos(mAngle));
        mThumbStartY = (int) (mCircleCenterY - mCircleRadius * Math.sin(mAngle));

        //find thumb end position
        mThumbEndX = (int) (mCircleCenterX + mCircleRadius * Math.cos(mAngleEnd));
        mThumbEndY = (int) (mCircleCenterY - mCircleRadius * Math.sin(mAngleEnd));

        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(mArcDashSize);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setColor(mArcColor == 0 ? Color.RED : mArcColor);
        mLinePaint.setAntiAlias(true);

        if(linearGradient==null){
            linearGradient = new LinearGradient(0, 0, 0, getHeight(), startGradientColor, endGradientColor, Shader.TileMode.MIRROR);
            mLinePaint.setShader(linearGradient);
        }

        arcRect.set(mCircleCenterX - mCircleRadius, mCircleCenterY + mCircleRadius, mCircleCenterX + mCircleRadius, mCircleCenterY - mCircleRadius);
        arcRectF.set(arcRect);
        arcRectF.sort();

        final float drawStart = toDrawingAngle(mAngle);
        final float drawEnd = toDrawingAngle(mAngleEnd);

        int mThumbSize = getStartThumbSize();
        canvas.drawArc(arcRectF, drawStart, ((360 + drawEnd - drawStart) % 360), false, mLinePaint);
        if (mStartThumbImage != null) {
            // draw png
            mStartThumbImage.setBounds(mThumbStartX - mThumbSize / 2, mThumbStartY - mThumbSize / 2, mThumbStartX + mThumbSize / 2, mThumbStartY + mThumbSize / 2);
            mStartThumbImage.draw(canvas);
        } else {
            // draw colored circle
            mPaint.setColor(mStartThumbColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mThumbStartX, mThumbStartY, mThumbSize / 2, mPaint);
        }

        mThumbSize = getEndThumbSize();
        if (mEndThumbImage != null) {
            // draw png
            mEndThumbImage.setBounds(mThumbEndX - mThumbSize / 2, mThumbEndY - mThumbSize / 2, mThumbEndX + mThumbSize / 2, mThumbEndY + mThumbSize / 2);
            mEndThumbImage.draw(canvas);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mEndThumbColor);
            canvas.drawCircle(mThumbEndX, mThumbEndY, mThumbSize / 2, mPaint);
        }

        if (backgoundImage != null) {
            // draw png
            Bitmap bitmap = ((BitmapDrawable)backgoundImage).getBitmap();
            bitmap = getRoundedShape(bitmap);
            canvas.drawBitmap(bitmap,getBorderThickness(),getBorderThickness(),mPaint);
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = getWidth()-2*getBorderThickness();
        int targetHeight = getHeight()-2*getBorderThickness();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    /**
     * Invoked when slider starts moving or is currently moving. This method calculates and sets position and angle of the thumb.
     *
     * @param touchX Where is the touch identifier now on X axis
     * @param touchY Where is the touch identifier now on Y axis
     */
    private void updateSliderState(int touchX, int touchY, Thumb thumb) {
        int distanceX = touchX - mCircleCenterX;
        int distanceY = mCircleCenterY - touchY;
        //noinspection SuspiciousNameCombination
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        double angle = Math.acos(distanceX / c);
        if (distanceY < 0)
            angle = -angle;

        if (thumb == Thumb.START) {
            mAngle = angle;
        } else {
            mAngleEnd = angle;
        }

        if (mListener != null) {
            if (thumb == Thumb.START) {
                if(currentSelectedStartAngle != toDrawingAngle((angle))){
                    prevSelectedStartAngle = currentSelectedStartAngle;
                    currentSelectedStartAngle = toDrawingAngle(angle);
                    mListener.onStartSliderMoved(toDrawingAngle(angle),true);
                }
            } else {
                if(currentSelectedEndAngle != toDrawingAngle(angle)) {
                    prevSelectedEndAngle = currentSelectedEndAngle;
                    currentSelectedEndAngle = toDrawingAngle(angle);
                    mListener.onEndSliderMoved(currentSelectedEndAngle,true);
                }
            }
        }
    }

    public void updateSliderState(double start,double end){
        if (mListener != null) {
            mListener.onStartSliderMoved(start,true);
            mListener.onEndSliderMoved(end,true);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private float toDrawingAngle(double angleInRadians) {
        double fixedAngle = Math.toDegrees(angleInRadians) % 360;
        if (angleInRadians > 0)
            fixedAngle = 360 - fixedAngle;
        else
            fixedAngle = -fixedAngle;
        return (float) fixedAngle;
    }

    private double fromDrawingAngle(double angleInDegrees) {
        double radians = Math.toRadians(angleInDegrees);
        return -radians;
    }

    /**
     * Set slider range moved listener.
     *
     * @param listener Instance of the slider range moved listener, or null when removing it
     */
    public void setOnSliderRangeMovedListener(OnSliderRangeMovedListener listener) {
        mListener = listener;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (isFirstTouchPixelEqualZero(ev)) //if touch pixel equal 0 then do nothing
                    return false;
                // start moving the thumb (this is the first touch)
                int x = (int) ev.getX();
                int y = (int) ev.getY();

                int mThumbSize = getStartThumbSize();
                boolean isThumbStartPressed = x < mThumbStartX + mThumbSize
                        && x > mThumbStartX - mThumbSize
                        && y < mThumbStartY + mThumbSize
                        && y > mThumbStartY - mThumbSize;

                mThumbSize = getEndThumbSize();
                boolean isThumbEndPressed = x < mThumbEndX + mThumbSize
                        && x > mThumbEndX - mThumbSize
                        && y < mThumbEndY + mThumbSize
                        && y > mThumbEndY - mThumbSize;

                if (isThumbStartPressed) {
                    mIsThumbSelected = true;
                    updateSliderState(x, y, Thumb.START);
                } else if (isThumbEndPressed) {
                    mIsThumbEndSelected = true;
                    updateSliderState(x, y, Thumb.END);
                }

                if (mListener != null) {
                    if (mIsThumbSelected)
                        mListener.onStartSliderEvent(ThumbEvent.THUMB_PRESSED);
                    if (mIsThumbEndSelected)
                        mListener.onEndSliderEvent(ThumbEvent.THUMB_PRESSED);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // still moving the thumb (this is not the first touch)
                if (mIsThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.START);  //touch in start thumb
                } else if (mIsThumbEndSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y, Thumb.END);  //touch in end thumb
                }
                else{
                    double touchAngle = toDrawingAngle(getTouchAngle(ev)); // calc touch angle (0-359)
                    if (isTouchOnGreyArea((int) touchAngle))   //if touch angle isn't on green area
                        return false;

                    prevSelectedStartAngle = currentSelectedStartAngle;
                    currentSelectedStartAngle = (int)toDrawingAngle(mAngle);

                    prevSelectedEndAngle = currentSelectedEndAngle;
                    currentSelectedEndAngle = (int) toDrawingAngle(mAngleEnd);

                    //init prev and current angles
                    if(prevTouchAngle == currentTouchAngle && prevTouchAngle == 0){
                        prevTouchAngle = currentTouchAngle = touchAngle;
                    }
                    prevTouchAngle = currentTouchAngle;
                    currentTouchAngle = touchAngle;

                    //if diff > 0 than shift clockwise, otherwise shift opposite clock direction
                    double diff = currentTouchAngle - prevTouchAngle;
                    mAngle = mAngle - diff*(Math.PI/180);
                    mAngleEnd = mAngleEnd - diff*(Math.PI/180);

                    //update start and end thumbs and also time.
                    mListener.onStartSliderMoved(toDrawingAngle(mAngle),false);
                    mListener.onEndSliderMoved(toDrawingAngle(mAngleEnd),false);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mListener != null) {
                    if (mIsThumbSelected)
                        mListener.onStartSliderEvent(ThumbEvent.THUMB_RELEASED);
                    if (mIsThumbEndSelected)
                        mListener.onEndSliderEvent(ThumbEvent.THUMB_RELEASED);
                }

                // finished moving (this is the last touch)
                mIsThumbSelected = false;
                mIsThumbEndSelected = false;
                prevTouchAngle = currentTouchAngle = 0;
                break;
            }
        }

        invalidate();
        return true;
    }

    private boolean isTouchOnGreyArea(int touchAngle) {
        double prevThumbStartAngle = toDrawingAngle(mAngle);
        double prevThumbEndAngle = toDrawingAngle(mAngleEnd);
        int distance = (int)(360 + prevThumbEndAngle - prevThumbStartAngle) % 360;
        boolean isGreenTouch = false;
        for(int i = (int) prevThumbStartAngle; i< prevThumbStartAngle +distance; i++){
            if(i % 360 == touchAngle){
                isGreenTouch = true;
                break;
            }else if(i%360 == prevThumbEndAngle){
                isGreenTouch = false;
                break;
            }
        }
        return !isGreenTouch;
    }

    private boolean isFirstTouchPixelEqualZero(MotionEvent ev) {

        int x = (int)ev.getX();
        int y = (int)ev.getY();
        int width = this.getDrawingCache(true).getWidth();
        int height = this.getDrawingCache(true).getHeight();

        if(x < width && y < height && x>0 && y>0) {
            int pixel = this.getDrawingCache(true).getPixel((int) ev.getX(), (int) ev.getY());
            if(pixel == 0) {
                return true;
            }
        }
        return false;
    }

    private double getTouchAngle(MotionEvent ev) {
        int distanceX = (int) ev.getX() - mCircleCenterX;
        int distanceY = mCircleCenterY - (int) ev.getY();
        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        double touchAngle = Math.acos(distanceX / c);
        if (distanceY < 0) {
            touchAngle = -touchAngle;
        }
        return touchAngle;
    }

    public double getStartThumbAngle() {
        return toDrawingAngle(mAngle);
    }

    public double getEndThumbAngle() {
        return toDrawingAngle(mAngleEnd);
    }

    /* ***** Setters ***** */

    /**
     * Set start angle in degrees.
     * An angle of 0 degrees correspond to the geometric angle of 0 degrees (3 o'clock on a watch.)
     *
     * @param startAngle value in degrees.
     */
    public void setStartAngle(double startAngle) {
        mAngle = fromDrawingAngle(startAngle);
    }

    /**
     * Set end angle in degrees.
     * An angle of 0 degrees correspond to the geometric angle of 0 degrees (3 o'clock on a watch.)
     *
     * @param angle value in degrees.
     */
    public void setEndAngle(double angle) {
        mAngleEnd = fromDrawingAngle(angle);
    }

    public void setThumbSize(int thumbSize) {
        setStartThumbSize(thumbSize);
        setEndThumbSize(thumbSize);
    }

    public void setStartThumbSize(int thumbSize) {
        if (thumbSize == THUMB_SIZE_NOT_DEFINED)
            return;
        mStartThumbSize = thumbSize;
    }

    public void setEndThumbSize(int thumbSize) {
        if (thumbSize == THUMB_SIZE_NOT_DEFINED)
            return;
        mEndThumbSize = thumbSize;
    }

    public int getStartThumbSize() {
        return mStartThumbSize;
    }

    public int getEndThumbSize() {
        return mEndThumbSize;
    }

    public void setBorderThickness(int circleBorderThickness) {
        mBorderThickness = circleBorderThickness;
    }

    public int getBorderThickness() {
        return mBorderThickness + mPadding;
    }

    public void setBorderColor(int color) {
        mBorderColor = color;
    }

    public void setStartThumbImage(Drawable drawable) {
        mStartThumbImage = drawable;
    }

    public void setEndThumbImage(Drawable drawable) {
        mEndThumbImage = drawable;
    }

    public void setStartThumbColor(int color) {
        mStartThumbColor = color;
    }

    public void setEndThumbColor(int color) {
        mEndThumbColor = color;
    }

    public void setPadding(int padding) {
        mPadding = padding;
    }

    public float getCurrentSelectedEndAngle() {
        return currentSelectedEndAngle;
    }

    public void setArcDashSize(int value) {
        mArcDashSize = value;
    }

    public float getPrevSelectedStartAngle() {
        return prevSelectedStartAngle;
    }

    public float getCurrentSelectedStartAngle() {
        return currentSelectedStartAngle;
    }

    public void setArcColor(int color) {
        mArcColor = color;
    }

    public int hourToHourAngle(int hour){
        int hourAngle = (hour-3)*30;
        if(hourAngle <0){
            hourAngle = 360+ hourAngle;
        }
        return hourAngle;
    }

    public float minutesToMinutesAngle(float minutes){
        return minutes/2;
    }

}
