/**
 * Created by axelinternet on 2017-05-21.
 */

import netP5.*;
import processing.core .PApplet;
import oscP5.*;
import processing.data.JSONArray;
import processing.data.JSONObject;


public class MainApp extends PApplet {

    String letters = "";
    OscP5 oscP5;
    JSONArray values;
    int sentenceScore;
    NetAddress myRemoteLocation;

    public static void main(String[] args) {
        PApplet.main("MainApp", args);

    }

    public void settings() {
        size(1200, 1200);
    }

    public void setup() {
        background(80);
        values = loadJSONArray("afinn.json");
        myRemoteLocation = new NetAddress("127.0.0.1",32000);
        oscP5 = new OscP5(this,12000);
    }

    public int score(String sentence) {
        /* takes a sentacne, splits and scores the whole sentence */
        String[] wordArray = sentence.split(" ");
        sentenceScore = 0;
        for ( String ss : wordArray) {
            for (int i = 0; i < values.size(); i++) {
                JSONObject afinnJSON = values.getJSONObject(i);
                String word = afinnJSON.getString("word");
                String wordScore = afinnJSON.getString("score");
                if (word.equals(ss)) {
                    println(ss, "found with score: ", wordScore);
                    int d = Integer.valueOf((String) wordScore); // Cast string to int
                    sentenceScore += d;
                }
            }
        }

        return sentenceScore;
    }

    public void draw() {
        if (sentenceScore == 0) {
            fill(128, 128, 128);
        } else if (sentenceScore < 0) {
            fill(abs(sentenceScore) * 45, 0,0 );
        } else {
            fill(0, sentenceScore * 45, 0);
        }
        background(80);
        ellipse(600,600, 600, 600);
        fill(0);
        textSize(64);
        textAlign(LEFT);
        float cursorPosition = textWidth(letters);
        text(letters, 200, 200);

    }

    public void keyPressed() {
        if (key == BACKSPACE) {
            if (letters.length() > 0) {
                letters = letters.substring(0, letters.length()-1);
            }
        } else if (key == ENTER) {
            int s = score(letters.toLowerCase());
            println(letters, s);
            letters="";
            background(80);

            OscMessage myMessage = new OscMessage("/score");
            myMessage.add(s); /* add an int to the osc message */
            oscP5.send(myMessage, myRemoteLocation);

        } else if (textWidth(letters+key) < width) {
            letters = letters + key;
        }
    }

}
