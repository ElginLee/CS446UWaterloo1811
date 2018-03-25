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
    GraphView graph;
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mc_graph);
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                graph = (GraphView) findViewById(R.id.graph);
                graph.removeAllSeries();
                handler.postDelayed(this, 2000);
                drawGraph();
            }
        };
        handler.postDelayed(r, 0000);
    }

    private void drawGraph(){
        graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0,0),
                new DataPoint(1,new Random().nextInt(5 + 1)),
                new DataPoint(2, new Random().nextInt(5 + 1)),
                new DataPoint(3, new Random().nextInt(5 + 1)),
                new DataPoint(4, new Random().nextInt(5 + 1))
        });
        series.setSpacing(10);
        graph.addSeries(series);

// draw values on top
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"","a","b", "c","d", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

// draw values on top
        series.setDrawValuesOnTop(true);
    }

}
