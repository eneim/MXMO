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
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;

/**
 * Created by eneim on 2/26/17.
 *
 * @since 1.0.0
 */

public class GameBoardFragment extends BaseFragment implements GameBoardContract.GameView {

  public static GameBoardFragment newInstance() {
    Bundle args = new Bundle();
    GameBoardFragment fragment = new GameBoardFragment();
    fragment.setArguments(args);
    return fragment;
  }

  GameBoardContract.GamePresenter presenter;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  BoardAdapter adapter;

  BoardStateChangeListener listener;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (getTargetFragment() instanceof BoardStateChangeListener) {
      this.listener = (BoardStateChangeListener) getTargetFragment();
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    presenter = new GamePresenterImpl();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
    SparseArray<String> gameState = new SparseArray<>(9);
    for (int i = 0; i < 9; i++) {
      gameState.put(i, null);
    }

    adapter = new BoardAdapter(gameState);
    adapter.setItemClickListener(new BoardAdapter.ItemClickHandler() {
      @Override void onChecked(View view, int pos) {
        if (listener != null) {
          listener.onBoardState(adapter.getGameState());
        }
      }
    });

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);

    adapter.setCursorPosition(4);
    presenter.setView(this);
    presenter.loadGameDB();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_game_board, container, false);
  }

  public interface BoardStateChangeListener {

    void onBoardState(SparseArray<String> gameState);
  }
}
