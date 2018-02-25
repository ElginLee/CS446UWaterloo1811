package ca.uwaterloo.ewslee.boardcast;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Random;


/**
 * Created by kianl on 2/23/2018.
 */

public class ResponseGraphActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.response_graph);
        drawGraph();
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                handler.postDelayed(this, 5000);
                drawGraph();
            }
        };
        handler.postDelayed(r, 5000);
    }

    private void drawGraph(){
        int x =  new Random().nextInt(5 + 1);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1,x),
                new DataPoint(2, new Random().nextInt(5 + 1)),
                new DataPoint(3, new Random().nextInt(5 + 1)),
                new DataPoint(4, new Random().nextInt(5 + 1))
        });
        series.setSpacing(10);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);


        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"a","b", "c","d"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

// draw values on top
        series.setDrawValuesOnTop(true);
    }

}
