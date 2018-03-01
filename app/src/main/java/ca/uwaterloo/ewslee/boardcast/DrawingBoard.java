package ca.uwaterloo.ewslee.boardcast;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * Created by #YEOGUOKUAN on 25/2/2018.
 */

public class DrawingBoard extends Activity {

    private CanvasView customCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_board);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
    }

    public void clearBoard(View v) {
        customCanvas.clearBoard();
    }

    public void eraser(View v){
        customCanvas.eraser();
    }

    public void undo(View v){
        customCanvas.undo();
    }

    public void black(View v){
        customCanvas.black();
    }

    public void red(View v){
        customCanvas.red();
    }

    public void blue(View v){
        customCanvas.blue();
    }

    public void green(View v){
        customCanvas.green();
    }

    public void yellow(View v){
        customCanvas.yellow();
    }

}
