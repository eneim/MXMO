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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by eneim on 2/26/17.
 */

public class NormalGameFragment extends GameFragment {

  private GameContract.Presenter presenter;

  @NonNull @Override protected GameContract.Presenter getPresenter() {
    if (presenter == null) {
      presenter = new GamePresenterImpl(FirebaseDatabase.getInstance().getReference("games"));
    }
    return presenter;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getPresenter();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    overlayView.setVisibility(View.GONE);
    presenter.setView(this);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    presenter.setupGameUser();
  }
}
