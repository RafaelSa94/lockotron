package com.lockotron.mobi_o_tron.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lockotron.mobi_o_tron.model.Historico;
import com.lockotron.mobi_o_tron.model.Usuario;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Statistics {
    private static final int MOST_FREQUENT = 0;
    private static final int LESS_FREQUENT = 1;

    private static boolean isNative(Context context) {
        final String PREF_NATIVE = "use_native";
        boolean result = false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(PREF_NATIVE)) {
            result = prefs.getBoolean(PREF_NATIVE, false);
        }
        return result;
    }

    public static com.lockotron.mobi_o_tron.model.Usuario mostFrequentUser(Context context, List<Historico> log){
        int userId;
        if (log.size() == 0)
            return null;
        com.lockotron.mobi_o_tron.model.Usuario user = log.get(0).getUsuario();
        int[] idArray = new int[log.size()];

        for (int i = 0; i < log.size(); i++) {
            idArray[i] = log.get(i).getUsuario().getId();
        }

        if (isNative(context))
            userId = Native.getMode(idArray);
        else
            userId = getMode(idArray);

        for (int i = 0; i < log.size(); i++) {
            if (log.get(i).getId() == userId) {
                user = log.get(i).getUsuario();
                break;
            }
        }

        return user;
    }

    @Nullable
    public static Usuario lessFrequentUser(Context context, List<Historico> log) {
        int userId;
        if (log.size() == 0)
            return null;
        com.lockotron.mobi_o_tron.model.Usuario user = log.get(0).getUsuario();
        int[] idArray = new int[log.size()];

        for (int i = 0; i < log.size(); i++) {
            idArray[i] = log.get(i).getUsuario().getId();
        }

        if (isNative(context))
            userId = Native.lessRepeated(idArray);
        else
            userId = lessRepeated(idArray);

        for (int i = 0; i < log.size(); i++) {
            if (log.get(i).getId() == userId) {
                user = log.get(i).getUsuario();
                break;
            }
        }

        return user;
    }

    @Nullable
    public static String mostFrequentTime(Context context, List<Historico> log, int userId){
        return timeStats(MOST_FREQUENT, context, log, userId);
    }

    @Nullable
    public static String lessFrequentTime(Context context, List<Historico> log, int userId){
        return timeStats(LESS_FREQUENT, context, log, userId);
    }

    @Nullable
    public static String mostFrequentTime(Context context, List<Historico> log) {
        return timeStats(MOST_FREQUENT, context, log);
    }

    @Nullable
    public static String lessFrequentTime(Context context, List<Historico> log) {
        return timeStats(LESS_FREQUENT, context, log);
    }

    @Nullable
    private static String timeStats(int function, Context context, List<Historico> log, int userId){
        List<Historico> newList = new ArrayList<>();

        for (Historico l : log) {
            if (l.getUsuario().getId() == userId) {
                newList.add(l);
            }
        }

        return timeStats(function, context, newList);
    }

    @Nullable
    private static String timeStats(int function, Context context, List<Historico> log) {
        if (log.size() == 0)
            return null;

        int[] hours = new int[log.size()];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        for (int i = 0; i < log.size(); i++) {
            try {
                hours[i] = dateFormat.parse(log.get(i).getData()).getHours();
            } catch (ParseException e) {
                hours[i] = -1;
            }
        }

        int time = -1;
        if (isNative(context))
            switch (function) {
                case MOST_FREQUENT:
                    time = Native.getMode(hours);
                    break;
                case LESS_FREQUENT:
                    time = Native.lessRepeated(hours);
                    break;
            }
        else
            switch (function) {
                case MOST_FREQUENT:
                    time = getMode(hours);
                    break;
                case LESS_FREQUENT:
                    time = lessRepeated(hours);
                    break;
            }

        return String.format(Locale.getDefault(), "%02d:00 - %02d:00", time, time+1);
    }

    @Nullable
    public static String mostFrequentDay(Context context, List<Historico> log, int userId){
        List<Historico> newList = new ArrayList<>();

        for (Historico l : log) {
            if (l.getUsuario().getId() == userId) {
                newList.add(l);
            }
        }

        return mostFrequentDay(context, newList);
    }

    @Nullable
    public static String mostFrequentDay(Context context, List<Historico> log) {
        if (log.size() == 0)
            return null;

        int[] days = new int[log.size()];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        for (int i = 0; i < log.size(); i++) {
            try {
                calendar.setTime(dateFormat.parse(log.get(i).getData()));
                days[i] = calendar.get(Calendar.DAY_OF_WEEK);
            } catch (ParseException e) {
                days[i] = -1;
            }
        }

        int day;
        if (isNative(context))
            day = Native.getMode(days);
        else
            day = getMode(days);

        return day >= 0 && day <= 7 ? DateFormatSymbols.getInstance(Locale.getDefault()).getWeekdays()[day] : null;
    }
    
    public static void testPerformance() {
        final String TAG = "MOBI-O-TRON-PERFORMANCE";
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Long> times = new ArrayList<>();

        final int min  = 100;
        final int max  = 10000;
        final int step = 500;

        for (int i = min; i < max; i += step) {
            int[] values = new int[i];
            sizes.add(i);
            for (int j = 0; j < values.length; j++) {
                values[j] = new Random().nextInt(2*i/3);
            }
            long time = 0;
            for (int j = 0; j < 10; j++) {
                long nowTime = System.nanoTime();
                Native.getMode(values);
                nowTime = System.nanoTime() - nowTime;
                time += nowTime;
            }
            time /= 10;
            times.add(time);
        }
        Log.d(TAG, "Native: " + sizes.toString());
        Log.d(TAG, "Native: " + times.toString());

        sizes.clear();
        times.clear();

        for (int i = min; i < max; i += step) {
            int[] values = new int[i];
            sizes.add(i);
            for (int j = 0; j < values.length; j++) {
                values[j] = new Random().nextInt(2*i/3);
            }
            long time = 0;
            for (int j = 0; j < 10; j++) {
                long nowTime = System.nanoTime();
                getMode(values);
                nowTime = System.nanoTime() - nowTime;
                time += nowTime;
            }
            time /= 10;
            times.add(time);
        }
        Log.d(TAG, "Java: " + sizes.toString());
        Log.d(TAG, "Java: " + times.toString());
    }

    private static int lessRepeated(int[] values) {
        quickSort(values, 0, values.length-1);

        int menor = values[0];
        int valorAtual = values[0];
        int quantidadeMin = values.length;
        int quantidadeAtual = 1;

        for(int i = 1; i < values.length; i++){
            if (values[i] == valorAtual) {
                quantidadeAtual++;
            }
            else {
                if (quantidadeAtual <= quantidadeMin) {
                    quantidadeMin = quantidadeAtual;
                    menor = valorAtual;
                }
                valorAtual = values[i];
                quantidadeAtual = 1;
            }
        }
        return menor;
    }

    private static int getMode(int[] values) {
        quickSort(values, 0, values.length-1);

        int maior = 0;
        int valorAtual = values[0];
        int quantidadeMax = 0;
        int quantidadeAtual = 0;

        for (int userId : values) {
            if (userId == valorAtual) {
                quantidadeAtual++;
            } else {
                valorAtual = userId;
                quantidadeAtual = 1;
            }

            if (quantidadeAtual > quantidadeMax) {
                quantidadeMax = quantidadeAtual;
                maior = userId;
            }
        }
        return maior;
    }

    private static void quickSort(int[] vet, int esq, int dir) {
        int pivo = esq,i,ch,j;
        for(i=esq+1; i<=dir; i++){
            j = i;
            if(vet[j] < vet[pivo]){
                ch = vet[j];
                while(j > pivo){
                    vet[j] = vet[j-1];
                    j--;
                }
                vet[j] = ch;
                pivo++;
            }
        }
        if(pivo-1 > esq){
            quickSort(vet,esq,pivo-1);
        }
        if(pivo+1 < dir){
            quickSort(vet,pivo+1,dir);
        }
    }

    private static class Native {
        @SuppressWarnings("JniMissingFunction")
        public native static int getMode(int[] userIds);
        @SuppressWarnings("JniMissingFunction")
        public native static int lessRepeated(int[] userIds);
    }
}