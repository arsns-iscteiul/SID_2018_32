package com.example.sid2019.APP;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.sid2019.APP.Connection.ConnectionHandler;
import com.example.sid2019.APP.Database.DatabaseHandler;
import com.example.sid2019.APP.Database.DatabaseReader;
import com.example.sid2019.APP.Helper.UserLogin;
import com.example.sid2019.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GlobaisActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();

    String getMedicoesTemperatura = "http://" + IP + ":" + PORT + "/scripts/getMedicoesTemperatura.php";
    String getMedicoesLuz = "http://" + IP + ":" + PORT + "/scripts/getMedicoesLuz.php";
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_globais);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        updateMedicoes();
        drawGraphs();


    }

    public void refresh(View v){
        updateMedicoes();
        drawGraphs();
    }


    public void alertas(View v){
        Intent i = new Intent(this, AlertasGlobaisActivity.class);
        startActivity(i);
    }

    private void updateMedicoes(){
        db.clearMedicoes();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        ConnectionHandler jParser = new ConnectionHandler();
        JSONArray medicoesTemperatura = jParser.getJSONFromUrl(getMedicoesTemperatura, params);
        JSONArray medicoesLuz = jParser.getJSONFromUrl(getMedicoesLuz, params);
        try {
            if (medicoesTemperatura != null){
                for (int i=0;i< medicoesTemperatura.length();i++){
                    JSONObject c = medicoesTemperatura.getJSONObject(i);
                    String dataHoraMedicao = c.getString("dataHoraMedicao");
                    String valorMedicaoTemperatura = c.getString("valorMedicaoTemperatura");
                    db.insert_MedicaoTemperatura(dataHoraMedicao,Double.parseDouble(valorMedicaoTemperatura));
                }
            }
            if (medicoesLuz != null){
                for (int i=0;i< medicoesLuz.length();i++){
                    JSONObject c = medicoesLuz.getJSONObject(i);
                    String dataHoraMedicao = c.getString("dataHoraMedicao");
                    String valorMedicaoLuz = c.getString("valorMedicaoLuminosidade");
                    db.insert_MedicaoLuz(dataHoraMedicao,Double.parseDouble(valorMedicaoLuz));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawGraphs(){
        GraphView graphTemperatura = (GraphView) findViewById(R.id.temperatura_graph);
        GraphView graphLuz = (GraphView) findViewById(R.id.luz_graph);
        graphTemperatura.removeAllSeries();
        graphLuz.removeAllSeries();
        int helper=0;
        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursorTemperatura = dbReader.readMedicoesTemperatura();
        Cursor cursorLuz = dbReader.readMedicoesLuz();
        Date currentTimestamp = new Date();
        long currentLong = currentTimestamp.getTime();

        DataPoint[] datapointsTemperatura = new DataPoint[cursorTemperatura.getCount()];
        DataPoint[] datapointsLuz = new DataPoint[cursorLuz.getCount()];

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        while (cursorTemperatura.moveToNext()){
            Integer valorMedicaoTemperatura = cursorTemperatura.getInt(cursorTemperatura.getColumnIndex("Temperatura"));
            String dataHoraMedicao =  cursorTemperatura.getString(cursorTemperatura.getColumnIndex("DataHoraMedicao"));
            try {
                Date date = format.parse(dataHoraMedicao);
                long pointLong = date.getTime();
                long difference = currentLong - pointLong;
                double seconds = 300 - TimeUnit.MILLISECONDS.toSeconds(difference);
                datapointsTemperatura[helper]=new DataPoint(seconds,valorMedicaoTemperatura);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            helper++;
        }
        cursorTemperatura.close();

        helper = 0;
        while (cursorLuz.moveToNext()){
            Integer valorMedicaoLuz = cursorLuz.getInt(cursorLuz.getColumnIndex("Luz"));
            String dataHoraMedicao =  cursorLuz.getString(cursorLuz.getColumnIndex("DataHoraMedicao"));
            try {
                Date date = format.parse(dataHoraMedicao);
                long pointLong = date.getTime();
                long difference = currentLong - pointLong;
                double seconds = 300 - TimeUnit.MILLISECONDS.toSeconds(difference);
                datapointsLuz[helper]=new DataPoint(seconds,valorMedicaoLuz);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            helper++;
        }

        graphTemperatura.getViewport().setXAxisBoundsManual(true);
        graphTemperatura.getViewport().setMinX(0);
        graphTemperatura.getViewport().setMaxX(300);
        LineGraphSeries<DataPoint> seriesTemperatura = new LineGraphSeries<>(datapointsTemperatura);
        seriesTemperatura.setColor(Color.RED);
        seriesTemperatura.setTitle("Temperatura");
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphTemperatura);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"300", "250","200","150","100","50","0"});
        graphTemperatura.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graphTemperatura.getLegendRenderer().setVisible(true);
        graphTemperatura.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphTemperatura.getLegendRenderer().setBackgroundColor(Color.alpha(0));
        graphTemperatura.addSeries(seriesTemperatura);



        graphLuz.getViewport().setXAxisBoundsManual(true);
        graphLuz.getViewport().setMinX(0);
        graphLuz.getViewport().setMaxX(300);
        LineGraphSeries<DataPoint> seriesLuz = new LineGraphSeries<>(datapointsLuz);
        seriesLuz.setColor(Color.YELLOW);
        seriesLuz.setTitle("Luminosidade");
        StaticLabelsFormatter staticLabelsFormatter2 = new StaticLabelsFormatter(graphLuz);
        staticLabelsFormatter2.setHorizontalLabels(new String[] {"300", "250","200","150","100","50","0"});
        graphLuz.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter2);
        graphLuz.getLegendRenderer().setVisible(true);
        graphLuz.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphLuz.getLegendRenderer().setBackgroundColor(Color.alpha(0));
        graphLuz.addSeries(seriesLuz);


    }


}
