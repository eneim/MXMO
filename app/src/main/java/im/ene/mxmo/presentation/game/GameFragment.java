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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;
import im.ene.mxmo.MemeApp;
import im.ene.mxmo.R;
import im.ene.mxmo.common.BaseFragment;
import im.ene.mxmo.common.ChildEventListenerAdapter;
import im.ene.mxmo.common.RxBus;
import im.ene.mxmo.common.ValueEventListenerAdapter;
import im.ene.mxmo.common.event.GameChangedEvent;
import im.ene.mxmo.domain.model.TicTacToe;
import im.ene.mxmo.presentation.game.board.GameBoardFragment;
import im.ene.mxmo.presentation.game.chat.GameChatFragment;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/26/17.
 */

public abstract class GameFragment extends BaseFragment {

  private static final String TAG = "MXMO:GameFragment";

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

  private CompositeDisposable disposables = new CompositeDisposable();

  protected @BindView(R.id.overlay) View overlayView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    boardFragment = GameBoardFragment.newInstance();
    chatFragment = GameChatFragment.newInstance();

    getChildFragmentManager().beginTransaction()
        .replace(R.id.game_board, boardFragment)
        .replace(R.id.game_chat, chatFragment)
        .commit();
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    gameDb = FirebaseDatabase.getInstance().getReference("games");
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    disposables.dispose();
  }

  // Sync game with Firebase

  protected TicTacToe game;
  protected GameBoardFragment boardFragment;
  protected GameChatFragment chatFragment;
  protected DatabaseReference gameDb;
  protected DatabaseReference gameRef;

  protected final ValueEventListener gameFoundListener = new ValueEventListenerAdapter() {
    @Override public void onDataChange(DataSnapshot snapshot) {
      if (snapshot.getValue() instanceof HashMap<?, ?>) {
        Observable.fromIterable(((HashMap<?, ?>) snapshot.getValue()).entrySet())
            // each entry value must be a HashMap
            .filter(entry -> entry.getValue() instanceof HashMap<?, ?>)
            // then convert the entry to pair for ease
            .map(new Function<Map.Entry<?, ?>, Pair<String, HashMap<?, ?>>>() {
              @Override public Pair<String, HashMap<?, ?>> apply(Map.Entry<?, ?> entry)
                  throws Exception {
                return Pair.create(entry.getKey().toString(), (HashMap<?, ?>) entry.getValue());
              }
            })
            // find all gameDb those are not finished
            .filter(pair -> pair.second.containsKey("finished") &&  //
                Boolean.FALSE.equals(pair.second.get("finished")))
            // convert to TicTacToe POJO
            .map(pair -> {
              JsonElement jsonData = getApp().getGson().toJsonTree(pair.second);
              return Pair.create(pair.first,
                  getApp().getGson().fromJson(jsonData, TicTacToe.class));
            })
            // sort by create time, get the latest only
            .sorted((o1, o2) -> Long.compare(o2.second.getCreatedAt(), o1.second.getCreatedAt()))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .first(new Pair<>("", TicTacToe.DEFAULT))
            .subscribe(game -> {
              gameDb.removeEventListener(this);
              RxBus.getBus().send(new GameChangedEvent(gameDb.child(game.first), game.second));
            });
      } else {
        gameDb.removeEventListener(this);
        //noinspection ConstantConditions
        RxBus.getBus().send(new GameChangedEvent(null, TicTacToe.DEFAULT));
      }
    }
  };

  // must be call after onActivityCreated

  // this method must be called after Username has been decided
  protected void setupEventBus(@NonNull String userName) {
    disposables.add(RxBus.getBus()
        .observe(GameChangedEvent.class)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(event -> {
          if (event.game == TicTacToe.DEFAULT) {
            // create new game
            createGame(userName);
          } else if (getApp().getCurrentGame() == null || // always get latest game
              getApp().getCurrentGame().getCreatedAt() < event.game.getCreatedAt()) {
            this.gameRef = event.gameRef;
            this.game = event.game;
            // to cache
            getApp().setCurrentGame(event.game);
            if (!MemeApp.getApp().getUserName() //
                .equals(this.game.getFirstUser())) { // I didn't create this game, so I join
              joinGame(MemeApp.getApp().getUserName());
            }
          }
        }));
  }

  protected final void findGameOrCreateNew() {
    // initialize game state
    gameDb.addValueEventListener(gameFoundListener);
  }

  protected final void joinGame(String userName) {
    // at this point, game's secondUser is empty, so update it and start the game.
    this.game.setSecondUser(userName);
    gameRef = gameDb.child(gameRef.getKey());
    gameRef.addValueEventListener(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        super.onDataChange(dataSnapshot);
      }
    });

    gameRef.updateChildren(MemeApp.getApp().toHashMap(this.game));
  }

  // The user who creates the game will be the first user.
  protected final void createGame(String userName) {
    TicTacToe newGame = new TicTacToe();
    newGame.setFinished(false);
    newGame.setFirstUser(userName);

    gameDb.orderByChild("createdAt")
        .equalTo(newGame.getCreatedAt())
        .limitToLast(1)
        .addChildEventListener(new ChildEventListenerAdapter() {
          @Override public void onChildAdded(DataSnapshot snapshot, String s) {
            gameDb.removeEventListener(this);
            if (snapshot != null && snapshot.getValue() != null) {
              JsonElement jsonData = getApp().getGson().toJsonTree(snapshot.getValue());
              TicTacToe currentGame = getApp().getGson().fromJson(jsonData, TicTacToe.class);
              RxBus.getBus().send(new GameChangedEvent(snapshot.getRef(), currentGame));
            }
          }
        });

    gameDb.push().setValue(newGame);
  }
}
