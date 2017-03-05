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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/26/17.
 *
 * @since 1.0.0
 */

public class GameBoardFragment extends BaseFragment implements GameBoardContract.GameView {

  private static final String ARG_USER_SIDE = "MXMO_USER_SIDE";
  private static final String ARG_GAME_STATE = "MXMO_GAME_STATE";

  public static GameBoardFragment newInstance(@NonNull Boolean side, ArrayList<String> state) {
    GameBoardFragment fragment = new GameBoardFragment();
    Bundle args = new Bundle();
    args.putBoolean(ARG_USER_SIDE, side);
    args.putStringArrayList(ARG_GAME_STATE, state);
    fragment.setArguments(args);
    return fragment;
  }

  GameBoardContract.GamePresenter presenter;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  BoardAdapter adapter;

  Boolean side;
  ArrayList<String> gameState;

  Callback callback;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (getTargetFragment() instanceof Callback) {
      this.callback = (Callback) getTargetFragment();
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      side = getArguments().getBoolean(ARG_USER_SIDE);
      ArrayList<String> temp = getArguments().getStringArrayList(ARG_GAME_STATE);
      //noinspection ConstantConditions
      gameState = new ArrayList<>(temp);
    }

    presenter = new GamePresenterImpl();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

    adapter = new BoardAdapter(this.side, this.gameState);
    adapter.setItemClickListener(new BoardAdapter.ItemClickHandler() {
      @Override void onChecked(View view, int pos) {
        if (callback != null) {
          callback.onUserMove(adapter.getGameState());
        }
      }

      @Override boolean cellCheckable(View view, int pos) {
        return callback != null && callback.isMyTurnNow();
      }
    });

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);

    adapter.setCursorPosition(4, false);
    presenter.setView(this);
    presenter.loadGameDB();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_game_board, container, false);
  }

  public void syncBoard(List<String> boardToSync, boolean userInput) {
    boolean change = false;
    for (int i = 0; i < gameState.size(); i++) {
      if (!gameState.get(i).equals(boardToSync.get(i))) {
        gameState.set(i, boardToSync.get(i));
        change = true;
      }
    }

    if (change) {
      adapter.notifyDataSetChanged();
    }
  }

  public void moveCursorPosition() {
    adapter.setCursorPosition(adapter.getNextCursorPosition(), true);
  }

  public void checkCursor() {
    // only check when user can do this
    if (callback != null && callback.isMyTurnNow()) {
      adapter.manuallyCheck(adapter.getCursorPosition());
    }
  }

  public interface Callback {

    void onUserMove(List<String> gameState);

    boolean isMyTurnNow();
  }
}
