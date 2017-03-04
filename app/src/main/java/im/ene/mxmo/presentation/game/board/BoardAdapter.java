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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.common.OnItemClickListener;
import java.util.List;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 3/4/17.
 *
 * @since 1.0.0
 */

class BoardAdapter extends RecyclerView.Adapter<BoardCellViewHolder> {

  private int cursorPosition = RecyclerView.NO_POSITION;  // move over by meme action

  final List<String> states;
  final Boolean side; // TRUE for FALSE;

  OnItemClickListener itemClickListener;

  @SuppressWarnings("WeakerAccess")
  public void setItemClickListener(OnItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  BoardAdapter(Boolean side, List<String> states) {
    this.side = side;
    this.states = states;
  }

  @Override public BoardCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(BoardCellViewHolder.LAYOUT_RES, parent, false);
    final BoardCellViewHolder viewHolder = new BoardCellViewHolder(view);
    viewHolder.button.setOnClickListener(v -> {
      int pos = viewHolder.getAdapterPosition();
      if (itemClickListener != null && pos != RecyclerView.NO_POSITION) {
        check(pos, getApp().getUserName());
        itemClickListener.onItemClick(this, viewHolder, v, pos, getItemId(pos));
      }
    });
    return viewHolder;
  }

  @Override public void onBindViewHolder(BoardCellViewHolder holder, int position) {
    holder.bind(this, states.get(position));
  }

  @Override public int getItemCount() {
    return this.states.size();
  }

  /**
   * Call this to update current board state.
   *
   * @param pos checked position
   * @param value TRUE or FALSE, base on User side
   */
  void check(int pos, String userName) {
    if (pos >= 0 && pos < getItemCount()) {
      states.set(pos, userName);
      setCursorPosition(pos);
    }
  }

  void setCursorPosition(int pos) {
    if (pos != cursorPosition && pos >= 0 && pos < getItemCount()) {
      int oldCurPos = cursorPosition;
      this.cursorPosition = pos;
      notifyItemChanged(pos);
      notifyItemChanged(oldCurPos);
    }
  }

  int getCursorPosition() {
    return cursorPosition;
  }

  List<String> getGameState() {
    return states;
  }

  static abstract class ItemClickHandler implements OnItemClickListener {

    abstract void onChecked(View view, int pos);

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder viewHolder,
        View view, int pos, long posId) {
      onChecked(view, pos);
    }
  }
}
