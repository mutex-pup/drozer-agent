package com.WithSecure.dz.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.WithSecure.dz.Agent;
import com.WithSecure.dz.R;
import com.WithSecure.jsolar.api.connectors.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;

public class GlobalSettings {
    private GlobalSettings(Context ctx) {
        // load settings from config file
        try {
            BufferedReader confFile = new BufferedReader(new InputStreamReader(
                    ctx.getResources().openRawResource(R.raw.config)));
            String line;

            while ((line = confFile.readLine()) != null) {
                String[] parts = line.split(":", 2);

                if (parts.length != 2) {
                    continue;
                }

                if (ensure(parts[0], parts[1])) {
                    // make additional changes on a per setting basis
                    switch (parts[0]) {
                        case "server-port":
                            try {
                                SharedPreferences.Editor e = Agent.getInstance().getSettings().edit();
                                e.putString(Server.SERVER_PORT, parts[1]);
                                e.commit();
                            } catch (NumberFormatException ignored) {
                                break;
                            }
                            break;
                        case "endpoint":
                            break;
                    }
                }
            }
        }
        catch (Resources.NotFoundException ignored) { }
        catch (IOException ignored) { }
    }

    private static boolean ensure(String key, String value) {
        SharedPreferences p = Agent.getInstance().getSettings();

        if (!p.contains(key)) {
            SharedPreferences.Editor e = p.edit();
            e.putString(key, value);
            e.commit();
            return true;
        }
        return false;
    }

    public static String get(String key) {
        return Agent.getInstance().getSettings().getString(key, null);
    }

    public static int themeFromString(String s) {
        if (s == null) {
            return R.style.AppTheme;
        }

        switch(s) {
            case "red":		return R.style.dzRed;
            case "green":	return R.style.dzGreen;
            case "purple":	return R.style.dzPurple;
            case "blue":	return R.style.dzBlue;
            default:		return R.style.AppTheme;
        }
    }

    public static void Init(Context ctx) {
        if (instance == null) {
            instance = new GlobalSettings(ctx);
        }
    }

    private static GlobalSettings instance = null;
    private Dictionary<String, String> data;
}
