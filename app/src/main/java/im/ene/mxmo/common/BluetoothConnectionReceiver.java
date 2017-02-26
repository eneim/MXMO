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

package im.ene.mxmo.common;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import im.ene.mxmo.common.event.BluetoothConnectionEvent;

/**
 * Created by eneim on 2/24/17.
 */

public class BluetoothConnectionReceiver extends BroadcastReceiver {

  private static final String TAG = "MXMO@BLE";

  @Override public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
      int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
      int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
      Log.d(TAG, "onReceive: " + state + " | " + prevState);
      switch (state) {
        case BluetoothAdapter.STATE_ON:
          Log.i(TAG, "onReceive: STATE_ON");
          RxBus.getBus().send(new BluetoothConnectionEvent(state, prevState));
          break;
        case BluetoothAdapter.STATE_OFF:
          Log.i(TAG, "onReceive: STATE_OFF");
          RxBus.getBus().send(new BluetoothConnectionEvent(state, prevState));
          break;
        default:
          Log.i(TAG, "onReceive: " + state);
          break;
      }
    }
  }
}
