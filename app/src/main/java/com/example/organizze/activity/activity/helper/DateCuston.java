package com.example.organizze.activity.activity.helper;

import java.text.SimpleDateFormat;

public class DateCuston {

    public static String dataAtual(){

        long data =  System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");
        String dataString = simpleDateFormat.format(data);

        return dataString;

    }

    public static String mesAnoDataEscolhida(String data){

        String retornoDat[] =  data.split("/");
        String dia = retornoDat[0];
        String mes = retornoDat[1];
        String ano = retornoDat[2];

        String mesAno = mes + ano;
        return  mesAno;

    }
}
