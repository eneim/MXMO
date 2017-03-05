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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eneim on 2/5/17.
 */

public class GyroData implements Parcelable {

  private final float pitch;
  private final float roll;
  private final float yaw;

  public GyroData(float pitch, float roll, float yaw) {
    this.pitch = pitch;
    this.roll = roll;
    this.yaw = yaw;
  }

  public float getPitch() {
    return pitch;
  }

  public float getRoll() {
    return roll;
  }

  public float getYaw() {
    return yaw;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeFloat(this.pitch);
    dest.writeFloat(this.roll);
    dest.writeFloat(this.yaw);
  }

  protected GyroData(Parcel in) {
    this.pitch = in.readFloat();
    this.roll = in.readFloat();
    this.yaw = in.readFloat();
  }

  public static final Creator<GyroData> CREATOR = new Creator<GyroData>() {
    @Override public GyroData createFromParcel(Parcel source) {
      return new GyroData(source);
    }

    @Override public GyroData[] newArray(int size) {
      return new GyroData[size];
    }
  };
}
