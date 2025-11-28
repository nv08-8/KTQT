//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public class SessionManager {
    private static final String PREFS_NAME = "bookshop_prefs";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_USER_JSON = "key_user_json";

    private final SharedPreferences prefs;
    private final Gson gson;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUser(Map<String, Object> user) {
        if (user.containsKey("token")) {
            saveToken((String) user.get("token"));
        }
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER_JSON, userJson).apply();
    }

    public Map<String, Object> getUser() {
        String userJson = prefs.getString(KEY_USER_JSON, null);
        if (userJson == null) {
            return null;
        }
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        return gson.fromJson(userJson, type);
    }

    public boolean isLoggedIn() {
        return getToken() != null && !getToken().isEmpty();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
