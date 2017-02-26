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

package im.ene.mxmo.presentation.game.board;

import android.os.Bundle;
import android.support.annotation.Nullable;
import im.ene.mxmo.common.BaseFragment;

/**
 * Created by eneim on 2/26/17.
 */

public class GameBoardFragment extends BaseFragment implements GameBoardContract.GameView {

  public static GameBoardFragment newInstance() {
    Bundle args = new Bundle();
    GameBoardFragment fragment = new GameBoardFragment();
    fragment.setArguments(args);
    return fragment;
  }

  GameBoardContract.GamePresenter presenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new GamePresenterImpl();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    presenter.setView(this);
    presenter.loadGameDB();
  }
}
