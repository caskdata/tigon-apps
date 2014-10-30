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


package co.cask.tigon.apps.clickstreamflow;

import co.cask.tigon.test.SQLFlowTestBase;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
* ClickStreamFlowTest
*/
public class ClickStreamFlowTest extends SQLFlowTestBase {
  private static final int MAX = 10;
  private static CountDownLatch latch;

  @BeforeClass
  public static void beforeClass() throws Exception {
    setupFlow(ClickStreamFlow.class);
    latch = setExpectedOutputCount(4);
  }

  @Test
  public void testClickStreamFlow() throws Exception {
    // Generating mock view event data packets
    List<String> viewDataList = Lists.newArrayList();
    for (int i = 1; i <= MAX; i++) {
      JsonObject bodyJson = new JsonObject();
      JsonArray dataArray = new JsonArray();
      dataArray.add(new JsonPrimitive(Integer.toString(i)));
      dataArray.add(new JsonPrimitive(Integer.toString(i * 10)));
      dataArray.add(new JsonPrimitive(Integer.toString(0)));
      dataArray.add(new JsonPrimitive(Integer.toString(1)));
      dataArray.add(new JsonPrimitive(Integer.toString(2)));
      dataArray.add(new JsonPrimitive("PageName" + i));
      dataArray.add(new JsonPrimitive("'0':'LinkName0' - '1':'LinkName1' - '2':'LinkName2'"));
      bodyJson.add("data", dataArray);
      viewDataList.add(bodyJson.toString());
    }
    // Generating mock click event data packets
    List<String> clickDataList = Lists.newArrayList();
    for (int i = 1; i <= MAX / 2; i++) {
      JsonObject bodyJson = new JsonObject();
      JsonArray dataArray = new JsonArray();
      dataArray.add(new JsonPrimitive(Integer.toString(i * 2)));
      dataArray.add(new JsonPrimitive(Integer.toString(i * 2 * 15 + 260)));
      dataArray.add(new JsonPrimitive(Integer.toString(i % 3)));
      bodyJson.add("data", dataArray);
      clickDataList.add(bodyJson.toString());
    }
    List<Map.Entry<String, List<String>>> dataStreams = Lists.newArrayList();
    dataStreams.add(new AbstractMap.SimpleEntry<String, List<String>>("viewStream", viewDataList));
    dataStreams.add(new AbstractMap.SimpleEntry<String, List<String>>("clickStream", clickDataList));
    ingestData(dataStreams);
    latch.await(60, TimeUnit.SECONDS);
    int dataPacketCounter = 4;
    ClickInfo dataPacket;
    while ((dataPacket = getDataPacket(ClickInfo.class)) != null) {
      //Asserting that referrerPageInfo is always of the format "PageName<refID>"
      Assert.assertEquals("PageName" + dataPacket.refID, dataPacket.referrerPageInfo);
      //Asserting that the linkName is as expected based on the value of refID
      Assert.assertEquals("LinkName" + (dataPacket.refID / 2) % 3, dataPacket.getLinkName());
      //Asserting on the value of time which is equal to that of the value generated for clickTime
      Assert.assertEquals(dataPacket.refID * 15 + 260, dataPacket.time);
      dataPacketCounter = dataPacketCounter - 1;
    }
    Assert.assertEquals(0, dataPacketCounter);
  }
}

