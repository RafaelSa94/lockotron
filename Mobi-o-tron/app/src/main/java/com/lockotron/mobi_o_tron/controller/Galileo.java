package com.lockotron.mobi_o_tron.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lockotron.mobi_o_tron.exception.GalileoNotFoundException;
import com.lockotron.mobi_o_tron.exception.ServerNotSetException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Galileo {
    private static final String TAG = "MOBI-O-TRON";
    private static final String KEY_SERVER_ADDRESS = "server_address";
    public enum Command {PANIC, UPDATE, KILL}

    public static void openDoor(Context context) throws IOException, ServerNotSetException, GalileoNotFoundException {
        request(context, Command.PANIC);
    }

    public static void update(Context context) throws IOException, ServerNotSetException, GalileoNotFoundException {
        request(context, Command.UPDATE);
    }

    public static void kill(Context context) throws IOException, ServerNotSetException, GalileoNotFoundException {
        request(context, Command.KILL);
    }

    public static void request(Context context, Command command) throws ServerNotSetException, IOException, GalileoNotFoundException {
        assert context != null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(KEY_SERVER_ADDRESS) && !prefs.getString(KEY_SERVER_ADDRESS, "").equals("")) {
            String url = prefs.getString(KEY_SERVER_ADDRESS, "");
            String param = "";
            switch (command){
                case PANIC:
                    param = "panic";
                    break;
                case UPDATE:
                    param = "update";
                    break;
                case KILL:
                    param = "kill";
                    break;
            }
            Galileo.request(url + "/galileo.php?" + param);
        } else {
            throw new ServerNotSetException();
        }
    }

    private static boolean request(String server_addr) throws IOException, GalileoNotFoundException {

        URL url = new URL("http://" + server_addr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(10000);
        conn.connect();
        int status = conn.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                return true;
            case 500:
                throw new GalileoNotFoundException();
            default:
                return false;
        }
    }
}
