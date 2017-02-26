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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import im.ene.mxmo.presentation.game.board.GameBoardFragment;
import im.ene.mxmo.presentation.game.chat.GameChatFragment;

/**
 * Created by eneim on 2/26/17.
 */

public abstract class GameFragment extends BaseFragment {

  public static GameFragment newInstance(int gameMode) {
    GameFragment fragment;
    switch (gameMode) {
      case GameMode.MODE_MEME:
        fragment = new MemeGameFragment();
        break;
      case GameMode.MODE_NORMAL:
      default:
        fragment = new NormalGameFragment();
        break;
    }

    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  protected @BindView(R.id.overlay) View overlayView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getChildFragmentManager().beginTransaction()
        .replace(R.id.game_board, GameBoardFragment.newInstance())
        .replace(R.id.game_chat, GameChatFragment.newInstance())
        .commit();
  }
}
