package ca.uwaterloo.ewslee.boardcast;

/**
 * Created by kianl on 3/25/2018.
 */

public class Utils {
    public String[] splitPayload(String value){
        String[] result = value.split("=");
        return result;
    }

    public String resultsToString(int[] result ){
        String text ="/";
        for(int i=0; i < result.length;i++){
            text+= result[i]+"/";
        }
        return text;
    }

    public String[] splitString(String value){
        String[] result = value.split("/");
        return result;
    }

    public int [] splitResults(String value){
        String[] result = value.split("/");
        int [] results = new int[result.length-1];
        for(int i = 0; i < results.length;i++){
            results[i] = Integer.parseInt(result[i+1]);
        }
        return results;
    }

    public String getAnswer(String value){
        String[] result = value.split("/");
        return result[0];
    }
}
