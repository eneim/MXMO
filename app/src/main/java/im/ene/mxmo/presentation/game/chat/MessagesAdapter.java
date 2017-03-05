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

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.domain.model.Message;
import java.util.Collection;

/**
 * Created by eneim on 3/5/17.
 */

class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {

  private final SortedList<Message> messages;

  MessagesAdapter() {
    super();
    this.messages = new SortedList<>(Message.class, new SortedListAdapterCallback<Message>(this) {
      @Override public int compare(Message o1, Message o2) {
        return Long.compare(o2.getCreatedAt(), o1.getCreatedAt());
      }

      @Override public boolean areContentsTheSame(Message oldItem, Message newItem) {
        return newItem.equals(oldItem);
      }

      @Override public boolean areItemsTheSame(Message item1, Message item2) {
        return item1.equals(item2);
      }
    });
  }

  @Override public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(MessageViewHolder.LAYOUT_RES, parent, false);
    return new MessageViewHolder(view);
  }

  @Override public void onBindViewHolder(MessageViewHolder holder, int position) {
    holder.bind(this, messages.get(position));
  }

  @Override public int getItemCount() {
    return messages.size();
  }

  void addMessages(Collection<Message> messages) {
    this.messages.addAll(messages);
  }
}
