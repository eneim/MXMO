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
import android.content.Context;
import android.support.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import im.ene.mxmo.domain.model.TicTacToe;

/**
 * Created by eneim on 2/24/17.
 */

public class GameContract {

  interface GameView {

    void setupUserName(String defaultUserName);

    Context getViewContext();

    void setupEventBus(@NonNull String userName);

    void letTheGameBegin();
  }

  interface Presenter {

    void setView(GameView view);

    void setupGameUser();

    void joinGameOrCreateNew();

    void createGame(String userName);

    void joinGame(@NonNull String userName);

    void setGame(TicTacToe game, DatabaseReference gameRef);

    void onGameAbleToStart();
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
