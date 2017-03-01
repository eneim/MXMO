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

package im.ene.mxmo.presentation.game.board;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by eneim on 2/26/17.
 */

public class GamePresenterImpl implements GameBoardContract.GamePresenter {

  private static final String TAG = "MXMO:GamePresenter";

  GameBoardContract.GameView view;
  FirebaseDatabase database;

  ValueEventListener dbValueListener;

  @Override public void setView(GameBoardContract.GameView view) {
    this.view = view;
    if (view != null) {
      database = FirebaseDatabase.getInstance();

      dbValueListener = new ValueEventListener() {
        @Override public void onDataChange(DataSnapshot dataSnapshot) {
          Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
        }

        @Override public void onCancelled(DatabaseError databaseError) {
          Log.d(TAG, "onCancelled() called with: databaseError = [" + databaseError + "]");
        }
      };

      // database.getReference().addValueEventListener(dbValueListener);
    } else {
      if (dbValueListener != null) {
        database.getReference().removeEventListener(dbValueListener);
        dbValueListener = null;
      }
    }
  }

  @Override public void loadGameDB() {
    //HashMap<String, String> names = new HashMap<>();
    //names.put("John", "John Name");
    //names.put("Tim", "Tim Name");
    //names.put("Sam", "Sam Name");
    //names.put("Ben", "Ben Name");
    //database.getReference("messages").setValue(names);
  }
}
