package com.example.sid2019.APP;

import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    Spinner spinner;
    DatabaseHandler db = new DatabaseHandler(this);
    String getCulturasOfInvestigador = "http://" + IP + ":" + PORT + "/scripts/getCulturasOfUser.php";
    String getInformacaoCultura = "http://" + IP + ":" + PORT + "/scripts/getInformacaoCultura.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getCulturasAvailable();
    }

    @Override
    protected void onResume(){
        super.onResume();
        populateComboBox();
        updateTextFields();
       // updateSpinner();

    }

    private void getCulturasAvailable(){
        db.clearIds();
        db.clearCultura();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        ConnectionHandler jParser = new ConnectionHandler();
        JSONArray culturasOfInvestigador = jParser.getJSONFromUrl(getCulturasOfInvestigador, params);
        try {
        if (culturasOfInvestigador != null){
            for (int i=0;i< culturasOfInvestigador.length();i++){
                JSONObject c = culturasOfInvestigador.getJSONObject(i);
                int idCultura = c.getInt("idCultura");
                db.insert_available_id(idCultura);
            }
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void populateComboBox(){
        spinner = findViewById(R.id.culturasDisponiveis_cb);
        ArrayList<String> idCulturas = new ArrayList<>();
        DatabaseReader dbReader = new DatabaseReader(db);
        int idCultura=-1;
        Cursor cursorAvailableIds = dbReader.readAvailableIds();
        Cursor cursorCultura = dbReader.readCultura();
        while (cursorAvailableIds.moveToNext()){
            idCulturas.add(Integer.toString(cursorAvailableIds.getInt(cursorAvailableIds.getColumnIndex("IDCultura"))));
        }
        while (cursorCultura.moveToNext()){
            idCultura=cursorCultura.getInt(cursorCultura.getColumnIndex("IDCultura"));
            Log.d("teste",Integer.toString(idCultura));
        }
        cursorAvailableIds.close();
        cursorCultura.close();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,idCulturas);
        spinner.setAdapter(adp);
        if (idCultura!=-1){
        spinner.setSelection(adp.getPosition(Integer.toString(idCultura)));}


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
                updateTextFields();
            } catch (JSONException e) {
                e.printStackTrace();
            }}
        }
    }

    private void updateTextFields(){
        TextView nomeCultura = findViewById(R.id.nomeCultura_tv);
        TextView descricaoCultura = findViewById(R.id.descricaoCultura_tv);
        Button alertasCultura =  findViewById(R.id.alertas_bt);

        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursor = dbReader.readCultura();
        while (cursor.moveToNext()){
            nomeCultura.setText(cursor.getString(cursor.getColumnIndex("NomeCultura")));
            nomeCultura.setVisibility(View.VISIBLE);
            descricaoCultura.setText(cursor.getString(cursor.getColumnIndex("DescricaoCultura")));
            descricaoCultura.setVisibility(View.VISIBLE);
            alertasCultura.setVisibility(View.VISIBLE);
        }
        cursor.close();



    }

    public void variaveisGlobais(View v){
        Intent i = new Intent(this, GlobaisActivity.class);
        startActivity(i);
    }

    public void alertaCultura(View v){
        Intent i = new Intent(this,AlertasCulturaActivity.class);
        startActivity(i);
    }
}
