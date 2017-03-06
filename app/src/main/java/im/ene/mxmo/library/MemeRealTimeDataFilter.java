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

import android.support.annotation.NonNull;
import android.util.Log;
import com.jins_jp.meme.MemeRealtimeData;

/**
 * Created by eneim on 2/5/17.
 */

class MemeRealTimeDataFilter {

  private static final float lowerFactor = 0.70f;
  private static final float higherFactor = 1.30f;

  private final int IDLE_TIME; // 1000 milliseconds

  private final Source cmdSource;
  private Command lastCmd;
  private long lastCmdTimeStamp;

  // stable state, may need to re-calibrate if user move
  private final GyroData calibGyroData;
  // stable acceleration, may need to re-calibrate if user move
  // private final AccelData calibAccelData;

  MemeRealTimeDataFilter(@NonNull Source cmdSource, @NonNull GyroData calibGyroData) {
    this.cmdSource = cmdSource;
    if (cmdSource == Source.HEAD) {
      IDLE_TIME = 850;  // TODO optimize this value
    } else {
      IDLE_TIME = 150;  // TODO optimize this value
    }

    this.calibGyroData = calibGyroData;
    // this.calibAccelData = calibAccelData;
  }

  @NonNull final Command getLastCmd() {
    return lastCmd;
  }

  final Command update(MemeRealtimeData data) {
    if (isWaiting()) {
      lastCmd = Command.of(this.cmdSource, Action.IDLE);
      return lastCmd;
    } else {
      switch (cmdSource) {
        case EYE:
          // TODO 最適化
          if (data.getBlinkStrength() > 30 || data.getBlinkSpeed() > 50) {
            return setCommand(Command.of(cmdSource, Action.EYE_BLINK));
          } else if (data.getEyeMoveLeft() > 0) {
            return setCommand(Command.of(cmdSource, Action.EYE_TURN_LEFT));
          } else if (data.getEyeMoveRight() > 0) {
            return setCommand(Command.of(cmdSource, Action.EYE_TURN_RIGHT));
          } else if (data.getEyeMoveUp() > 0) {
            return setCommand(Command.of(cmdSource, Action.EYE_TURN_UP));
          } else if (data.getEyeMoveDown() > 0) {
            return setCommand(Command.of(cmdSource, Action.EYE_TURN_DOWN));
          } else {
            return setCommand(Command.of(cmdSource, Action.IDLE));
          }
        case HEAD:
          float pitchDiff = Math.abs(data.getPitch() - calibGyroData.getPitch());
          float rollDiff = Math.abs(data.getRoll() - calibGyroData.getRoll());
          float rawYawDiff = getRawYawDiff(data.getYaw());
          float yawDiff = Math.abs(rawYawDiff);
          Log.i("GYRO:DIFF", "update: " + (yawDiff + rollDiff + pitchDiff));
          if (yawDiff + rollDiff + pitchDiff > 10 /* magic number, TODO: calibrate this */) {
            float maxDiff = Math.max(yawDiff, Math.max(pitchDiff, rollDiff));
            if (yawDiff == maxDiff) {
              if (rawYawDiff < -25) {
                return setCommand(Command.of(cmdSource, Action.YAW_LEFT));
              } else if (rawYawDiff > 25) {
                return setCommand(Command.of(cmdSource, Action.YAW_RIGHT));
              } else {
                return setCommand(Command.of(cmdSource, Action.IDLE));
              }
            } else if (pitchDiff == maxDiff) {
              if (data.getPitch() > calibGyroData.getPitch() * higherFactor) {
                return setCommand(Command.of(cmdSource, Action.PITCH_FORWARD));
              } else if (data.getPitch() < calibGyroData.getPitch() * lowerFactor) {
                return setCommand(Command.of(cmdSource, Action.PITCH_BACKWARD));
              } else {
                return setCommand(Command.of(cmdSource, Action.IDLE));
              }
            } else {
              if (data.getRoll() > calibGyroData.getRoll() * higherFactor) {
                return setCommand(Command.of(cmdSource, Action.ROLL_LEFT));
              } else if (data.getRoll() < calibGyroData.getRoll() * lowerFactor) {
                return setCommand(Command.of(cmdSource, Action.ROLL_RIGHT));
              } else {
                return setCommand(Command.of(cmdSource, Action.IDLE));
              }
            }
          } else {
            return setCommand(Command.of(cmdSource, Action.IDLE));
          }
        default:
          return lastCmd;
      }
    }
  }

  // private implementations
  private boolean isWaiting() {
    return System.nanoTime() / (double) 1_000_000 - lastCmdTimeStamp < IDLE_TIME;
  }

  private Command setCommand(Command command) {
    lastCmd = command;
    lastCmdTimeStamp = System.nanoTime() / 1_000_000;
    return lastCmd;
  }

  // just yaw
  private float getRawYawDiff(float yaw) {
    float stable = calibGyroData.getYaw();
    if (stable > 180) {
      stable = stable - 360;
    }

    if (yaw > 180) {
      yaw = yaw - 360;
    }

    return yaw - stable;
  }
}
