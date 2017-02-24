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

package im.ene.mxmo.presentation.trial;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;
import com.jins_jp.meme.MemeResponse;
import com.jins_jp.meme.MemeResponseListener;
import com.jins_jp.meme.MemeStatus;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseActivity;
import im.ene.mxmo.domain.model.GyroCalibrator;
import im.ene.mxmo.library.Action;
import im.ene.mxmo.library.Command;
import im.ene.mxmo.library.GyroData;
import im.ene.mxmo.library.MemeActionFilter;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static im.ene.mxmo.MemeApp.TAG;

/**
 * Created by eneim on 2/13/17.
 */

public class MemeDataActivity extends BaseActivity
    implements MemeConnectListener, MemeResponseListener, MemeRealtimeListener,
    MemeActionFilter.OnEyeActionListener, MemeActionFilter.OnHeadActionListener {

  MemeLib memeLib;
  final GyroCalibrator calibrator = new GyroCalibrator();
  MemeActionFilter actionFilter;

  @BindView(R.id.start_scan) ImageButton startScan;
  @BindView(R.id.action_head) TextView actionHead;
  @BindView(R.id.action_eye) TextView actionEye;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  BoardAdapter adapter;
  GridLayoutManager layoutManager;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    maybeRequestLocationPermission();
    memeLib = MemeLib.getInstance();

    layoutManager = new GridLayoutManager(this, 4);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new BoardAdapter();
    recyclerView.setAdapter(adapter);

    adapter.setSelectedCell(0);
    adapter.setCursorCell(0);

    startScan.setOnClickListener(this::startScan);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (memeLib.isScanning()) {
      memeLib.stopScan();
    }

    if (memeLib.isDataReceiving()) {
      memeLib.stopDataReport();
    }

    if (memeLib.isConnected()) {
      memeLib.disconnect();
      memeLib = null;
    }
  }

  AlertDialog memesDialog;
  final List<String> memes = new ArrayList<>();
  ArrayAdapter<String> memesListAdapter;

  public void startScan(View view) {
    memeLib.setMemeConnectListener(this);
    MemeStatus status = memeLib.startScan(memeId -> {
      if (memesDialog == null) {
        memesListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, memes);
        memesDialog = new AlertDialog.Builder(this).setTitle("Memes found!!!")
            .setAdapter(memesListAdapter, (dialog, which) -> {
              Log.i(TAG, "memeFoundCallback: " + memeLib.connect(memeId));
              if (memeLib.isScanning()) {
                memeLib.stopScan();
                Log.d("SCAN", "scan stopped.");
              }
            })
            .setNeutralButton("Finish", (dialog, which) -> {
              dialog.dismiss();
              MemeDataActivity.this.finish();
            })
            .setCancelable(false)
            .create();
        memesDialog.show();
      }

      Log.d("SCAN", "found: " + memeId);
      memes.add(memeId);
      memesListAdapter.notifyDataSetChanged();
    });

    Log.i(TAG, "startScan: " + status);
  }

  @Override public void memeConnectCallback(boolean connected) {
    Log.d(TAG, "memeConnectCallback() called with: b = [" + connected + "]");
    if (connected) {
      Log.i(TAG, "memeLib.isCalibrated(): " + memeLib.isCalibrated());
      memeLib.setResponseListener(this);
      memeLib.startDataReport(this);
    }
  }

  @Override public void memeDisconnectCallback() {

  }

  @Override public void memeResponseCallback(MemeResponse memeResponse) {

  }

  @Override public void memeRealtimeCallback(MemeRealtimeData data) {
    if (calibrator.getStartTime() == null) {
      calibrator.setStartTime(SystemClock.elapsedRealtime());
    }

    if (calibrator.getStartTime() + 10 * 1000 > SystemClock.elapsedRealtime()) {
      synchronized (calibrator) {
        calibrator.add(new GyroData(data.getPitch(), data.getRoll(), data.getYaw()));
      }

      return;
    }

    if (actionFilter == null) {
      actionFilter = new MemeActionFilter(calibrator.getCalibrated());
      actionFilter.addOnEyeActionListener(this);
      actionFilter.addOnHeadActionListener(this);
    }

    Flowable.just(data)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(memeData -> actionFilter.onNewData(memeData));
  }

  @Override public void onEyeAction(Command action) {
    if (action.getAction() != Action.IDLE) {
      actionEye.setText(action.toString());

      cursor.set(adapter.getCursorCell());
      switch (action.getAction()) {
        //case ROLL_LEFT:
        //  if (cursor.decrementAndGet() < 0) {
        //    cursor.set(0);
        //  }
        //
        //  adapter.setCursorCell(cursor.get());
        //  break;

        // TODO implement the following logic

        // if GAME MODE --> RIGHT: move cursor, LEFT: do nothing
        // if COMMENT MODE --> RIGHT: open dialog, select emoji, also check the command right before this, if LEFT --> do nothing (案)
        // LEFT: go to OK button or Cancel, but not enter, count down to OK or Cancel. (also check command before this?)

        // switch between GAME MODE and COMMENT MODE:
        // YAW-LEFT : GAME MODE if on turn,
        // YAW-RIGHT: COMMENT MODE

        // TODO Try Bridge App

        case EYE_TURN_RIGHT:
          cursor.incrementAndGet();
          adapter.setCursorCell(cursor.get());
          if (layoutManager.findLastVisibleItemPosition() < cursor.get()
              || layoutManager.findFirstVisibleItemPosition() > cursor.get()) {
            recyclerView.smoothScrollToPosition(cursor.get());
          }
          break;
        case EYE_TURN_UP:
          if (cursor.get() >= 0) {
            adapter.setSelectedCell(cursor.get());
          }
          break;
        default:
          break;
      }
    }
  }

  AtomicInteger cursor = new AtomicInteger();

  @Override public void onHeadAction(Command action) {
    if (action.getAction() != Action.IDLE) {
      Log.d(TAG, "onHeadAction() called with: action = [" + action + "]");
      actionHead.setText(action.toString());
    }
  }
}
