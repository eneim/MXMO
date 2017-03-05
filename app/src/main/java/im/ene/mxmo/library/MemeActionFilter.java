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

import com.jins_jp.meme.MemeRealtimeData;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eneim on 2/5/17.
 */

public class MemeActionFilter {

  public synchronized void onNewData(MemeRealtimeData data) {
    if (this.headActions != null) {
      for (OnHeadActionListener listener : this.headActions) {
        listener.onHeadAction(headActionFilter.update(data));
      }
    }

    if (this.eyeActions != null) {
      for (OnEyeActionListener listener : this.eyeActions) {
        listener.onEyeAction(eyeActionFilter.update(data));
      }
    }
  }

  public interface OnHeadActionListener {

    void onHeadAction(Command action);
  }

  public interface OnEyeActionListener {

    void onEyeAction(Command action);
  }

  private List<OnHeadActionListener> headActions;
  private List<OnEyeActionListener> eyeActions;

  public void addOnHeadActionListener(OnHeadActionListener listener) {
    if (this.headActions == null) {
      this.headActions = new ArrayList<>();
    }
    this.headActions.add(listener);
  }

  public void removeOnHeadActionListener(OnHeadActionListener listener) {
    if (this.headActions != null) {
      if (listener != null) {
        this.headActions.remove(listener);
      } else {
        this.headActions.clear();
      }
    }
  }

  public void addOnEyeActionListener(OnEyeActionListener listener) {
    if (this.eyeActions == null) {
      this.eyeActions = new ArrayList<>();
    }
    this.eyeActions.add(listener);
  }

  public void removeOnEyeActionListener(OnEyeActionListener listener) {
    if (this.eyeActions != null) {
      if (listener != null) {
        this.eyeActions.remove(listener);
      } else {
        this.eyeActions.clear();
      }
    }
  }

  private final MemeRealTimeDataFilter eyeActionFilter;
  private final MemeRealTimeDataFilter headActionFilter;

  public MemeActionFilter(GyroData calibGyroData) {
    this.eyeActionFilter = new MemeRealTimeDataFilter(Source.EYE, calibGyroData); // need separated calibated data
    this.headActionFilter = new MemeRealTimeDataFilter(Source.HEAD, calibGyroData);
  }
}
