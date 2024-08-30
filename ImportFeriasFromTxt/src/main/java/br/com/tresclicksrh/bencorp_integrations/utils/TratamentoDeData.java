package br.com.tresclicksrh.bencorp_integrations.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.DateTimeFormatter;

public class TratamentoDeData {

    public static LocalDate parseDate(String pDate) throws ParseException {
        return LocalDate.parse(pDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        /*
        String data[] = pDate.split("/");
        LocalDate ld = LocalDate.of(Integer.parseInt(data[2]),Integer.parseInt(data[1]),Integer.parseInt(data[0]));
        return ld;
        */
    }

    public static String getSqlDate(LocalDate data) {
        String str = data.getYear() + "-"+data.getMonthValue()+"-"+data.getDayOfMonth();
        return str;
    }

    public static LocalDate somaDias(LocalDate data, int qtdDias) throws ParseException {
        data = data.plusDays(qtdDias);
        return data;

    }
}
