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

package im.ene.mxmo.presentation.trial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.ene.mxmo.R;

/**
 * Created by eneim on 2/20/17.
 */

public class MemeFoundDialogFragment extends AppCompatDialogFragment {

  static final String TAG = "MemeFoundDialogFragment";

  public static MemeFoundDialogFragment newInstance() {
    Bundle args = new Bundle();
    MemeFoundDialogFragment fragment = new MemeFoundDialogFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public int getTheme() {
    return R.style.Theme_AppCompat_Dialog_Alert;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }
}
