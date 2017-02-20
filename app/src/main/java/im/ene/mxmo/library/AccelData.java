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

public class AccelData {

  private final float accX;
  private final float accY;
  private final float accZ;

  public AccelData(float accX, float accY, float accZ) {
    this.accX = accX;
    this.accY = accY;
    this.accZ = accZ;
  }

  public float getAccX() {
    return accX;
  }

  public float getAccY() {
    return accY;
  }

  public float getAccZ() {
    return accZ;
  }
}
