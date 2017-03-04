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

package im.ene.mxmo.presentation.game;

import android.app.Activity;

/**
 * Created by eneim on 2/24/17.
 */

class GameContract {

  interface GameView {

    void showHideOverLay(boolean willShow);

    void showUserNameInputDialog(String defaultUserName);

    void showWaitForSecondUserDialog();

    void letTheGameBegin();
  }

  interface Presenter {

    void setView(GameView view);

    void initGame();

    void onUserName(String userName);
  }

  interface MemeGameView extends GameView {

    void onBluetoothState(int state);

    void onMemeScanned(String id);

    void onMemeConnected(boolean connected);
  }

  interface MemeGamePresenter extends Presenter {

    void enableBluetoothIfNeed(Activity activity, boolean expected);

    void startScanForMeme();

    boolean isScanningForMeme();

    void stopScanForMeme();

    void connectToMeme(String memeId);

    void disconnectFromMeme();
  }
}
