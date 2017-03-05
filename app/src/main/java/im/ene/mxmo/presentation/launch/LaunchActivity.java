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

package im.ene.mxmo.presentation.launch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseActivity;
import im.ene.mxmo.presentation.game.GameActivity;

/**
 * Created by eneim on 3/6/17.
 */

public class LaunchActivity extends BaseActivity {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_launch);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.start_game) void startGame() {
    startActivity(new Intent(this, GameActivity.class));
  }

  @OnClick(R.id.reset_gyro_data) void resetGyroData() {
    MemeApp.getApp().saveCalibratedGyroData(null);
    Toast.makeText(this, "DONE!", Toast.LENGTH_SHORT).show();
  }

  @OnClick(R.id.reset_user_name) void resetUserName() {
    MemeApp.getApp().setUserName(null);
    Toast.makeText(this, "DONE!", Toast.LENGTH_SHORT).show();
  }
}
