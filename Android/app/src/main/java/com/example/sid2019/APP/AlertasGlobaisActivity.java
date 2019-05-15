package com.example.sid2019.APP;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sid2019.APP.Connection.ConnectionHandler;
import com.example.sid2019.APP.Database.DatabaseHandler;
import com.example.sid2019.APP.Database.DatabaseReader;
import com.example.sid2019.APP.Helper.UserLogin;
import com.example.sid2019.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class AlertasGlobaisActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    DatabaseHandler db = new DatabaseHandler(this);
    String getAlertasGlobais = "http://" + IP + ":" + PORT + "/scripts/getAlertasGlobais.php";
    int year;
    int month;
    int day;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_globais);


        if (getIntent().hasExtra("date")){
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            year = yearMonthDay[0];
            month= yearMonthDay[1];
            day=yearMonthDay[2];
        }
        else{
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH)+1;
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        dateToString();
        getAlertas();
        listAlertas();
    }


    private void dateToString(){
       String yearString = Integer.toString(year);
       String monthString ="";
       String dayString="";
        if (month<10){
            monthString="0"+Integer.toString(month);
        }else{
            monthString=Integer.toString(month);
        }
        if(day<10){
            dayString="0"+Integer.toString(day);
        }
        else{
            dayString=Integer.toString(day);
        }
        date = yearString+"-"+monthString+"-"+dayString;
        String formatted_date = dayString+"-"+monthString+"-"+yearString;
        TextView stringData = findViewById(R.id.diaSelecionado_tv);
        stringData.setText(formatted_date);

    }

    public void showDatePicker(View v){
        Intent intent = new Intent(this,DatePickerActivity.class);
        intent.putExtra("global",1);
        startActivity(intent);
        finish();
    }

    private void getAlertas(){
        db.clearAlertasGlobais();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("date",date);
        ConnectionHandler jParser = new ConnectionHandler();
        JSONArray alertas_globais = jParser.getJSONFromUrl(getAlertasGlobais, params);
        try{
            if(alertas_globais!=null){
                for (int i=0;i< alertas_globais.length();i++){
                    JSONObject c = alertas_globais.getJSONObject(i);
                    String nomeVariavel = c.getString("nomeVariavel");
                    String dataHoraMedicao = c.getString("dataHoraAlerta");
                    String descricao = c.getString("descricao");
                    String intensidade = c.getString("intensidade");
                    double valorMedicao = c.getDouble("valorMedicao");
                    double limiteInferior = c.getDouble("limiteInferior");
                    double limiteSuperior = c.getDouble("limiteSuperior");


                    db.insert_alertaGlobal(dataHoraMedicao,nomeVariavel,limiteInferior,limiteSuperior,valorMedicao,descricao,intensidade);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    private void listAlertas(){
        TableLayout table = findViewById(R.id.tableAlertas);

        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursorAlertasGlobais = dbReader.readAlertasGlobais();
        table.removeAllViewsInLayout();
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView headerNomeVariavel = new TextView(this);
        headerNomeVariavel.setText("Nome Variavel");
        headerNomeVariavel.setTextSize(16);
        headerNomeVariavel.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerHora = new TextView(this);
        headerHora.setText("Hora");
        headerHora.setTextSize(16);
        headerHora.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerLimiteInferior = new TextView(this);
        headerLimiteInferior.setText("L. Inferior");
        headerLimiteInferior.setTextSize(16);
        headerLimiteInferior.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerLimiteSuperior = new TextView(this);
        headerLimiteSuperior.setText("L. Superior");
        headerLimiteSuperior.setTextSize(16);
        headerLimiteSuperior.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerValorMedicao = new TextView(this);
        headerValorMedicao.setText("Valor Medicao");
        headerValorMedicao.setTextSize(16);
        headerValorMedicao.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);


        TextView headerDescricao = new TextView(this);
        headerDescricao.setText("Descricao");
        headerDescricao.setTextSize(16);
        headerDescricao.setPadding(dpAsPixels(16),dpAsPixels(50),dpAsPixels(5),10);

        TextView headerIntensidade = new TextView(this);
        headerIntensidade.setText("Descricao");
        headerIntensidade.setTextSize(16);
        headerIntensidade.setPadding(dpAsPixels(16),dpAsPixels(50),dpAsPixels(5),10);

        headerRow.addView(headerNomeVariavel);
        headerRow.addView(headerHora);
        headerRow.addView(headerLimiteInferior);
        headerRow.addView(headerLimiteSuperior);
        headerRow.addView(headerValorMedicao);
        headerRow.addView(headerDescricao);
        headerRow.addView(headerIntensidade);

        table.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        while (cursorAlertasGlobais.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView nomeVariavel = new TextView(this);
            nomeVariavel.setText(cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("NomeVariavel")));
            nomeVariavel.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView hora = new TextView(this);
            String horaDesformatado = cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("DataHora"));
            String horaFormatado = horaDesformatado.split(" ")[1];
            hora.setText(horaFormatado);
            hora.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView limiteInferior = new TextView(this);
            limiteInferior.setText(Double.toString(cursorAlertasGlobais.getDouble(cursorAlertasGlobais.getColumnIndex("LimiteInferior"))));
            limiteInferior.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView limiteSuperior = new TextView(this);
            limiteSuperior.setText(Double.toString(cursorAlertasGlobais.getDouble(cursorAlertasGlobais.getColumnIndex("LimiteSuperior"))));
            limiteSuperior.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView valorMedicao = new TextView(this);
            valorMedicao.setText(Double.toString(cursorAlertasGlobais.getDouble(cursorAlertasGlobais.getColumnIndex("ValorMedicao"))));
            valorMedicao.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView descricao = new TextView(this);
            descricao.setText(cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("Descricao")));
            descricao.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);

            TextView intensidade = new TextView(this);
            intensidade.setText(cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("Intensidade")));
            intensidade.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);

            row.addView(nomeVariavel);
            row.addView(hora);
            row.addView(limiteInferior);
            row.addView(limiteSuperior);
            row.addView(valorMedicao);
            row.addView(descricao);
            row.addView(intensidade);

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        }


    }

    private int dpAsPixels(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);

    }

    public void refreshButton(View v){
        getAlertas();
        listAlertas();
    }
}
