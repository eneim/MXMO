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

/**
 * Created by eneim on 2/26/17.
 */

public class Message {

  private final String userName;

  private final Long createdAt;

  private String message; // current: only Emoji

  public Message(String userName) {
    this.userName = userName;
    this.createdAt = System.currentTimeMillis();
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getUserName() {
    return userName;
  }

  public String getMessage() {
    return message;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Message)) return false;

    Message message1 = (Message) o;

    if (!userName.equals(message1.userName)) return false;
    if (!createdAt.equals(message1.createdAt)) return false;
    return message.equals(message1.message);
  }

  @Override public int hashCode() {
    int result = userName.hashCode();
    result = 31 * result + createdAt.hashCode();
    result = 31 * result + message.hashCode();
    return result;
  }
}
