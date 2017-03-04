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
import android.view.View;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.R;

/**
 * Created by eneim on 3/4/17.
 */

public class BoardCellViewHolder extends RecyclerView.ViewHolder {

  static final int LAYOUT_RES = R.layout.widget_board_cell;

  @BindView(R.id.cell_button) ImageButton button;
  @BindView(R.id.cell_overlay) View overlay;

  BoardCellViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  void bind(BoardAdapter adapter, Object item /* username or MemeApp.INVALID */) {
    if (item != MemeApp.INVALID) {
      int res = adapter.side == (item.toString().equals(MemeApp.getApp().getUserName())) ? //
          R.drawable.ic_button_shape_oval : R.drawable.ic_button_close;
      button.setImageResource(res);
    } else {
      button.setImageResource(R.drawable.blank_button);
    }

    if (getAdapterPosition() == adapter.getCursorPosition()) {
      overlay.setVisibility(View.VISIBLE);
    } else {
      overlay.setVisibility(View.GONE);
    }
  }
}
