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
 * Created by eneim on 2/4/17.
 */

public enum Action {

  // Idle
  IDLE,

  // from gyro data
  YAW_LEFT, YAW_RIGHT, PITCH_FORWARD, PITCH_BACKWARD, ROLL_LEFT, ROLL_RIGHT,

  // from accelerator data
  HEAD_TURN_LEFT, HEAD_TURN_RIGHT, HEAD_TURN_FORWARD, HEAD_TURN_BACKWARD,

  // Eye movement
  EYE_TURN_LEFT, EYE_TURN_RIGHT, EYE_TURN_UP, EYE_TURN_DOWN, EYE_BLINK;
}
