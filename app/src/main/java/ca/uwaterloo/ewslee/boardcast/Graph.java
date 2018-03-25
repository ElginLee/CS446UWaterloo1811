package ca.uwaterloo.ewslee.boardcast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.Random;

/**
 * Created by kianl on 3/25/2018.
 */

public class Graph {
    public void drawGraph(GraphView graph, int [] results){
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0,0),
                new DataPoint(1,results[0]),
                new DataPoint(2, results[1]),
                new DataPoint(3, results[2]),
                new DataPoint(4, results[3])
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
