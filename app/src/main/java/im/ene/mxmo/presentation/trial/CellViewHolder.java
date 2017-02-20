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
import android.view.View;
import android.widget.CheckedTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.mxmo.R;

/**
 * Created by eneim on 2/13/17.
 */

public class CellViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.cell_overlay) View overlayView;
  @BindView(R.id.cell_button) CheckedTextView cellButton;

  public CellViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void bind(BoardAdapter adapter, Object item) {
    cellButton.setChecked(getAdapterPosition() == adapter.getSelectedCell());
    if (getAdapterPosition() == adapter.getCursorCell()) {
      overlayView.setVisibility(View.VISIBLE);
    } else {
      overlayView.setVisibility(View.INVISIBLE);
    }
  }
}
