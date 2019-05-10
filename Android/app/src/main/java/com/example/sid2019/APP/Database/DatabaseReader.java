package com.example.sid2019.APP.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseReader {
    SQLiteDatabase db;

    public DatabaseReader(DatabaseHandler dbHandler){
        db = dbHandler.getReadableDatabase();

    }

    public Cursor readAvailableIds(){
        Cursor cursor = db.query(
                DataBaseConfig.AvailableIds.TABLE_NAME,   // Nome da tabela
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readCultura(){
        Cursor cursor = db.query(
                DataBaseConfig.Cultura.TABLE_NAME,   // Nome da tabela
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readMedicoesTemperatura(){
        Cursor cursor = db.query(
                DataBaseConfig.MedicoesTemperatura.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }


    public Cursor readMedicoesLuz(){
        Cursor cursor = db.query(
                DataBaseConfig.MedicoesLuz.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readAlertasGlobais(){
        Cursor cursor = db.query(
                DataBaseConfig.AlertasGlobais.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readAlertasVariavel(){
        Cursor cursor = db.query(
                DataBaseConfig.AlertasVariavel.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }
}
