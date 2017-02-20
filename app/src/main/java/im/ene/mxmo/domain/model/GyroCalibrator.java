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

import im.ene.mxmo.library.GyroData;
import java.util.ArrayList;

/**
 * Created by eneim on 2/13/17.
 */

public class GyroCalibrator extends ArrayList<GyroData> {

  private Long startTime;

  float avrPitch = 0;
  float avrRoll = 0;
  float avrYaw = 0;

  public GyroCalibrator() {
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Override public boolean add(GyroData gyroData) {
    avrPitch += gyroData.getPitch();
    avrRoll += gyroData.getRoll();
    avrYaw += gyroData.getYaw();
    return super.add(gyroData);
  }

  public GyroData getCalibrated() {
    int size = this.size();
    return new GyroData(avrPitch / (float) size, avrRoll / (float) size, avrYaw / (float) size);
  }
}
