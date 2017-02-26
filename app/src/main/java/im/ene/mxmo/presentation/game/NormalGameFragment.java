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
import android.text.TextUtils;
import android.view.View;
import im.ene.mxmo.MemeApp;
import java.util.UUID;

/**
 * Created by eneim on 2/26/17.
 */

public class NormalGameFragment extends GameFragment {

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    overlayView.setVisibility(View.GONE);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // TODO ask for username
    if (TextUtils.isEmpty(MemeApp.getApp().getUserName())) {
      MemeApp.getApp().setUserName("normal_user_" + UUID.randomUUID().toString());
    }

    setupEventBus(MemeApp.getApp().getUserName());
    findGameOrCreateNew();
  }
}
