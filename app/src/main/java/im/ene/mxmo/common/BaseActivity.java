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

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by eneim on 2/13/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

  @TargetApi(23) protected void maybeRequestLocationPermission() {
    if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
    } else {
      onPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION, true);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.d("PERMISSION", "Succeeded");
        Toast.makeText(BaseActivity.this, "Permission Request Succeed", Toast.LENGTH_SHORT).show();
        onPermissionGranted(permissions[0], true);
      } else {
        Log.d("PERMISSION", "Failed");
        Toast.makeText(BaseActivity.this, "Permission Request Failed", Toast.LENGTH_SHORT).show();
        onPermissionGranted(permissions[0], false);
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  protected void onPermissionGranted(String permission, boolean granted) {

  }
}
