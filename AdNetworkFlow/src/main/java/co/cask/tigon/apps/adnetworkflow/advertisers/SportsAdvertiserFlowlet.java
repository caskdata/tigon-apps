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

package co.cask.tigon.apps.adnetworkflow.advertisers;

import co.cask.tigon.api.annotation.ProcessInput;
import co.cask.tigon.api.flow.flowlet.AbstractFlowlet;
import co.cask.tigon.api.flow.flowlet.OutputEmitter;
import co.cask.tigon.apps.adnetworkflow.Advertisers;
import co.cask.tigon.apps.adnetworkflow.Bid;
import co.cask.tigon.apps.adnetworkflow.IdData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An advertiser that makes bids on users to show ads related to sports.
 */
public final class SportsAdvertiserFlowlet extends AbstractFlowlet {
  private static final Logger LOG = LoggerFactory.getLogger(SportsAdvertiserFlowlet.class);
  private OutputEmitter<Bid> bidOutputEmitter;

  /**
   * Compute the SportAdvertiser's bid amount for each incoming user id.
   * @param idData the data used to compute the bid amount.
   */
  @ProcessInput(Advertisers.SPORTS)
  @SuppressWarnings("UnusedDeclaration")
  public void process(IdData idData) throws Exception {
    double bidAmount;
    if (idData.getTotalCount() == 0 || (idData.getItemCount()/(double) idData.getTotalCount()) < 0.5) {
      bidAmount = 10;
    } else {
      bidAmount = 0;
    }
    bidOutputEmitter.emit(new Bid(idData.getId(), Advertisers.SPORTS, bidAmount), "userId", idData.getId());
    LOG.info("Bid {} for user {}", bidAmount, idData.getId());
  }
}
