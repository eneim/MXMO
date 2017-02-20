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

/**
 * Created by eneim on 2/4/17.
 */

public class DataWindow {

  private final GyroData calibGyroData;

  private final MemeRealTimeDataFilter headFilter;
  private final MemeRealTimeDataFilter eyeFilter;

  public DataWindow(GyroData calibGyroData) {
    this.calibGyroData = calibGyroData;
    this.headFilter = new MemeRealTimeDataFilter(Source.HEAD, calibGyroData);
    this.eyeFilter = new MemeRealTimeDataFilter(Source.EYE, calibGyroData);
  }

  private MemeRealtimeData endData;

  public void update(MemeRealtimeData data) {
    this.endData = data;
    this.eyeFilter.update(data);
    this.headFilter.update(data);
  }

  public MemeRealtimeData getEndData() {
    return endData;
  }

  public Command getEyeCommand() {
    return eyeFilter.getLastCmd();
  }

  public Command getHeadCommand() {
    return headFilter.getLastCmd();
  }
}
