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

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/26/17.
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
    getPresenter();
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

  @Override public void onDestroyView() {
    super.onDestroyView();
    presenter.setView(null);
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

  @Override public void onMemeScanned(String id) {
    if (memesDialog == null) {
      memesListAdapter =
          new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, memes);
      memesDialog = new AlertDialog.Builder(getContext()).setTitle("Memes found!!!")
          .setAdapter(memesListAdapter, (dialog, which) -> {
            presenter.connectToMeme(memes.get(which));
          })
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
  }

  @Override public void onMemeConnected(boolean connected) {
    overlayView.setVisibility(View.GONE);
    presenter.setupGameUser();
  }
}
