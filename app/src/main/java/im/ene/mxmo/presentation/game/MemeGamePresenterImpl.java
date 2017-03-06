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
import android.bluetooth.BluetoothAdapter;
import android.os.SystemClock;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeResponse;
import com.jins_jp.meme.MemeResponseListener;
import com.jins_jp.meme.MemeScanListener;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.common.RxBus;
import im.ene.mxmo.common.event.BluetoothConnectionEvent;
import im.ene.mxmo.domain.model.GyroCalibrator;
import im.ene.mxmo.library.Action;
import im.ene.mxmo.library.Command;
import im.ene.mxmo.library.GyroData;
import im.ene.mxmo.library.MemeActionFilter;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by eneim on 2/24/17.
 */
class MemeGamePresenterImpl extends GamePresenterImpl implements GameContract.MemeGamePresenter {

  private static final String TAG = "MXMO:MemeGamePresenter";

  GameContract.MemeGameView view;
  MemeLib memeLib;
  String memeId = null; // changed after connected

  MemeGamePresenterImpl(DatabaseReference gameDb) {
    super(gameDb);
  }

  @Override public void setView(GameContract.GameView view) {
    super.setView(view);
    this.view = (GameContract.MemeGameView) view;
    if (view != null) {
      memeLib = MemeLib.getInstance();
      memeLib.setResponseListener(new MemeResponseListenerImpl());

      disposables.add(RxBus.getBus()
          .observe(BluetoothConnectionEvent.class)
          .observeOn(AndroidSchedulers.mainThread())
          .filter(__ -> this.view != null)
          .subscribe(event -> this.view.onBluetoothState(Math.max(event.state, event.prevState))));
    } else {
      memeLib.stopDataReport();
      memeLib = null;
      if (actionFilter != null) {
        actionFilter.removeOnEyeActionListener(null);
        actionFilter.removeOnHeadActionListener(null);
      }
    }
  }

  @Override public void enableBluetoothIfNeed(Activity activity, boolean expected) {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (Boolean.compare(expected, bluetoothAdapter.isEnabled()) != 0) {
      if (!bluetoothAdapter.isEnabled()) {
        bluetoothAdapter.enable();
      } else {
        bluetoothAdapter.disable();
      }
    } else if (expected && bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
      RxBus.getBus()
          .send(new BluetoothConnectionEvent(BluetoothAdapter.STATE_ON, BluetoothAdapter.STATE_ON));
    }
  }

  @Override public void startScanForMeme() {
    memeLib.startScan(new MemeScanListenerImpl());
  }

  @Override public boolean isScanningForMeme() {
    return memeLib.isScanning();
  }

  @Override public void stopScanForMeme() {
    memeLib.stopScan();
  }

  @Override public void connectToMeme(String memeId) {
    this.memeId = memeId;
    memeLib.setMemeConnectListener(new MemeConnectListenerImpl() {
      @Override public void memeDisconnectCallback() {
        MemeGamePresenterImpl.this.memeId = null;
      }
    });
    memeLib.connect(memeId);
  }

  @Override public void disconnectFromMeme() {
    memeLib.disconnect();
  }

  @Override public void initGame() {
    if (view == null) {
      throw new NullPointerException("View is null!!");
    }

    // start listen to meme data to calibrated
    GyroData gyroData = MemeApp.getApp().getCalibratedGyroDta();
    if (gyroData == null) {
      view.showCalibrateDialog(true);
      final GyroCalibrator calibrator = new GyroCalibrator();
      memeLib.startDataReport(data -> {
        if (calibrator.getStartTime() == null) {
          calibrator.setStartTime(SystemClock.elapsedRealtime());
        }

        if (calibrator.getStartTime() + 10 * 1000 > SystemClock.elapsedRealtime()) {
          synchronized (calibrator) {
            calibrator.add(new GyroData(data.getPitch(), data.getRoll(), data.getYaw()));
          }

          return;
        }

        MemeApp.getApp().saveCalibratedGyroData(calibrator.getCalibrated());
        memeLib.stopDataReport();
        if (!gameInitCalled) {
          initMemeGame();
        }
      });
    } else {
      if (!gameInitCalled) {
        initMemeGame();
      }
    }
  }

  // dirty trick...
  private boolean gameInitCalled = false;

  @SuppressWarnings("WeakerAccess") void initMemeGame() {
    gameInitCalled = true;
    Single.just(view != null && memeId != null)
        .filter(Boolean.TRUE::equals)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(aBoolean -> {
          view.showCalibrateDialog(false);
          view.showHideOverLay(true);
          view.showUserNameInputDialog("meme_user_" + memeId);
        });
  }

