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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.FirebaseDatabase;
import im.ene.mxmo.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/26/17.
 *
 * @since 1.0.0
 */
public class MemeGameFragment extends GameFragment implements GameContract.MemeGameView {

  private GameContract.MemeGamePresenter presenter;

  @NonNull @Override protected GameContract.Presenter getPresenter() {
    if (presenter == null) {
      presenter = new MemeGamePresenterImpl(FirebaseDatabase.getInstance().getReference("games"));
    }
    return presenter;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getApp().initMemeLib();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    presenter.setView(this);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.enableBluetoothIfNeed(getActivity(), true);
  }

  @Override public void onStop() {
    super.onStop();
    presenter.enableBluetoothIfNeed(getActivity(), false);
  }

  @Override public void onBluetoothState(int state) {
    if (state == BluetoothAdapter.STATE_ON) {
      new AlertDialog.Builder(getContext()).setMessage("Start scanning for Meme?")
          .setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (!presenter.isScanningForMeme()) {
              presenter.startScanForMeme();
            }
          })
          .setNegativeButton(android.R.string.cancel, (dialog, which) -> getActivity().finish())
          .create()
          .show();
    } else {
      if (presenter.isScanningForMeme()) {
        presenter.stopScanForMeme();
      }
    }
  }

  AlertDialog memesDialog;
  final List<String> memes = new ArrayList<>();
  ArrayAdapter<String> memesListAdapter;

  @Override public void onMemeScanned(String memeId) {
    if (memesDialog == null) {
      memesListAdapter =
          new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, memes);
      memesDialog = new AlertDialog.Builder(getContext()).setTitle("Memes found!!!")
          .setAdapter(memesListAdapter,
              (dialog, which) -> presenter.connectToMeme(memes.get(which)))
          .setNeutralButton("Finish", (dialog, which) -> {
            dialog.dismiss();
            getActivity().finish();
          })
          .setOnDismissListener(dialog -> {
            if (presenter.isScanningForMeme()) {
              presenter.stopScanForMeme();
            }
          })
          .setCancelable(false)
          .create();
      memesDialog.show();
    }

    memes.add(memeId);
    memesListAdapter.notifyDataSetChanged();
  }

  private static final String TAG = "MemeGameFragment";

  @Override public void onMemeConnected(boolean connected) {
    Log.d(TAG, "onMemeConnected() called with: connected = [" + connected + "]");
    getActivity().runOnUiThread(() -> presenter.initGame());
  }

  AlertDialog calibrateTimerDialog;

  @Override public void showCalibrateDialog(boolean willShow) {
    if (willShow) {
      if (calibrateTimerDialog == null) {
        int waitLength = 12;
        @SuppressLint("InflateParams") TextView timer = (TextView) LayoutInflater.from(getContext())
            .inflate(R.layout.widget_text_countdown, null);
        Disposable disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> timer.setText("ETA: " + (waitLength - aLong) + " second(s)."));

        calibrateTimerDialog = new AlertDialog.Builder(getContext()).setCancelable(false)
            .setTitle("Calibrating your Meme, please wait.")
            .setView(timer)
            .setOnDismissListener(dialog -> disposable.dispose())
            .create();
      }

      if (!calibrateTimerDialog.isShowing()) {
        calibrateTimerDialog.show();
      }
    } else {
      if (calibrateTimerDialog != null && calibrateTimerDialog.isShowing()) {
        calibrateTimerDialog.dismiss();
      }
    }
  }

  private int mode = -1;  // init value

  @Override public int getCurrentMode() {
    return mode;
  }

  @Override public void setCurrentMode(int mode) {
    this.mode = mode;
    if (mode == MODE_GAME) {
      chatFragment.hideEmojiDialog();
    }
    
    Toast.makeText(getContext(), "MODE: " + mode, Toast.LENGTH_SHORT).show();
  }

  @Override public void moveCursorPosition() {
    boardFragment.moveCursorPosition();
  }

  @Override public void prepareEmojiSelectDialog() {
    chatFragment.showEmojiDialog();
  }
}
