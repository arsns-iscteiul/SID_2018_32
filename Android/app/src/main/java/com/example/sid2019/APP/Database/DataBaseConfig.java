package com.example.sid2019.APP.Database;

import android.provider.BaseColumns;

public class DataBaseConfig {


    public static class AvailableIds implements BaseColumns {
        public static final String TABLE_NAME="AvailableIds";
        public static final String COLUMN_NAME_IDCULTURA="IDCultura";

    }

    public static class Cultura implements BaseColumns {
        public static final String TABLE_NAME="Cultura";
        public static final String COLUMN_NAME_IDCULTURA="IDCultura";
        public static final String COLUMN_NAME_NOMECULTURA="NomeCultura";
        public static final String COLUMN_NAME_DESCRICAOCULTURA="DescricaoCultura";

    }

    public static class MedicoesTemperatura implements BaseColumns {
        public static final String TABLE_NAME="MedicoesTemperatura";
        public static final String COLUMN_NAME_IDMEDICAO="idMedicao";
        public static final String COLUMN_NAME_DATAHORAMEDICAO="DataHoraMedicao";
        public static final String COLUMN_NAME_TEMPERATURA="Temperatura";
    }
    public static class MedicoesLuz implements BaseColumns {
        public static final String TABLE_NAME="MedicoesLuz";
        public static final String COLUMN_NAME_IDMEDICAO="idMedicao";
        public static final String COLUMN_NAME_DATAHORAMEDICAO="DataHoraMedicao";
        public static final String COLUMN_NAME_LUZ="Luz";
    }

    public static class AlertasGlobais implements BaseColumns {
        public static final String TABLE_NAME="AlertasGlobais";
        public static final String COLUMN_NAME_IDALERTA="idAlerta";
        public static final String COLUMN_NAME_DATAHORAMEDICAO="DataHora";
        public static final String COLUMN_NAME_DESCRICAO="Descricao";
        public static final String COLUMN_NAME_NOMEVARIAVEL="NomeVariavel";
        public static final String COLUMN_NAME_Limite_Inferior="LimiteInferior";
        public static final String COLUMN_NAME_Limite_Superior="LimiteSuperior";
        public static final String COLUMN_NAME_VALORMEDICAO="ValorMedicao";
        public static final String COLUMN_NAME_INTENSIDADE="Intensidade";

    }

    public static class AlertasVariavel implements BaseColumns {
        public static final String TABLE_NAME="AlertasVariavel";
        public static final String COLUMN_NAME_IDALERTA="idAlerta";
        public static final String COLUMN_NAME_NOMEVARIAVEL="NomeVariavel";
        public static final String COLUMN_NAME_DATAHORAALERTA="DataHoraAlerta";
        public static final String COLUMN_NAME_DESCRICAO="Descricao";
        public static final String COLUMN_NAME_INTENSIDADE="Intensidade";
        public static final String COLUMN_NAME_DATAHORAMEDICAO="DataHoraMedicao";
        public static final String COLUMN_NAME_VALORMEDICAO="ValorMedicao";;
        public static final String COLUMN_NAME_Limite_Inferior="LimiteInferior";
        public static final String COLUMN_NAME_Limite_Superior="LimiteSuperior";
    }

    protected static final String SQL_CREATE_CULTURA=
            "CREATE TABLE " + Cultura.TABLE_NAME +
                    " (" + Cultura.COLUMN_NAME_IDCULTURA + " INTEGER PRIMARY KEY," +
                    Cultura.COLUMN_NAME_NOMECULTURA + " TEXT," +
                    Cultura.COLUMN_NAME_DESCRICAOCULTURA + " TEXT )";

    protected static final String SQL_DELETE_CULTURA_DATA=
            "DELETE FROM " + Cultura.TABLE_NAME;


    protected static final String SQL_CREATE_MEDICOESTEMPERATURA=
            "CREATE TABLE " + MedicoesTemperatura.TABLE_NAME +
                    " (" + MedicoesTemperatura.COLUMN_NAME_IDMEDICAO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MedicoesTemperatura.COLUMN_NAME_DATAHORAMEDICAO + " TIMESTAMP," +
                    MedicoesTemperatura.COLUMN_NAME_TEMPERATURA + " DOUBLE  )";

    protected static final String SQL_DELETE_MEDICOESTEMPERATURA_DATA=
            "DELETE FROM " + MedicoesTemperatura.TABLE_NAME;


    protected static final String SQL_CREATE_MEDICOESLUZ=
            "CREATE TABLE " + MedicoesLuz.TABLE_NAME +
                    " (" + MedicoesLuz.COLUMN_NAME_IDMEDICAO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    MedicoesLuz.COLUMN_NAME_DATAHORAMEDICAO + " TIMESTAMP," +
                    MedicoesLuz.COLUMN_NAME_LUZ + " DOUBLE  )";

    protected static final String SQL_DELETE_MEDICOESLUZ_DATA=
            "DELETE FROM " + MedicoesLuz.TABLE_NAME;

    protected static final String SQL_CREATE_ALERTASGLOBAIS=
            "CREATE TABLE " + AlertasGlobais.TABLE_NAME +
                    " (" + AlertasGlobais.COLUMN_NAME_IDALERTA + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AlertasGlobais.COLUMN_NAME_DATAHORAMEDICAO + " TIMESTAMP," +
                    AlertasGlobais.COLUMN_NAME_NOMEVARIAVEL + " TEXT, " +
                    AlertasGlobais.COLUMN_NAME_Limite_Inferior + " DOUBLE," +
                    AlertasGlobais.COLUMN_NAME_Limite_Superior + " DOUBLE," +
                    AlertasGlobais.COLUMN_NAME_VALORMEDICAO + " DOUBLE," +
                    AlertasGlobais.COLUMN_NAME_DESCRICAO + " TEXT," +
                    AlertasGlobais.COLUMN_NAME_INTENSIDADE + " TEXT )";

    protected static final String SQL_CREATE_ALERTASVARIAVEL=
            "CREATE TABLE " + AlertasVariavel.TABLE_NAME +
                    " (" + AlertasVariavel.COLUMN_NAME_IDALERTA + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AlertasVariavel.COLUMN_NAME_NOMEVARIAVEL + " TEXT, " +
                    AlertasVariavel.COLUMN_NAME_DATAHORAALERTA + " TIMESTAMP," +
                    AlertasGlobais.COLUMN_NAME_DESCRICAO + " TEXT," +
                    AlertasVariavel.COLUMN_NAME_INTENSIDADE + " TEXT," +
                    AlertasVariavel.COLUMN_NAME_DATAHORAMEDICAO + " TIMESTAMP," +
                    AlertasVariavel.COLUMN_NAME_VALORMEDICAO + " DOUBLE," +
                    AlertasVariavel.COLUMN_NAME_Limite_Inferior + " DOUBLE," +
                    AlertasVariavel.COLUMN_NAME_Limite_Superior + " DOUBLE)";


    protected static final String SQL_DELETE_ALERTASGLOBAIS_DATA=
            "DELETE FROM " + AlertasGlobais.TABLE_NAME;

    protected static final String SQL_DELETE_ALERTASVARIAVEL_DATA=
            "DELETE FROM " + AlertasVariavel.TABLE_NAME;

    protected static final String SQL_CREATE_AVAILABLEIDS=
            "CREATE TABLE " + AvailableIds.TABLE_NAME +
                    " (" + AvailableIds.COLUMN_NAME_IDCULTURA + " INTEGER PRIMARY KEY )";

    protected static final String SQL_DELETE_AVAILABLEIDS=
            "DELETE FROM " + AvailableIds.TABLE_NAME;

}