  private MemeActionFilter actionFilter;

  @Override protected void onGameAbleToStart() {
    actionFilter = new MemeActionFilter(MemeApp.getApp().getCalibratedGyroDta());
    actionFilter.addOnEyeActionListener(action -> Flowable.just(action)
        .filter(command -> command.getAction() != Action.IDLE)
        .subscribe(action1 -> {
          Log.w(TAG, "onEyeAction() called with: action = [" + action1 + "]");
          if (view != null) {
            if (view.getCurrentMode() == GameContract.MemeGameView.MODE_CHAT) {
              handleActionInChatMode(action1);
            } else if (view.getCurrentMode() == GameContract.MemeGameView.MODE_GAME) {
              handleActionInGameMode(action1);
            }
          }
        }));

    actionFilter.addOnHeadActionListener(action -> Flowable.just(action)
        .filter(command -> command.getAction() != Action.IDLE && view != null)
        .subscribe(action1 -> {
          Log.i(TAG, "onHeadAction() called with: action = [" + action1 + "]");
          switch (action1.getAction()) {
            case YAW_LEFT:
              if (view.getCurrentMode() != GameContract.MemeGameView.MODE_GAME) {
                view.setCurrentMode(GameContract.MemeGameView.MODE_GAME);
              } else {
                handleActionInGameMode(action1);
              }
              break;
            case YAW_RIGHT:
              if (view.getCurrentMode() != GameContract.MemeGameView.MODE_CHAT) {
                view.setCurrentMode(GameContract.MemeGameView.MODE_CHAT);
              } else {
                handleActionInChatMode(action1);
              }
              break;
            default:
              if (view.getCurrentMode() == GameContract.MemeGameView.MODE_CHAT) {
                handleActionInChatMode(action1);
              } else if (view.getCurrentMode() == GameContract.MemeGameView.MODE_GAME) {
                handleActionInGameMode(action1);
              }
              break;
          }
        }));

    memeLib.startDataReport(data -> Flowable.just(data)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(actionFilter::onNewData));

    super.onGameAbleToStart();
  }

  @Override public void endGame() {
    if (actionFilter != null) {
      actionFilter.removeOnEyeActionListener(null);
      actionFilter.removeOnHeadActionListener(null);
    }
    memeLib.stopDataReport();
    super.endGame();
  }

  private void handleActionInGameMode(Command action) {
    switch (action.getAction()) {
      case EYE_TURN_RIGHT:
      case ROLL_RIGHT:  // backup action
        // move cursor + 1;
        view.moveCursorPosition();
        break;
      case YAW_LEFT:
      case PITCH_BACKWARD:  // backup action
        view.checkCursor();
        break;
      // do nothing
      case EYE_TURN_LEFT:
      default:
        break;
    }
  }

  private void handleActionInChatMode(Command action) {
    // RIGHT: open dialog, select emoji, also check the command right before this, if LEFT --> do nothing (案)
    // LEFT: go to OK button or Cancel, but not enter, count down to OK or Cancel. (also check command before this?)
    switch (action.getAction()) {
      case EYE_TURN_RIGHT:
      case ROLL_RIGHT:  // backup action
        view.prepareEmojiSelectDialog(dialog -> {
          view.nextEmoji(); // select one;
        });
        view.nextEmoji();
        break;
      case YAW_RIGHT:
      case PITCH_FORWARD: // backup action
        view.selectEmojiAndSend(true);
        break;
      default:
        break;
    }
  }

  // Meme callback
  private class MemeScanListenerImpl implements MemeScanListener {

    MemeScanListenerImpl() {
    }

    @Override public void memeFoundCallback(String memeId) {
      if (view != null) {
        view.onMemeScanned(memeId);
      }
    }
  }

  private class MemeConnectListenerImpl implements MemeConnectListener {

    MemeConnectListenerImpl() {
    }

    @Override public void memeConnectCallback(boolean connected) {
      if (view != null) {
        view.onMemeConnected(connected);
      }
    }

    @Override public void memeDisconnectCallback() {
      // Do nothing
    }
  }

  private class MemeResponseListenerImpl implements MemeResponseListener {

    MemeResponseListenerImpl() {
    }

    @Override public void memeResponseCallback(MemeResponse memeResponse) {
      // Do nothing
    }
  }
}
