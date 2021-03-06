/*
 * Copyright © 2014 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.tigon.apps.adnetworkflow;

import com.google.common.collect.ImmutableList;

/**
 * List of advertisers bidding on user-views.
 */
public final class Advertisers {

  public static final String SPORTS = "SPORTS";

  public static final String TRAVEL = "TRAVEL";

  public static final String MUSIC = "MUSIC";

  public static Iterable<String> getAll() {
    return ImmutableList.of(SPORTS, TRAVEL, MUSIC);
  }
}
