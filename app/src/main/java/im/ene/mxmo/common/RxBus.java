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

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import io.reactivex.Observable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by eneim on 2/24/17.
 */

public class RxBus {

  private final Relay bus = PublishRelay.create();
  private final Map<Class<? extends Event>, Observable> busCache = new LinkedHashMap<>();

  private static volatile RxBus singleton;

  public static RxBus getBus() {
    if (singleton == null) {
      synchronized (RxBus.class) {
        if (singleton == null) {
          singleton = new RxBus();
        }
      }
    }

    return singleton;
  }

  @SuppressWarnings("unchecked") public <E extends Event> void send(final E event) {
    bus.accept(event);
  }

  // synchronized to prevent observable from being created in wrong threads.
  public synchronized <E extends Event> Observable<E> observe(Class<E> clazz) {
    //noinspection unchecked
    Observable<E> observable = busCache.get(clazz);
    //noinspection Java8MapApi
    if (observable == null) {
      //noinspection unchecked
      observable = bus.filter(o -> o.getClass().equals(clazz));
      busCache.put(clazz, observable);
    }

    return observable;
  }
}
