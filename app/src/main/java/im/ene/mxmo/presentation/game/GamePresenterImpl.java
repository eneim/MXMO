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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.JsonElement;
import im.ene.mxmo.common.ChildEventListenerAdapter;
import im.ene.mxmo.common.RxBus;
import im.ene.mxmo.common.ValueEventListenerAdapter;
import im.ene.mxmo.common.event.GameChangedEvent;
import im.ene.mxmo.domain.model.TicTacToe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.Map;

import static im.ene.mxmo.MemeApp.getApp;

/**
 * Created by eneim on 2/27/17.
 */

class GamePresenterImpl implements GameContract.Presenter {

  @SuppressWarnings("unused") private static final String TAG = "MXMO:GamePresenter";

  GameContract.GameView view;

  protected TicTacToe game;
  final DatabaseReference gameDb;   // whole DB ref
  DatabaseReference gameRef;  // current game ref

  GamePresenterImpl(DatabaseReference gameDb) {
    this.gameDb = gameDb;
  }

  @Override public void setView(GameContract.GameView view) {
    this.view = view;
  }

  @Override public void setupGameUser() {
    if (view != null) {
      view.setupUserName("normal_user_" + System.currentTimeMillis());
    }
  }

  @Override public void joinGameOrCreateNew() {
    if (view == null) {
      return;
    }

    //noinspection ConstantConditions
    view.setupEventBus(getApp().getUserName());

    // Find latest game on Firebase.
    gameDb.addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot snapshot) {
        //noinspection Convert2MethodRef,ConstantConditions
        Observable.just(snapshot)
            .filter(s1 -> s1 != null && s1.getValue() instanceof HashMap)
            .map(s2 -> (HashMap) s2.getValue())
            .flatMapIterable((Function<HashMap, Iterable<Map.Entry>>) map -> map.entrySet())
            .filter(entry -> entry.getValue() instanceof HashMap)
            .map(entry -> Pair.create(entry.getKey().toString(), (HashMap) entry.getValue()))
            .filter(p -> Boolean.FALSE.equals(p.second.get("finished")) &&  //
                !Boolean.TRUE.equals(p.second.get("started")) &&  // either null or false
                (p.second.get("secondUser") == null || p.second.get("firstUser") == null))
            .map(pair -> {
              JsonElement json = getApp().getGson().toJsonTree(pair.second);
              return Pair.create(pair.first, getApp().getGson().fromJson(json, TicTacToe.class));
            })
            .sorted((o1, o2) -> Long.compare(o2.second.getCreatedAt(), o1.second.getCreatedAt()))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .first(new Pair<>(null, TicTacToe.DEFAULT))
            .filter(pair -> pair.first != null)
            .map(game -> new GameChangedEvent(gameDb.child(game.first), game.second))
            .defaultIfEmpty(new GameChangedEvent(null, TicTacToe.DEFAULT))
            .subscribe(game -> RxBus.getBus().send(game));
      }
    });
  }

  @Override public void createGame(String userName) {
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
              RxBus.getBus().send(new GameChangedEvent(snapshot.getRef(), currentGame));
            }
          }
        });

    gameDb.push().setValue(newGame);
  }

  @Override public void setGame(TicTacToe game, DatabaseReference gameRef) {
    this.game = game;
    this.gameRef = gameRef;
  }

  @Override public void joinGame(@NonNull String userName) {
    this.gameRef.addValueEventListener(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        gameRef.removeEventListener(this);
      }
    });
    // at this point, game's secondUser is empty, so update it and start the game.
    this.game.setSecondUser(userName);
    this.gameRef.updateChildren(getApp().saveAndParseToHashMap(this.game));
  }

  @Override public void onGameAbleToStart() {
    this.gameRef.addListenerForSingleValueEvent(new ValueEventListenerAdapter() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        if (view != null) {
          view.letTheGameBegin();
        }
      }
    });
    this.game.setStarted(true);
    this.gameRef.updateChildren(getApp().saveAndParseToHashMap(this.game));
  }
}
