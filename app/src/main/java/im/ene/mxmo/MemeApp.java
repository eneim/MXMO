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
import com.jins_jp.meme.MemeLib;
import im.ene.mxmo.presentation.game.GameMode;

/**
 * Created by eneim on 2/13/17.
 */

public class MemeApp extends Application {

  // begin pref key

  // either '0' for normal or '1' for 'meme'
  public static final String KEY_GAME_MODE = "mxmo_game_mode";

  // end pref key

  public static final String TAG = "MemeApp";

  static final String APP_ID = "553494787374184";
  static final String APP_SECRET = "li6cou50dj3cvie17kovm4gh2g1fb0xt";

  private static MemeApp app;

  @Override public void onCreate() {
    super.onCreate();
    app = this;

    preferences().edit().putInt(KEY_GAME_MODE, GameMode.MODE_NORMAL).apply();

    MemeLib.setAppClientID(this, APP_ID, APP_SECRET);
    MemeLib.getInstance().setAutoConnect(false);
  }

  public static SharedPreferences preferences() {
    return app.getSharedPreferences(app.getPackageName() + "__meme", Context.MODE_PRIVATE);
  }
}
