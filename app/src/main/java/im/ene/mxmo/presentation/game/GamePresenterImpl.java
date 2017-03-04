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

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;
import android.util.SparseArray;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonElement;
import im.ene.mxmo.common.ChildEventListenerAdapter;
import im.ene.mxmo.common.RxBus;
import im.ene.mxmo.common.ValueEventListenerAdapter;
import im.ene.mxmo.common.event.GameChangedEvent;
import im.ene.mxmo.domain.model.TicTacToe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/27/17.
 *
 * @since 1.0.0
 */

class GamePresenterImpl implements GameContract.Presenter {

  @SuppressWarnings("unused") private static final String TAG = "MXMO:GamePresenter";

  @SuppressWarnings("WeakerAccess") GameContract.GameView view;
  @SuppressWarnings("WeakerAccess") CompositeDisposable disposables;

  protected TicTacToe game;
  @SuppressWarnings("WeakerAccess") @NonNull final DatabaseReference gameDb;
  @SuppressWarnings("WeakerAccess") DatabaseReference gameRef;

  GamePresenterImpl(@NonNull DatabaseReference gameDb) {
    this.gameDb = gameDb;
    this.disposables = new CompositeDisposable();
  }

  @Override public void setView(GameContract.GameView view) {
    this.view = view;
    if (view == null) {
      setGame(null, null);
      disposables.dispose();
    } else {
      disposables.add(RxBus.getBus().observe(GameChangedEvent.class)  //
          .observeOn(AndroidSchedulers.mainThread()).subscribe(event -> {
            if (event.game == TicTacToe.DEFAULT) {  // default unknown game
              // There is no available game yet, I create new game with my prefer username
              createGame(event.userName);
            } else {
              // Set current game and database.
              // At this point, there is at least one user joined the game.
              setGame(event.game, event.gameRef);
              if (!event.userName.equals(event.game.getFirstUser())) {
                // I didn't create this game, so I join
                joinGame(event.userName);
              } else {
                // I created the game, so I need to wait for the second player to join
                waitForSecondPlayer();
              }
            }
          }));
    }
  }

  @Override public void initGame() {
    if (view != null) {
      view.showHideOverLay(true);
      view.showUserNameInputDialog("normal_user_" + System.currentTimeMillis());
    }
  }

  // Call after username was set
  @Override public void onUserName(String userName) {
    // Query an available game on Firebase, create new if there is no one.
    this.gameDb.addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        //noinspection Convert2MethodRef,ConstantConditions
        Observable.just(snapshot)
            .filter(s1 -> s1 != null && s1.getValue() instanceof HashMap)
            .map(s2 -> (HashMap) s2.getValue())
            .flatMapIterable((Function<HashMap, Iterable<Map.Entry>>) map -> map.entrySet())
            .filter(entry -> entry.getValue() instanceof HashMap)
            .map(entry -> Pair.create(entry.getKey().toString(), (HashMap) entry.getValue()))
            .filter(pair -> Boolean.FALSE.equals(pair.second.get("finished")) // not finished yet
                && !Boolean.TRUE.equals(pair.second.get("started")) // not started yet
                && (pair.second.get("secondUser") == null || pair.second.get("firstUser") == null))
            .map(pair -> {
              JsonElement json = getApp().getGson().toJsonTree(pair.second);
              return Pair.create(pair.first, getApp().getGson().fromJson(json, TicTacToe.class));
            })
            .sorted((o1, o2) -> Long.compare(o2.second.getCreatedAt(), o1.second.getCreatedAt()))
            .first(new Pair<>(null, TicTacToe.DEFAULT))
            .filter(pair -> pair.first != null)
            .map(game -> new GameChangedEvent(userName, gameDb.child(game.first), game.second))
            .defaultIfEmpty(new GameChangedEvent(userName, null, TicTacToe.DEFAULT))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(event -> RxBus.getBus().send(event));
      }
    });
  }

  @SuppressWarnings("WeakerAccess") void createGame(String userName) {
    TicTacToe newGame = new TicTacToe();
    newGame.setStarted(false);
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
              RxBus.getBus().send(new GameChangedEvent(userName, snapshot.getRef(), currentGame));
            }
          }
        });

    gameDb.push().setValue(newGame);
  }

  @SuppressWarnings("WeakerAccess") void setGame(TicTacToe game, DatabaseReference gameRef) {
    this.game = game;
    this.gameRef = gameRef;
  }

  @SuppressWarnings("WeakerAccess") void waitForSecondPlayer() {
    this.gameRef.addValueEventListener(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        if (snapshot.getValue() != null) {
          Object firstUser = ((HashMap) snapshot.getValue()).get("firstUser");
          Object secondUser = ((HashMap) snapshot.getValue()).get("secondUser");
          if (firstUser != null && secondUser != null) {  // both Users are in
            gameRef.removeEventListener(this);
            onGameAbleToStart();
          } else {
            if (view != null) {
              view.showWaitForSecondUserDialog();
            }
          }
        }
      }
    });
  }

  @SuppressWarnings("WeakerAccess") void joinGame(@NonNull String userName) {
    this.gameRef.addValueEventListener(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        gameRef.removeEventListener(this);
        onGameAbleToStart();
      }
    });
    // at this point, game's secondUser is empty, so update it and start the game.
    this.game.setSecondUser(userName);
    this.gameRef.updateChildren(getApp().parseToHashMap(this.game));
  }

  ChildEventListener childEventListener = new ChildEventListener() {
    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      Log.i(TAG, "onChildAdded: " + dataSnapshot);
    }

    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
      Log.i(TAG, "onChildChanged: " + dataSnapshot);
    }

    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override public void onCancelled(DatabaseError databaseError) {

    }
  };

  @SuppressWarnings("WeakerAccess") void onGameAbleToStart() {
    this.gameRef.addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        if (view != null) {
          view.showHideOverLay(false);
          view.letTheGameBegin();
        }
        gameRef.addChildEventListener(childEventListener);
      }
    });
    this.game.setStarted(true);
    for (int i = 0; i < 9; i++) {
      this.game.getCells().add("?");
    }
    this.gameRef.updateChildren(getApp().parseToHashMap(this.game));
  }

  @Override public void updateGameState(SparseArray<String> state) {
    this.game.getCells().clear();
    for (int i = 0; i < state.size(); i++) {
      this.game.getCells().add(state.get(i));
    }

    this.gameRef.child("cells").setValue(game.getCells());
  }
}
