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

package im.ene.mxmo.library;

/**
 * Created by eneim on 2/5/17.
 */

public class Command {

  private final Source source;

  private final Action action;

  public Command(Source source, Action action) {
    this.source = source;
    this.action = action;
  }

  public static Command of(Source source, Action action) {
    return new Command(source, action);
  }

  @Override public String toString() {
    return action.name();
  }

  public Source getSource() {
    return source;
  }

  public Action getAction() {
    return action;
  }
}
