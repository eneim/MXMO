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

package im.ene.mxmo.presentation.game.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.common.OnItemClickListener;
import java.util.List;

/**
 * Created by eneim on 3/5/17.
 */

public class EmojiAdapter extends RecyclerView.Adapter<EmojiViewHolder> {

  private final List<Integer> emojis;

  private int cursorPosition = RecyclerView.NO_POSITION;

  EmojiAdapter(List<Integer> emojis) {
    this.emojis = emojis;
  }

  EmojiClickHandler clickHandler;

  public void setClickHandler(EmojiClickHandler clickHandler) {
    this.clickHandler = clickHandler;
  }

  @Override public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(EmojiViewHolder.LAYOUT_RES, parent, false);
    EmojiViewHolder holder = new EmojiViewHolder(view);
    holder.itemView.setOnClickListener(v -> {
      if (clickHandler != null) {
        setCursorPosition(holder.getAdapterPosition());
        clickHandler.onItemClick(EmojiAdapter.this, holder, v, holder.getAdapterPosition(),
            getItemId(holder.getAdapterPosition()));
      }
    });

    return holder;
  }

  @Override public void onBindViewHolder(EmojiViewHolder holder, int position) {
    holder.bind(this, emojis.get(position % emojis.size()));
  }

  @Override public int getItemCount() {
    return Integer.MAX_VALUE;
  }

  void setCursorPosition(int cursorPosition) {
    if (this.cursorPosition != cursorPosition) {
      int oldPos = this.cursorPosition;
      this.cursorPosition = cursorPosition;
      // notifyItemChanged(oldPos);
      // notifyItemChanged(this.cursorPosition);
      notifyDataSetChanged();
    }
  }

  int getCursorPosition() {
    return cursorPosition;
  }

  int getRealCursorPosition() {
    return this.cursorPosition % emojis.size();
  }

  int getRealPosition(EmojiViewHolder holder) {
    return holder.getAdapterPosition() % emojis.size();
  }

  static abstract class EmojiClickHandler implements OnItemClickListener {

    abstract void onEmojiSelected(Integer emoji, String emojiText);

    @Override
    public void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder viewHolder,
        View view, int pos, long posId) {
      Integer emoji = ((EmojiViewHolder) viewHolder).emoji;
      onEmojiSelected(emoji, EmojiViewHolder.getEmojiByUnicode(emoji));
    }
  }
}
