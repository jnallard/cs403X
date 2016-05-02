package cs403x.crowdcade;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Joshua on 5/1/2016.
 */
public class ArcadeTextView extends TextView {

    public ArcadeTextView(Context context){
        super(context);
        setCustomFont();
    }
    public ArcadeTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        setCustomFont();
    }
    public ArcadeTextView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        setCustomFont();
    }
    public ArcadeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr);
        setCustomFont();
    }

    private void setCustomFont(){
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/PressStart2P-Regular.ttf");
        setTextColor(Color.WHITE);
        setTypeface(tf);
    }
}
