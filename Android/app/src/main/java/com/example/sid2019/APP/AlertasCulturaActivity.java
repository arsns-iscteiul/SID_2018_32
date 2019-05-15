package com.example.sid2019.APP;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AlertasCulturaActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    DatabaseHandler db = new DatabaseHandler(this);
    String getAlertasCultura = "http://" + IP + ":" + PORT + "/scripts/getAlertasCultura.php";
    String getInformacaoCultura = "http://" + IP + ":" + PORT + "/scripts/getInformacaoCultura.php";
    int year;
    int month;
    int day;
    String date;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_cultura);

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
        populateComboBox();
        dateToString();
        getAlertas();
        listAlertas();
    }

    private void populateComboBox(){

        spinner = findViewById(R.id.culturasDisponiveis_cb2);
        ArrayList<String> idCulturas = new ArrayList<>();
        DatabaseReader dbReader = new DatabaseReader(db);
        int idCultura=0;
        Cursor cursorAvailableIds = dbReader.readAvailableIds();
        Cursor cursorCultura = dbReader.readCultura();
        while (cursorAvailableIds.moveToNext()){
            idCulturas.add(Integer.toString(cursorAvailableIds.getInt(cursorAvailableIds.getColumnIndex("IDCultura"))));
        }
        while (cursorCultura.moveToNext()){
            idCultura=cursorCultura.getInt(cursorCultura.getColumnIndex("IDCultura"));
            TextView nomeCultura = findViewById(R.id.nomeCultura_tv);
            nomeCultura.setText(cursorCultura.getString(cursorCultura.getColumnIndex("NomeCultura")));
        }
        cursorAvailableIds.close();
        cursorCultura.close();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,idCulturas);
        spinner.setAdapter(adp);
        spinner.setSelection(adp.getPosition(Integer.toString(idCultura)));

    }

    public void refreshButton(View v){
        if (spinner.getSelectedItem()!=null){
            String selectedCulture = spinner.getSelectedItem().toString();
            if (selectedCulture!=null){
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("idCultura",selectedCulture);
                ConnectionHandler jParser = new ConnectionHandler();
                JSONArray informacaoCultura = jParser.getJSONFromUrl(getInformacaoCultura, params);
                try {
                    if (informacaoCultura != null){
                        for (int i=0;i< informacaoCultura.length();i++){
                            JSONObject c = informacaoCultura.getJSONObject(i);
                            String nomeCultura = c.getString("nomeCultura");
                            String descricaoCultura = c.getString("descricaoCultura");
                            db.insert_Cultura(Integer.parseInt(selectedCulture),nomeCultura,descricaoCultura);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }}
            populateComboBox();
            getAlertas();
            listAlertas();
        }
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

    private void getAlertas() {
        db.clearAlertasVariavel();
        if (spinner.getSelectedItem()!=null){
            String idCultura = spinner.getSelectedItem().toString();
            HashMap<String, String> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            params.put("date", date);
            params.put("idCultura",idCultura);
            ConnectionHandler jParser = new ConnectionHandler();
            JSONArray alertas_variavel = jParser.getJSONFromUrl(getAlertasCultura, params);
            try {
                if (alertas_variavel != null) {
                    for (int i = 0; i < alertas_variavel.length(); i++) {
                        JSONObject c = alertas_variavel.getJSONObject(i);
                        String nomeVariavel = c.getString("nomeVariavel");
                        String dataHoraAlerta = c.getString("dataHoraAlerta");
                        String descricao = c.getString("descricao");
                        String intensidade = c.getString("intensidade");
                        String dataHoraMedicao = c.getString("dataHoraMedicao");
                        double valorMedicao = c.getDouble("valorMedicao");
                        double limiteInferior = c.getDouble("limiteInferior");
                        double limiteSuperior = c.getDouble("limiteSuperior");
                        db.insert_alertaCulturaVariaveis(nomeVariavel, dataHoraAlerta, descricao, intensidade, dataHoraMedicao, valorMedicao, limiteInferior,limiteSuperior);
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }}



    }

    private void listAlertas(){
        TableLayout table = findViewById(R.id.tableAlertas);

        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursorAlertasVariavel = dbReader.readAlertasVariavel();
        table.removeAllViewsInLayout();
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView headerNomeVariavel = new TextView(this);
        headerNomeVariavel.setText("Nome Variavel");
        headerNomeVariavel.setTextSize(16);
        headerNomeVariavel.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerHoraAlerta = new TextView(this);
        headerHoraAlerta.setText("HoraAlerta");
        headerHoraAlerta.setTextSize(16);
        headerHoraAlerta.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerDescricao = new TextView(this);
        headerDescricao.setText("Descricao");
        headerDescricao.setTextSize(16);
        headerDescricao.setPadding(dpAsPixels(16),dpAsPixels(50),dpAsPixels(5),10);

        TextView headerIntensidade = new TextView(this);
        headerDescricao.setText("Intensidade");
        headerDescricao.setTextSize(16);
        headerDescricao.setPadding(dpAsPixels(16),dpAsPixels(50),dpAsPixels(5),10);


        TextView headerHoraMedicao = new TextView(this);
        headerHoraMedicao.setText("HoraMedicao");
        headerHoraMedicao.setTextSize(16);
        headerHoraMedicao.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);


        TextView headerValorMedicao = new TextView(this);
        headerValorMedicao.setText("Valor Medicao");
        headerValorMedicao.setTextSize(16);
        headerValorMedicao.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerLimiteInferior = new TextView(this);
        headerLimiteInferior.setText("L. Inferior");
        headerLimiteInferior.setTextSize(16);
        headerLimiteInferior.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerLimiteSuperior = new TextView(this);
        headerLimiteSuperior.setText("L. Superior");
        headerLimiteSuperior.setTextSize(16);
        headerLimiteSuperior.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);


        headerRow.addView(headerNomeVariavel);
        headerRow.addView(headerHoraAlerta);
        headerRow.addView(headerDescricao);
        headerRow.addView(headerIntensidade);
        headerRow.addView(headerHoraMedicao);
        headerRow.addView(headerValorMedicao);
        headerRow.addView(headerLimiteInferior);
        headerRow.addView(headerLimiteSuperior);

        table.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        while (cursorAlertasVariavel.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView nomeVariavel = new TextView(this);
            nomeVariavel.setText(cursorAlertasVariavel.getString(cursorAlertasVariavel.getColumnIndex("NomeVariavel")));
            nomeVariavel.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView horaAlerta = new TextView(this);
            String horaDesformatadoAlerta = cursorAlertasVariavel.getString(cursorAlertasVariavel.getColumnIndex("DataHoraAlerta"));
            String horaFormatadoAlerta = horaDesformatadoAlerta.split(" ")[1];
            horaAlerta.setText(horaFormatadoAlerta);
            horaAlerta.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView descricao = new TextView(this);
            descricao.setText(cursorAlertasVariavel.getString(cursorAlertasVariavel.getColumnIndex("Descricao")));
            descricao.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);

            TextView intensidade = new TextView(this);
            descricao.setText(cursorAlertasVariavel.getString(cursorAlertasVariavel.getColumnIndex("Intensidade")));
            descricao.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);

            TextView hora = new TextView(this);
            String horaDesformatado = cursorAlertasVariavel.getString(cursorAlertasVariavel.getColumnIndex("DataHoraMedicao"));
            String horaFormatado = horaDesformatado.split(" ")[1];
            hora.setText(horaFormatado);
            hora.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView valorMedicao = new TextView(this);
            valorMedicao.setText(Double.toString(cursorAlertasVariavel.getDouble(cursorAlertasVariavel.getColumnIndex("ValorMedicao"))));
            valorMedicao.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView limiteInferior = new TextView(this);
            limiteInferior.setText(Double.toString(cursorAlertasVariavel.getDouble(cursorAlertasVariavel.getColumnIndex("LimiteInferior"))));
            limiteInferior.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView limiteSuperior = new TextView(this);
            limiteSuperior.setText(Double.toString(cursorAlertasVariavel.getDouble(cursorAlertasVariavel.getColumnIndex("LimiteSuperior"))));
            limiteSuperior.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            row.addView(headerNomeVariavel);
            row.addView(headerHoraAlerta);
            row.addView(headerDescricao);
            row.addView(headerIntensidade);
            row.addView(headerHoraMedicao);
            row.addView(headerValorMedicao);
            row.addView(headerLimiteInferior);
            row.addView(headerLimiteSuperior);

            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        }


    }

    private int dpAsPixels(int dp){
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);

    }

    public void showDatePicker (View v){
        Intent intent = new Intent(this, DatePickerActivity.class);
        startActivity(intent);
        finish();
    }
}
