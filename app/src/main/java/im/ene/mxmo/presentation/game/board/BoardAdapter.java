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

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.MemeApp;
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

  RecyclerView parent;
  final List<String> states;
  final Boolean side; // TRUE for FALSE;

  OnItemClickListener listener;

  @SuppressWarnings("WeakerAccess")
  public void setItemClickListener(OnItemClickListener itemClickListener) {
    this.listener = itemClickListener;
  }

  BoardAdapter(Boolean side, List<String> states) {
    this.side = side;
    this.states = states;
  }

  @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.parent = recyclerView;
  }

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    this.parent = null;
  }

  @Override public BoardCellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(BoardCellViewHolder.LAYOUT_RES, parent, false);
    final BoardCellViewHolder viewHolder = new BoardCellViewHolder(view);
    viewHolder.button.setOnClickListener(v -> {
      int pos = viewHolder.getAdapterPosition();
      if (pos != RecyclerView.NO_POSITION && listener != null //
          && (listener instanceof ItemClickHandler && //
          ((ItemClickHandler) listener).cellCheckable(v, pos))) {
        if (MemeApp.INVALID.equals(states.get(pos))) {
          check(pos, getApp().getUserName());
          listener.onItemClick(this, viewHolder, v, pos, getItemId(pos));
        } else {
          Snackbar.make(parent, "ERROR: Cell has been checked!!", Snackbar.LENGTH_SHORT).show();
        }
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
   * @param userName TRUE or FALSE, base on User side
   */
  @SuppressWarnings("WeakerAccess") void check(int pos, String userName) {
    if (pos >= 0 && pos < getItemCount()) {
      states.set(pos, userName);
      setCursorPosition(pos, true);
    }
  }

  boolean manuallyCheck(int pos) {
    if (MemeApp.INVALID.equals(states.get(pos))) {
      check(pos, getApp().getUserName());
      return true;
    } else {
      if (parent != null) {
        Snackbar.make(parent, "ERROR: Cell has been checked!!", Snackbar.LENGTH_SHORT).show();
      }

      return false;
    }
  }

  void setCursorPosition(int pos, boolean forceUpdate) {
    boolean changed = false;
    if (pos != cursorPosition && pos >= 0 && pos < getItemCount()) {
      int oldCurPos = cursorPosition;
      this.cursorPosition = pos;
      notifyItemChanged(pos);
      notifyItemChanged(oldCurPos);
      changed = true;
    }

    if (!changed) {
      if (forceUpdate) {
        notifyItemChanged(pos);
      }
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

    abstract boolean cellCheckable(View view, int pos);

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder viewHolder,
        View view, int pos, long posId) {
      onChecked(view, pos);
    }
  }
}
