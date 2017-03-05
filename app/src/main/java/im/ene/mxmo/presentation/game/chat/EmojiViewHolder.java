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

import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.mxmo.R;

/**
 * Created by eneim on 3/5/17.
 */

public class EmojiViewHolder extends RecyclerView.ViewHolder {

  static final int LAYOUT_RES = R.layout.viewholder_emoji_item;

  @BindView(R.id.emoji_text) TextView emojiText;

  ColorStateList selected = ColorStateList.valueOf(0xffea5444);
  ColorStateList normal = ColorStateList.valueOf(0xffffff);

  Integer emoji;

  public EmojiViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  void bind(EmojiAdapter adapter, Integer emoji) {
    this.emoji = emoji;
    emojiText.setText(getEmojiByUnicode(emoji));
    if (adapter.getRealPosition(this) == adapter.getRealCursorPosition()) {
      emojiText.setBackgroundTintList(selected);
    } else {
      emojiText.setBackgroundTintList(normal);
    }
  }

  static String getEmojiByUnicode(int unicode) {
    return new String(Character.toChars(unicode));
  }
}
