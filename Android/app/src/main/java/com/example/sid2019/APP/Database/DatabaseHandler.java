package com.example.sid2019.APP.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sid.db";
    DataBaseConfig config = new DataBaseConfig();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        System.out.println("Creating db");

        sqLiteDatabase.execSQL(config.SQL_CREATE_CULTURA);
        System.out.println("1");
        sqLiteDatabase.execSQL(config.SQL_CREATE_MEDICOESTEMPERATURA);
        System.out.println("2");
        sqLiteDatabase.execSQL(config.SQL_CREATE_MEDICOESLUZ);
        System.out.println("3");
        sqLiteDatabase.execSQL(config.SQL_CREATE_ALERTASGLOBAIS);
        System.out.println("4");
        sqLiteDatabase.execSQL(config.SQL_CREATE_ALERTASVARIAVEL);
        System.out.println("5");
        sqLiteDatabase.execSQL(config.SQL_CREATE_AVAILABLEIDS);
        System.out.println("6");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert_available_id(int idCultura){
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.AvailableIds.COLUMN_NAME_IDCULTURA,idCultura);
        getWritableDatabase().insert(DataBaseConfig.AvailableIds.TABLE_NAME,null,values);
    }

    public void insert_Cultura(int idCultura,String nomeCultura,String descricaoCultura){

        clearCultura();
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_IDCULTURA,idCultura);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_NOMECULTURA,nomeCultura);
        values.put(DataBaseConfig.Cultura.COLUMN_NAME_DESCRICAOCULTURA,descricaoCultura);
        getWritableDatabase().insert(DataBaseConfig.Cultura.TABLE_NAME,null,values);
    }

    public void insert_MedicaoTemperatura(String dataHoraMedicao, double temperatura){

        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.MedicoesTemperatura.COLUMN_NAME_DATAHORAMEDICAO,dataHoraMedicao);
        values.put(DataBaseConfig.MedicoesTemperatura.COLUMN_NAME_TEMPERATURA,temperatura);
        getWritableDatabase().insert(DataBaseConfig.MedicoesTemperatura.TABLE_NAME,null,values);
    }
    public void clearMedicoes(){
        getWritableDatabase().execSQL(config.SQL_DELETE_MEDICOESTEMPERATURA_DATA);
        getWritableDatabase().execSQL(config.SQL_DELETE_MEDICOESLUZ_DATA);
    }

    public void insert_MedicaoLuz(String dataHoraMedicao, double luz) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.MedicoesLuz.COLUMN_NAME_DATAHORAMEDICAO,dataHoraMedicao);
        values.put(DataBaseConfig.MedicoesLuz.COLUMN_NAME_LUZ,luz);
        getWritableDatabase().insert(DataBaseConfig.MedicoesLuz.TABLE_NAME,null,values);
    }

    public void insert_alertaGlobal(String dataHoraMedicao, String nomeVariavel, double limiteInferior, double limiteSuperior, double valorMedicao, String descricao, String intensidade) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_DATAHORAMEDICAO,dataHoraMedicao);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_NOMEVARIAVEL,nomeVariavel);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_Limite_Inferior,limiteInferior);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_Limite_Superior,limiteSuperior);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_VALORMEDICAO,valorMedicao);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_DESCRICAO,descricao);
        values.put(DataBaseConfig.AlertasGlobais.COLUMN_NAME_INTENSIDADE,intensidade);
        getWritableDatabase().insert(DataBaseConfig.AlertasGlobais.TABLE_NAME,null,values);
    }

    public void insert_alertaCulturaVariaveis(String nomeVariavel, String dataHoraAlerta, String descricao, String intensidade, String dataHoraMedicao, Double valorMedicao, Double limiteInferior, Double limiteSuperior) {
        ContentValues values = new ContentValues();
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_NOMEVARIAVEL,nomeVariavel);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_DATAHORAALERTA,dataHoraAlerta);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_DESCRICAO,descricao);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_INTENSIDADE,intensidade);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_DATAHORAMEDICAO,dataHoraMedicao);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_VALORMEDICAO,valorMedicao);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_Limite_Inferior,limiteInferior);
        values.put(DataBaseConfig.AlertasVariavel.COLUMN_NAME_Limite_Superior,limiteSuperior);
        getWritableDatabase().insert(DataBaseConfig.AlertasVariavel.TABLE_NAME,null,values);
    }

    public void clearAlertasGlobais() {
        getWritableDatabase().execSQL(config.SQL_DELETE_ALERTASGLOBAIS_DATA);
    }

    public void clearAlertasVariavel() {
        getWritableDatabase().execSQL(config.SQL_DELETE_ALERTASVARIAVEL_DATA);
    }
    public void clearIds(){
        System.out.println("Clearing...");
        getWritableDatabase().execSQL(config.SQL_DELETE_AVAILABLEIDS);
    }
    public void clearCultura(){
        getWritableDatabase().execSQL(config.SQL_DELETE_CULTURA_DATA);
    }


}
