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

import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.R;
import im.ene.mxmo.domain.model.Message;
import java.util.Date;

/**
 * Created by eneim on 3/5/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

  static final int LAYOUT_RES = R.layout.viewholder_message_item;

  @BindView(R.id.dummy_left) View leftDummy;
  @BindView(R.id.dummy_right) View rightDummy;
  @BindView(R.id.message_user_name) TextView userName;
  @BindView(R.id.message_content) TextView content;
  @BindView(R.id.message_timestamp) TextView timeStamp;

  public MessageViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  void bind(MessagesAdapter adapter, Message message) {
    if (message.getUserName().equals(MemeApp.getApp().getUserName())) {
      // expensive
      userName.setGravity(GravityCompat.START);
      timeStamp.setGravity(GravityCompat.START);
      leftDummy.setVisibility(View.GONE);
      rightDummy.setVisibility(View.VISIBLE);
    } else {
      // expensive
      userName.setGravity(GravityCompat.END);
      timeStamp.setGravity(GravityCompat.END);
      leftDummy.setVisibility(View.VISIBLE);
      rightDummy.setVisibility(View.GONE);
    }

    userName.setText(message.getUserName());
    content.setText(message.getMessage());
    timeStamp.setText(MemeApp.getApp().getPrettyTime().format(new Date(message.getCreatedAt())));
  }
}
