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

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by eneim on 3/4/17.
 */

public interface OnItemClickListener {

  /**
   * Interaction with an item.
   *
   * @param adapter The Adapter which holds current item data
   * @param viewHolder The ViewHolder which renders current item's content
   * @param view The PublicView that is clicked
   * @param pos Position of interacted viewHolder
   * @param posId Corresponded Id
   */
  void onItemClick(RecyclerView.Adapter adapter, RecyclerView.ViewHolder viewHolder, View view,
      int pos, long posId);
}
