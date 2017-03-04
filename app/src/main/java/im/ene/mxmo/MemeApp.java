/*
 * Copyright (c) 2017 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.mxmo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jins_jp.meme.MemeLib;
import im.ene.mxmo.domain.model.TicTacToe;
import im.ene.mxmo.presentation.game.GameMode;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by eneim on 2/13/17.
 *
 * @since 1.0.0
 *
 * Game flow:
 *
 * 1. Start: ask for gamer mode: Normal User or Meme User.
 *
 * 1.1: If 'Normal user': prepare a Username based on UUID or Edit text, attach normal user view
 * and
 * init stuff from Firebase database.
 * 1.2: If 'Meme user': process to scan for meme, prepare Username based on MemeId or Edit text,
 * attache meme user view and init stuff from Firebase.
 */

public class MemeApp extends Application {

  public static final String INVALID = "?";

  /* begin pref key */
  // either '0' for normal or '1' for 'meme'
  public static final String KEY_GAME_MODE = "mxmo_game_mode";

  // save username for future use
  public static final String KEY_USER_NAME = "mxmo_user_name";
  /* end pref key */

  public static final String TAG = "MemeApp";

  static final String APP_ID = "553494787374184";
  static final String APP_SECRET = "li6cou50dj3cvie17kovm4gh2g1fb0xt";

  private static MemeApp app;

  @Override public void onCreate() {
    super.onCreate();
    app = this;

    preferences().edit().putInt(KEY_GAME_MODE, GameMode.MODE_NORMAL).apply();
    // FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
  }

  public static MemeApp getApp() {
    return app;
  }

  public final SharedPreferences preferences() {
    return app.getSharedPreferences(app.getPackageName() + "__meme", Context.MODE_PRIVATE);
  }

  public void initMemeLib() {
    MemeLib.setAppClientID(this, APP_ID, APP_SECRET);
    MemeLib.getInstance().setAutoConnect(false);
  }

  // members
  final Gson gson = new GsonBuilder().create();
  final Type hashMapType = new TypeToken<Map<String, Object>>() {
  }.getType();

  // String userName;

  public Gson getGson() {
    return gson;
  }

  public Map<String, Object> parseToHashMap(TicTacToe game) {
    return gson.fromJson(gson.toJson(game), hashMapType);
  }

  public void setGameMode(int mode) {
    preferences().edit().putInt(MemeApp.KEY_GAME_MODE, mode).apply();
  }

  public int getGameMode() {
    return preferences().getInt(KEY_GAME_MODE, GameMode.MODE_NORMAL);
  }

  @Nullable public String getUserName() {
    return preferences().getString(KEY_USER_NAME, null);
  }

  public void setUserName(String userName) {
    preferences().edit().putString(KEY_USER_NAME, userName).apply();
  }
}
