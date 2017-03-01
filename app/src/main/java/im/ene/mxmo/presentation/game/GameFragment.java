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

package im.ene.mxmo.presentation.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import im.ene.mxmo.common.RxBus;
import im.ene.mxmo.common.TextWatcherAdapter;
import im.ene.mxmo.common.ValueEventListenerAdapter;
import im.ene.mxmo.common.event.GameChangedEvent;
import im.ene.mxmo.domain.model.TicTacToe;
import im.ene.mxmo.presentation.game.board.GameBoardFragment;
import im.ene.mxmo.presentation.game.chat.GameChatFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import java.util.HashMap;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/26/17.
 */

public abstract class GameFragment extends BaseFragment implements GameContract.GameView {

  @SuppressWarnings("unused") private static final String TAG = "MXMO:GameFragment";

  public static GameFragment newInstance(int gameMode) {
    GameFragment fragment;
    switch (gameMode) {
      case GameMode.MODE_MEME:
        fragment = new MemeGameFragment();
        break;
      case GameMode.MODE_NORMAL:
      default:
        fragment = new NormalGameFragment();
        break;
    }

    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  private CompositeDisposable disposables;
  protected @BindView(R.id.overlay) View overlayView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    disposables = new CompositeDisposable();

    boardFragment = GameBoardFragment.newInstance();
    chatFragment = GameChatFragment.newInstance();

    getChildFragmentManager().beginTransaction()
        .replace(R.id.game_board, boardFragment)
        .replace(R.id.game_chat, chatFragment)
        .commit();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    disposables.dispose();
    disposables = null;
  }

  // Sync game with Firebase

  // protected TicTacToe game;
  protected GameBoardFragment boardFragment;
  protected GameChatFragment chatFragment;
  // protected DatabaseReference gameDb;   // whole DB ref
  // protected DatabaseReference gameRef;  // current game ref

  // GameView interface

  @Override public Context getViewContext() {
    return getContext();
  }

  @Override public void setupUserName(@NonNull String defaultUserName) {
    CharSequence userName = MemeApp.getApp().getUserName();
    if (TextUtils.isEmpty(userName)) {
      userName = defaultUserName;
    }

    @SuppressLint("InflateParams")  //
        TextInputLayout inputLayout = (TextInputLayout) getActivity().getLayoutInflater()
        .inflate(R.layout.widget_edit_text, null);
    inputLayout.setHint("Username");
    EditText editText = (EditText) inputLayout.findViewById(R.id.edit_text);
    editText.setHint(userName);
    CharSequence finalUserName = userName;
    editText.addTextChangedListener(new TextWatcherAdapter() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
          editText.setHint(finalUserName);
        } else {
          editText.setHint("");
        }
      }
    });

    AlertDialog alertDialog =
        new AlertDialog.Builder(getContext()).setMessage("Choose your username for future use:")
            .setView(inputLayout)
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
              String name = editText.getText().toString();
              if (TextUtils.isEmpty(name)) {
                name = editText.getHint().toString().trim();
              }
              MemeApp.getApp().setUserName(name);
              dialog.dismiss();
            })
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
              MemeApp.getApp().setUserName(defaultUserName);
              dialog.dismiss();
            })
            .setOnDismissListener(dialog -> getPresenter().joinGameOrCreateNew())
            .setCancelable(false)
            .create();
    alertDialog.show();
  }

  // must be call after onActivityCreated

  // this method must be called after Username has been decided
  @Override public void setupEventBus(@NonNull String userName) {
    disposables.add(RxBus.getBus()
        .observe(GameChangedEvent.class)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(event -> {
          if (event.game == TicTacToe.DEFAULT) {
            // create new game
            getPresenter().createGame(userName);
          } else if (getApp().getCurrentGame() == null || //
              getApp().getCurrentGame().getCreatedAt() < event.game.getCreatedAt()) {
            // save to cache
            getApp().setCurrentGame(event.game);
            getPresenter().setGame(event.game, event.gameRef);

            AlertDialog dialog =
                new AlertDialog.Builder(getContext()).setTitle("Welcome to new TicTacToe Game.")
                    .setMessage("You are the first User, please wait for other User to join.")
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.cancel,
                        (dialog1, which) -> dialog1.dismiss())
                    .create();

            event.gameRef.addValueEventListener(new ValueEventListenerAdapter() {
              @Override public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                  Object firstUser = ((HashMap) snapshot.getValue()).get("firstUser");
                  Object secondUser = ((HashMap) snapshot.getValue()).get("secondUser");
                  if (firstUser != null && secondUser != null) {  // both in
                    if (dialog.isShowing()) {
                      dialog.setOnDismissListener(null);
                      dialog.dismiss();
                    }
                    event.gameRef.removeEventListener(this);
                    getPresenter().onGameAbleToStart();
                  } else {
                    waitForUser(dialog, event.gameRef, this);
                  }
                }
              }
            });
            // I didn't create this game, so I join
            if (!userName.equals(event.game.getFirstUser())) {
              getPresenter().joinGame(userName);
            }
          }
        }));
  }

  void waitForUser(AlertDialog dialog, DatabaseReference ref, ValueEventListener listener) {
    dialog.setOnDismissListener(dialog1 -> {
      ref.removeEventListener(listener);
      getActivity().finish();
    });
    if (!dialog.isShowing()) {
      dialog.show();
    }
  }

  @Override public void letTheGameBegin() {
    Toast.makeText(getContext(), "Game Started", Toast.LENGTH_SHORT).show();
  }

  @NonNull protected abstract GameContract.Presenter getPresenter();
}
