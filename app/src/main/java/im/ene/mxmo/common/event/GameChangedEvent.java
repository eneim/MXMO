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

package im.ene.mxmo.common.event;

import android.support.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import im.ene.mxmo.common.Event;
import im.ene.mxmo.domain.model.TicTacToe;

/**
 * Created by eneim on 2/26/17.
 */

public class GameChangedEvent extends Event {

  @NonNull public final String userName;

  @NonNull public final DatabaseReference gameRef;

  @NonNull public final TicTacToe game;

  public GameChangedEvent(@NonNull String userName, @NonNull DatabaseReference gameRef,
      @NonNull TicTacToe game) {
    this.userName = userName;
    this.gameRef = gameRef;
    this.game = game;
  }
}
