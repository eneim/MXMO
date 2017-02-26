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

package im.ene.mxmo.common;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by eneim on 2/26/17.
 */

public class ChildEventListenerAdapter implements ChildEventListener {

  @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

  }

  @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

  }

  @Override public void onCancelled(DatabaseError databaseError) {

  }
}
