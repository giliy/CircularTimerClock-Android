# CircularTimerClock-Android
Circular clock view that can set timer range between hours

![circular timer clock](https://user-images.githubusercontent.com/11720098/31091386-7882e1ca-a7b3-11e7-8677-43a6689954af.gif)

## Attributes

| Attr  | format | decription |
| ------------- | ------------- | ------------- |
| start_hour  | integer  | An integer between 1 to 12  |
| end_hour  | integer  | An integer between 1 to 12  |
| start_minutes  | integer  | An integer between 0 to 59  |
| end_minutes  | integer  | An integer between 0 to 59  |
| clock_time_interval  | integer  | An integer between 0 to 59. The interval of the clock jump that time can be display|
| clock_tick_interval  | integer  | An integer between 0 to 59. The clock tick interval of the clock |
| clock_hour_color  | color  | The color of the hours clock  |
| clock_tick_color  | color  | The color of the clock ticks  |
| border_color  | color  | The color of the clock border  |
| arc_gradient_color_start  | color  | The start arc color gradient  |
| arc_gradient_color_end  | color  | the end color gradient  |
| start_thumb_color  | color  | The color of the start thumb |
| end_thumb_color  | color  | The color of the end thumb  |
|  arc_color | color  | The arc color between start and end thumbs  |
|  start_thumb_size | dimention  | The start thumb size  |
|  end_thumb_size | dimention  | The end thumb size  |
| arc_dash_size  | dimention  | The thickness of the line between the start and end  |
| border_thickness  | dimention  | The thickness of the clock border  |
| thumb_size  | dimention  | The size of the thumbs  |
| hours_size  | dimention  | The size of the clock hours  |
| start_time_is_am  | boolean  | Is the start time is AM  |
| end_time_is_am  | boolean  | Is the end time is AM  |
| is_clock_inside  | boolean  | Is the clock view insie  |
| start_thumb_image  | reference  | the image drawable of the start thumb  |
| end_thumb_image  | reference  | the image drawable of the end thumb  |
| clock_background_image  | reference  | The image drawable of the backgound clock  |

# How to use

Add remote maven url
```
repositories {
     maven {
         url "https://jitpack.io"
     }
}
```

### Gradle
```
dependencies {
    compile 'com.github.giliy:CircularTimerClock-Android:0.1.1'
}
```

### To use it in your code
Simply add the View to your layout

```
 <giliy.com.circulartimerview.CircularTimerClock
    android:layout_width="wrap_content"
    android:id="@+id/circular_clock"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    timer:arc_dash_size="40dp"
    timer:start_hour="5"
    timer:start_minutes="20"
    timer:end_hour="9"
    timer:end_minutes="29"
    timer:clock_time_interval="5"
    timer:clock_tick_interval="15"
    timer:clock_hour_color="#FFF"
    timer:clock_tick_color="#000"
    timer:hours_size="18sp"
    timer:start_time_is_am="true"
    timer:end_time_is_am="false"
    timer:is_clock_inside="true"
    timer:border_thickness="40dp"
    timer:border_color="#e7e7e7"
    timer:arc_gradient_color_start="#6ef4b1"
    timer:arc_gradient_color_end="#00a351"
    timer:start_thumb_image="@drawable/circle_toggle_shape"
    timer:end_thumb_image="@drawable/circle_toggle_shape"
    timer:clock_background_image="@drawable/sunrise"
    timer:thumb_size="35dp" >

</giliy.com.circulartimerview.CircularTimerClock>
```
To retrieve the time simply call the view from your activity like this
```java
CircularTimerClock clock = (CircularTimerClock) findViewById(R.id.circular_clock);
        clock.setOnTimeChangedListener(new CircularTimerClock.ontTimeChanged() {
            @Override
            public void onStartTimeChange(String time, int hour, int minutes,boolean isAM) {
                Log.d("time: ",""+time);

            }

            @Override
            public void onEndTimeChange(String time, int hour, int minutes, boolean isAM) {
                Log.d("time: ",""+time);
            }
        });
```
