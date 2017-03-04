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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import im.ene.mxmo.common.TextWatcherAdapter;
import im.ene.mxmo.presentation.game.board.GameBoardFragment;
import im.ene.mxmo.presentation.game.chat.GameChatFragment;
import java.util.List;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/26/17.
 *
 * @since 1.0.0
 */
public abstract class GameFragment extends BaseFragment
    implements GameContract.GameView, GameBoardFragment.BoardStateChangeListener {

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

  protected @BindView(R.id.overlay) View overlayView;
  AlertDialog welcomeDialog;
  protected GameBoardFragment boardFragment;
  protected GameChatFragment chatFragment;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //noinspection ConstantConditions
    if (getPresenter() == null) {
      throw new NullPointerException("Presenter must not be null.");
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    welcomeDialog = new AlertDialog.Builder(getContext()) //
        .setTitle("Welcome to new TicTacToe Game.")
        .setMessage("You are the first User, please wait for other User to join.")
        .setCancelable(false)
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
        .create();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    getPresenter().setView(null);
  }

  // GameView interface

  @Override public void showHideOverLay(boolean willShow) {
    int overlayViewVisibility = willShow ? View.VISIBLE : View.GONE;
    overlayView.setVisibility(overlayViewVisibility);
  }

  @Override public void showUserNameInputDialog(String defaultUserName) {
    CharSequence currentUserName = getApp().getUserName();
    if (TextUtils.isEmpty(currentUserName)) {
      currentUserName = defaultUserName;
    }

    @SuppressLint("InflateParams") TextInputLayout inputLayout =
        (TextInputLayout) getActivity().getLayoutInflater()
            .inflate(R.layout.widget_edit_text, null);
    inputLayout.setHint("Username");

    EditText editText = (EditText) inputLayout.findViewById(R.id.edit_text);
    editText.setHint(currentUserName);
    CharSequence finalUserName = currentUserName;
    editText.addTextChangedListener(new TextWatcherAdapter() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
          editText.setHint(finalUserName);
        } else {
          editText.setHint("");
        }
      }
    });

    new AlertDialog.Builder(getContext()).setMessage("Choose your username for future use:")
        .setView(inputLayout)
        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
          String name = editText.getText().toString();
          if (TextUtils.isEmpty(name)) {
            name = editText.getHint().toString().trim();
          }
          getApp().setUserName(name);
          dialog.dismiss();
        })
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
          getApp().setUserName(defaultUserName);
          dialog.dismiss();
        })
        .setNeutralButton("Quit", (dialog, which) -> getActivity().finish())  // quit
        .setOnDismissListener(dialog -> getPresenter().onUserName(getApp().getUserName()))
        .setCancelable(false)
        .create()
        .show();
  }

  @Override public void showWaitForSecondUserDialog() {
    welcomeDialog.setOnDismissListener(__ -> getActivity().finish());

    if (!welcomeDialog.isShowing()) {
      welcomeDialog.show();
    }
  }

  @Override public void letTheGameBegin() {
    if (welcomeDialog.isShowing()) {
      welcomeDialog.setOnDismissListener(null);
      welcomeDialog.dismiss();
    }
    Toast.makeText(getContext(), "Game Started", Toast.LENGTH_SHORT).show();

    boardFragment =
        GameBoardFragment.newInstance(getPresenter().getUserSide(), getPresenter().getGameState());
    boardFragment.setTargetFragment(this, 100);
    chatFragment = GameChatFragment.newInstance();
    chatFragment.setTargetFragment(this, 101);

    getChildFragmentManager().beginTransaction()
        .replace(R.id.game_board, boardFragment)
        .replace(R.id.game_chat, chatFragment)
        .commit();
  }

  @Override public void updateGameState(List<String> cells, boolean userInput) {
    boardFragment.syncBoard(cells, userInput);
    String winner = getPresenter().judge();
    if (winner != null) {
      Toast.makeText(getContext(), "Winner: " + winner, Toast.LENGTH_SHORT).show();
    }
  }

  @NonNull protected abstract GameContract.Presenter getPresenter();

  // Other interfaces

  @Override public void onUserMove(List<String> gameState) {
    Log.d(TAG, "onUserMove() called with: gameState = [" + gameState + "]");
    getPresenter().updateGameStateAfterUserMode(gameState);
  }
}
