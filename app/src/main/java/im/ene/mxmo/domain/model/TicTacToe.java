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

package im.ene.mxmo.domain.model;

import java.util.ArrayList;

/**
 * Created by eneim on 2/26/17.
 */

public class TicTacToe {

  private final long createdAt;

  public TicTacToe() {
    this(System.currentTimeMillis());
  }

  public TicTacToe(long createdAt) {
    this.createdAt = createdAt;
  }

  // Normal user --> normal_user_{uuid}
  // Meme user --> meme_user_{meme_id}
  private String firstUser;

  // Normal user --> normal_user_{uuid}
  // Meme user --> meme_user_{meme_id}
  private String secondUser;

  private boolean started;

  private boolean finished;

  private Boolean currentTurn;  // TRUE: first user, FALSE: second user

  private ArrayList<String> cells = new ArrayList<>(9); // size of 9, state of cells

  private ArrayList<Message> messages = new ArrayList<>();

  public String getFirstUser() {
    return firstUser;
  }

  public void setFirstUser(String firstUser) {
    this.firstUser = firstUser;
  }

  public String getSecondUser() {
    return secondUser;
  }

  public void setSecondUser(String secondUser) {
    this.secondUser = secondUser;
  }

  public boolean isFinished() {
    return finished;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public boolean isStarted() {
    return started;
  }

  public void setStarted(boolean started) {
    this.started = started;
  }

  public ArrayList<String> getCells() {
    return cells;
  }

  @Deprecated public void setCells(ArrayList<String> cells) {
    this.cells = cells;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public ArrayList<Message> getMessages() {
    return messages;
  }

  @Deprecated public void setMessages(ArrayList<Message> messages) {
    this.messages = messages;
  }

  public Boolean getCurrentTurn() {
    return currentTurn;
  }

  public void setCurrentTurn(Boolean currentTurn) {
    this.currentTurn = currentTurn;
  }

  public static final TicTacToe DEFAULT = new TicTacToe(-1);

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TicTacToe)) return false;

    TicTacToe ticTacToe = (TicTacToe) o;

    if (createdAt != ticTacToe.createdAt) return false;
    if (started != ticTacToe.started) return false;
    if (finished != ticTacToe.finished) return false;
    if (firstUser != null ? !firstUser.equals(ticTacToe.firstUser) : ticTacToe.firstUser != null) {
      return false;
    }
    if (secondUser != null ? !secondUser.equals(ticTacToe.secondUser)
        : ticTacToe.secondUser != null) {
      return false;
    }
    if (currentTurn != null ? !currentTurn.equals(ticTacToe.currentTurn)
        : ticTacToe.currentTurn != null) {
      return false;
    }
    if (!cells.equals(ticTacToe.cells)) return false;
    return messages.equals(ticTacToe.messages);
  }

  @Override public int hashCode() {
    int result = (int) (createdAt ^ (createdAt >>> 32));
    result = 31 * result + (firstUser != null ? firstUser.hashCode() : 0);
    result = 31 * result + (secondUser != null ? secondUser.hashCode() : 0);
    result = 31 * result + (started ? 1 : 0);
    result = 31 * result + (finished ? 1 : 0);
    result = 31 * result + (currentTurn != null ? currentTurn.hashCode() : 0);
    result = 31 * result + cells.hashCode();
    result = 31 * result + messages.hashCode();
    return result;
  }
}
