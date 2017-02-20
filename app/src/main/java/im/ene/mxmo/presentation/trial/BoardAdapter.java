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

package im.ene.mxmo.presentation.trial;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.R;

/**
 * Created by eneim on 2/13/17.
 */

public class BoardAdapter extends RecyclerView.Adapter<CellViewHolder> {

  private int selectedCell = RecyclerView.NO_POSITION;
  private int cursorCell = RecyclerView.NO_POSITION;

  public int getSelectedCell() {
    return selectedCell;
  }

  public int getCursorCell() {
    return cursorCell;
  }

  public void setSelectedCell(int selectedCell) {
    if (this.selectedCell != selectedCell) {
      notifyItemChanged(this.selectedCell);
      this.selectedCell = selectedCell;
      notifyItemChanged(this.selectedCell);
    }
  }

  public void setCursorCell(int cursorCell) {
    if (this.cursorCell != cursorCell) {
      notifyItemChanged(this.cursorCell);
      this.cursorCell = cursorCell;
      notifyItemChanged(this.cursorCell);
    }
  }

  @Override public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.viewholder_board_cell, parent, false);
    CellViewHolder viewHolder = new CellViewHolder(view);
    viewHolder.itemView.setOnClickListener(v -> {
      setCursorCell(viewHolder.getAdapterPosition());
      setSelectedCell(viewHolder.getAdapterPosition());
    });

    return viewHolder;
  }

  @Override public void onBindViewHolder(CellViewHolder holder, int position) {
    holder.bind(this, position);
  }

  @Override public int getItemCount() {
    return Integer.MAX_VALUE;
  }
}
