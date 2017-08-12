package giliy.com.circulartimerclock;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.Locale;

public class CircularTimerClock extends FrameLayout {

    private static final int INTERVAL_1M = 1;
    private static final String AM = " AM";
    private static final String PM = " PM";

    public interface ontTimeChanged{
        void onStartTimeChange(String time,int hour, int minutes, boolean isAM);
        void onEndTimeChange(String time,int hour, int minutes,boolean isAM);
    }

    private int interval = 1;
    private boolean isStartTImeAM;
    private boolean isEndTimeAM;
    private boolean isClockInside = true;
    private CircularSliderView circularSliderView;
    private ClockView clockView;
    private StringBuilder startTimeSB = new StringBuilder();
    private StringBuilder endTimeSB = new StringBuilder();
    private Locale locale = new Locale("en_US");
    private FrameLayout circularSliderWrapper;
    private int startMinutes ;
    private int startHour;
    private int endMinutes;
    private int endHour;
    private ontTimeChanged ontTimeChangedListener;

    public CircularTimerClock(@NonNull Context context) {
        this(context, null);
    }

    public CircularTimerClock(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularTimerClock(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, final AttributeSet attrs, final int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularSlider, defStyleAttr, 0);
        isStartTImeAM = a.getBoolean(R.styleable.CircularSlider_start_time_is_am,true);
        isEndTimeAM = a.getBoolean(R.styleable.CircularSlider_end_time_is_am,true);
        isClockInside = a.getBoolean(R.styleable.CircularSlider_is_clock_inside,true);
        interval = a.getInteger(R.styleable.CircularSlider_clock_time_interval,INTERVAL_1M);
        a.recycle();
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.circular_timer_clock_layout, this);
        circularSliderWrapper = layout.findViewById(R.id.circular_slider_wrapper);
        circularSliderView = new CircularSliderView(getContext(), attrs, defStyleAttr);
        circularSliderWrapper.addView(circularSliderView);
        clockView = new ClockView(getContext(), attrs, defStyleAttr);

        circularSliderView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LayoutParams lp = new LayoutParams(isClockInside ?
                        circularSliderView.getWidth() - 2* circularSliderView.getBorderThickness() :
                        circularSliderView.getWidth(),
                        isClockInside ? circularSliderView.getHeight()- 2* circularSliderView.getBorderThickness() :
                                circularSliderView.getHeight());
                lp.gravity= Gravity.CENTER;
                clockView.setLayoutParams(lp);

                circularSliderWrapper.addView(clockView);
                circularSliderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        circularSliderView.setStartTimeAM(isStartTImeAM);
        circularSliderView.setEndTimeAM(isEndTimeAM);
        circularSliderView.updateSliderState(circularSliderView.getStartThumbAngle(), circularSliderView.getEndThumbAngle());

        circularSliderView.setOnSliderRangeMovedListener(new CircularSliderView.OnSliderRangeMovedListener() {
            @Override
            public void onStartSliderMoved(double pos, boolean isThumbSlide) {

                double min = (pos / 0.5) % 60;
                startMinutes = ((int)(min/interval))*interval;
                startHour = (int) (((pos / 30f) + 2f) % 12f) + 1;

                if (circularSliderView.getPrevSelectedStartAngle() > 90 && circularSliderView.getCurrentSelectedStartAngle() > 90 && ((circularSliderView.getPrevSelectedStartAngle() < 270 && circularSliderView.getCurrentSelectedStartAngle() >= 270) ||
                        (circularSliderView.getPrevSelectedStartAngle() > 270 && circularSliderView.getCurrentSelectedStartAngle() <= 270))) {
                    circularSliderView.setStartTimeAM(!circularSliderView.isStartTimeAM());
                }


                startTimeSB.delete(0, startTimeSB.length());
                startTimeSB.append(startHour)
                        .append(":")
                        .append(startMinutes < 10 ? String.format(locale, "%02d", startMinutes) : startMinutes)
                        .append(circularSliderView.isStartTimeAM() ? AM : PM);

                if(ontTimeChangedListener!=null){
                    ontTimeChangedListener.onStartTimeChange(startTimeSB.toString(),startHour,startMinutes,isStartTImeAM);
                }
            }

            @Override
            public void onEndSliderMoved(double pos, boolean isThumbSlide) {

                double min = (pos / 0.5) % 60;
                endMinutes = ((int)(min/interval))*interval;
                endHour = (int) (((pos / 30f) + 2f) % 12f) + 1;

                if (circularSliderView.getPrevSelectedEndAngle() > 90 && circularSliderView.getCurrentSelectedEndAngle() > 90 && ((circularSliderView.getPrevSelectedEndAngle() < 270 && circularSliderView.getCurrentSelectedEndAngle() >= 270) ||
                        (circularSliderView.getPrevSelectedEndAngle() > 270 && circularSliderView.getCurrentSelectedEndAngle() <= 270))) {
                    circularSliderView.setEndTimeAM(!circularSliderView.isEndTimeAM());
                }

                endTimeSB.delete(0, endTimeSB.length());
                endTimeSB.append(endHour)
                        .append(":")
                        .append(endMinutes < 10 ? String.format(locale, "%02d", endMinutes) : endMinutes)
                        .append(circularSliderView.isEndTimeAM() ? AM : PM);

                if(ontTimeChangedListener!=null){
                    ontTimeChangedListener.onEndTimeChange(endTimeSB.toString(),endHour,endMinutes,isEndTimeAM);
                }
            }

            @Override
            public void onStartSliderEvent(ThumbEvent event) {

            }

            @Override
            public void onEndSliderEvent(ThumbEvent event) {

            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        circularSliderView.onTouchEvent(event);
        return false;
    }

    public boolean isStartTimeAM(){
        return circularSliderView.isStartTimeAM();
    }

    public boolean isEndTimeAM(){
        return  circularSliderView.isEndTimeAM();
    }

    public int getStartMinutes(){return startMinutes;}

    public int getEndMinutes(){return endMinutes;}

    public int getEndHour(){
        return endHour;
    }

    public int getStartHour(){return startHour;}

    public void setClockStyle(boolean isClockInside){
        this.isClockInside = isClockInside;
    }

    public boolean isClockInside(){
        return isClockInside;
    }

    public void setOnTimeChangedListener(ontTimeChanged listener){
        ontTimeChangedListener = listener;
    }
}
