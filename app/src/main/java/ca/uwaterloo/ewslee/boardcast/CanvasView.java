package ca.uwaterloo.ewslee.boardcast;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by #YEOGUOKUAN on 25/2/2018.
 */

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private ArrayList<Path> paths = new ArrayList<Path>();
    Context context;
    private ArrayList<Paint> paints = new ArrayList<Paint>();
    private float mX, mY;
    private static final float TOLERANCE = 5;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        paths.add(new Path());

        // and we set a new Paint with the desired attributes
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.BLACK);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        for(int i = 0; i < paths.size(); i++){
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        //canvas.drawPath(paths.get(paths.size()-1), paints.get(paints.size()-1));
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        paths.get(paths.size()-1).moveTo(x, y);
        mX = x;
        mY = y;
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            paths.get(paths.size()-1).quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    public void clearBoard() {
        paths.get(paths.size()-1).reset();
        for(int i = 0; i < paths.size(); i++){
            paths.get(i).reset();
        }
        invalidate();
    }

    public void black(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.BLACK);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    public void red(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.RED);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    public void blue(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.BLUE);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    public void green(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.GREEN);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    public void yellow(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.YELLOW);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(10f);
    }

    public void eraser(){
        paths.add(new Path());
        paints.add(new Paint());
        paints.get(paints.size()-1).setAntiAlias(true);
        paints.get(paints.size()-1).setColor(Color.WHITE);
        paints.get(paints.size()-1).setStyle(Paint.Style.STROKE);
        paints.get(paints.size()-1).setStrokeJoin(Paint.Join.ROUND);
        paints.get(paints.size()-1).setStrokeWidth(50f);
    }

    public void undo(){
        if(paths.size() == 0) return;
        if(paths.size() == 1){
            paths.get(0).reset();
            invalidate();
            return;
        }
        if(paths.get(paths.size()-1).isEmpty()){
            paths.get(paths.size() - 1).reset();
            paths.remove(paths.size()-1);
            paths.get(paths.size() - 1).reset();
        }else{
            paths.get(paths.size() - 1).reset();
        }
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        paths.get(paths.size()-1).lineTo(mX, mY);
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }
}
