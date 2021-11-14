package com.fanshuff.rma.yourdiary.util;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;

public class FanshuffEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Paint line;
    public FanshuffEditText(Context context, AttributeSet As){
        super(context,As);
        setFocusable(true);
        line=new Paint();
        line.setColor(Color.TRANSPARENT);
        line.setStrokeWidth(2);
        setPadding(50,10,0,0);
        setGravity(Gravity.TOP);
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {

        if(getText().toString().length()!=0){
            float y=0;
            Paint p=new Paint();
            p.setColor(Color.GRAY);
            p.setTextSize(30);
            for(int l=0;l<getLineCount();l++){
                y=((l+1)*getLineHeight())-(getLineHeight()/4);
                if(l == 0 || (l+1)%5 == 0)
                canvas.drawText(String.valueOf(l+1),0,y,p);
                canvas.save();
            }
        }
        int k=getLineHeight();
        int i=getLineCount();
        canvas.drawLine(90,0,90,getHeight()+(i*k),line);
        int y=(getLayout().getLineForOffset(getSelectionStart())+1)*k;
        canvas.drawLine(0,y,getWidth(),y,line);
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }

}
