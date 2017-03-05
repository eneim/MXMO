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

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.OnClick;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import java.util.ArrayList;

/**
 * Created by eneim on 2/26/17.
 *
 * @since 1.0.0
 */

public class GameChatFragment extends BaseFragment {

  private static final String ARG_CHAT_EMOJIS = "MXMO_CHAT_EMOJIS";

  public static GameChatFragment newInstance(ArrayList<Integer> emojis) {
    GameChatFragment fragment = new GameChatFragment();
    Bundle args = new Bundle();
    args.putIntegerArrayList(ARG_CHAT_EMOJIS, emojis);
    fragment.setArguments(args);
    return fragment;
  }

  ArrayList<Integer> emojis;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      emojis = new ArrayList<>();
      //noinspection ConstantConditions
      emojis.addAll(getArguments().getIntegerArrayList(ARG_CHAT_EMOJIS));
    }
  }

  AlertDialog emojiDialog;
  EmojiAdapter.EmojiClickHandler emojiClickHandler;

  @SuppressWarnings("unused") @OnClick(R.id.select_emoji) void openEmojiDialog() {
    if (emojiDialog == null) {
      @SuppressLint("InflateParams")  //
          RecyclerView emojiList = (RecyclerView) LayoutInflater.from(getContext())
          .inflate(R.layout.layout_list_emoji, null);
      emojiDialog = new AlertDialog.Builder(getContext()).setTitle("Fun with Emoji Chat")
          .setView(emojiList)
          .setMessage("Select emoji from below:")
          .create();
      LinearLayoutManager layoutManager =
          new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
      emojiList.setLayoutManager(layoutManager);
      EmojiAdapter adapter = new EmojiAdapter(emojis);
      adapter.setClickHandler(emojiClickHandler);
      emojiList.setAdapter(adapter);
      LinearSnapHelper snapHelper = new LinearSnapHelper();
      snapHelper.attachToRecyclerView(emojiList);
      emojiDialog.setOnDismissListener(dialog -> snapHelper.attachToRecyclerView(null));
      emojiDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
          (dialog, which) -> dialog.dismiss());
      emojiDialog.setCancelable(false);
    }

    if (!emojiDialog.isShowing()) {
      emojiDialog.show();
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_game_chat, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    emojiClickHandler = new EmojiAdapter.EmojiClickHandler() {
      @Override void onEmojiSelected(Integer emoji, String emojiText) {
        // TODO
      }
    };
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    emojiClickHandler = null;
    emojiDialog = null;
  }
}
