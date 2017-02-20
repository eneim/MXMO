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
import com.jins_jp.meme.MemeLib;

/**
 * Created by eneim on 2/13/17.
 */

public class MemeApp extends Application {

  public static final String TAG = "MemeApp";

  static final String APP_ID = "553494787374184";
  static final String APP_SECRET = "li6cou50dj3cvie17kovm4gh2g1fb0xt";

  @Override public void onCreate() {
    super.onCreate();
    MemeLib.setAppClientID(this, APP_ID, APP_SECRET);
    MemeLib.getInstance().setAutoConnect(false);
  }
}
