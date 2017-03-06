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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import im.ene.mxmo.common.BaseActivity;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/24/17.
 *
 * @since 1.0.0
 */

public class GameActivity extends BaseActivity {

  AlertDialog welcomeDialog;
  AlertDialog userModeChooserDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    maybeRequestLocationPermission();
  }

  @Override protected void onPermissionGranted(String permission, boolean granted) {
    super.onPermissionGranted(permission, true);
    if (!granted) {
      finish();
      return;
    }

    int userMode = getApp().getGameMode();
    userModeChooserDialog = new AlertDialog.Builder(this).setTitle("Select user mode:")
        .setSingleChoiceItems(new CharSequence[] { "Normal User", "Meme User" }, userMode,
            (dialog, which) -> getApp().setGameMode(which))
        .setPositiveButton(android.R.string.ok, (dialog, which) -> //
            getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, GameFragment.newInstance(getApp().getGameMode()))
                .commit())
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> finish())
        .setCancelable(false)
        .create();

    welcomeDialog = new AlertDialog.Builder(this).setTitle("Welcome to MxMo!")
        .setMessage("Please choose your mode (Meme User will require a Jins Meme to play)")
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          if (!userModeChooserDialog.isShowing()) {
            userModeChooserDialog.show();
          }
        })
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> finish())
        .setCancelable(false)
        .create();

    welcomeDialog.show();
  }
}
