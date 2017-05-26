/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veyndan.paper.reddit;

import com.google.gson.JsonParseException;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.veyndan.paper.reddit.api.reddit.json.adapter.RuntimeJsonAdapterFactory;

import junit.framework.TestCase;

import java.io.IOException;

public final class RuntimeJsonAdapterFactoryTest extends TestCase {

  public void testRuntimeTypeAdapter() throws IOException {
    RuntimeJsonAdapterFactory<BillingInstrument> rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class)
        .registerSubtype(CreditCard.class);
    Moshi moshi = new Moshi.Builder()
        .add(rta)
        .build();

    CreditCard original = new CreditCard("Jesse", 234);
    assertEquals("{\"type\":\"CreditCard\",\"cvv\":234,\"ownerName\":\"Jesse\"}",
        moshi.adapter(BillingInstrument.class).toJson(original));
    BillingInstrument deserialized = moshi.adapter(BillingInstrument.class).fromJson(
        "{\"type\":\"CreditCard\",\"cvv\":234,\"ownerName\":\"Jesse\"}");
    assertEquals("Jesse", deserialized.ownerName);
    assertTrue(deserialized instanceof CreditCard);
  }

  public void testRuntimeTypeIsBaseType() throws IOException {
    JsonAdapter.Factory rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class)
        .registerSubtype(BillingInstrument.class);
    Moshi moshi = new Moshi.Builder()
        .add(rta)
        .build();

    BillingInstrument original = new BillingInstrument("Jesse");
    assertEquals("{\"type\":\"BillingInstrument\",\"ownerName\":\"Jesse\"}",
        moshi.adapter(BillingInstrument.class).toJson(original));
    BillingInstrument deserialized = moshi.adapter(BillingInstrument.class).fromJson(
        "{\"type\":\"BillingInstrument\",\"ownerName\":\"Jesse\"}");
    assertEquals("Jesse", deserialized.ownerName);
  }

  public void testNullBaseType() {
    try {
      RuntimeJsonAdapterFactory.of(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testNullTypeFieldName() {
    try {
      RuntimeJsonAdapterFactory.of(BillingInstrument.class, null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testNullSubtype() {
    RuntimeJsonAdapterFactory<BillingInstrument> rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class);
    try {
      rta.registerSubtype(null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testNullLabel() {
    RuntimeJsonAdapterFactory<BillingInstrument> rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class);
    try {
      rta.registerSubtype(CreditCard.class, null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testDuplicateSubtype() {
    RuntimeJsonAdapterFactory<BillingInstrument> rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class);
    rta.registerSubtype(CreditCard.class, "CC");
    try {
      rta.registerSubtype(CreditCard.class, "Visa");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDuplicateLabel() {
    RuntimeJsonAdapterFactory<BillingInstrument> rta = RuntimeJsonAdapterFactory.of(
        BillingInstrument.class);
    rta.registerSubtype(CreditCard.class, "CC");
    try {
      rta.registerSubtype(BankTransfer.class, "CC");
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testDeserializeMissingTypeField() {
    JsonAdapter.Factory billingAdapter = RuntimeJsonAdapterFactory.of(BillingInstrument.class)
        .registerSubtype(CreditCard.class);
    Moshi moshi = new Moshi.Builder()
        .add(billingAdapter)
        .build();
    try {
      moshi.adapter(BillingInstrument.class).fromJson("{ownerName:'Jesse'}");
      fail();
    } catch (IOException expected) {
    }
  }

  public void testDeserializeMissingSubtype() {
    JsonAdapter.Factory billingAdapter = RuntimeJsonAdapterFactory.of(BillingInstrument.class)
        .registerSubtype(BankTransfer.class);
    Moshi moshi = new Moshi.Builder()
        .add(billingAdapter)
        .build();
    try {
      moshi.adapter(BillingInstrument.class).fromJson("{type:'CreditCard',ownerName:'Jesse'}");
      fail();
    } catch (IOException expected) {
    }
  }

  public void testSerializeMissingSubtype() {
    JsonAdapter.Factory billingAdapter = RuntimeJsonAdapterFactory.of(BillingInstrument.class)
        .registerSubtype(BankTransfer.class);
    Moshi moshi = new Moshi.Builder()
        .add(billingAdapter)
        .build();
    try {
      moshi.adapter(BillingInstrument.class).toJson(new CreditCard("Jesse", 456));
      fail();
    } catch (JsonParseException expected) {
    }
  }

  public void testSerializeCollidingTypeFieldName() {
    JsonAdapter.Factory billingAdapter = RuntimeJsonAdapterFactory.of(BillingInstrument.class, "cvv")
        .registerSubtype(CreditCard.class);
    Moshi moshi = new Moshi.Builder()
        .add(billingAdapter)
        .build();
    try {
      moshi.adapter(BillingInstrument.class).toJson(new CreditCard("Jesse", 456));
      fail();
    } catch (JsonParseException expected) {
    }
  }

  public void testSerializeWrappedNullValue() throws IOException {
    JsonAdapter.Factory billingAdapter = RuntimeJsonAdapterFactory.of(BillingInstrument.class)
        .registerSubtype(CreditCard.class)
        .registerSubtype(BankTransfer.class);    
    Moshi moshi = new Moshi.Builder()
        .add(billingAdapter)
        .build();
    String serialized = moshi.adapter(BillingInstrumentWrapper.class).toJson(new BillingInstrumentWrapper(null));
    BillingInstrumentWrapper deserialized = moshi.adapter(BillingInstrumentWrapper.class).fromJson(serialized);
    assertNull(deserialized.instrument);
  }

  static class BillingInstrumentWrapper {
    BillingInstrument instrument;
    BillingInstrumentWrapper(BillingInstrument instrument) {
      this.instrument = instrument;
    }
  }

  static class BillingInstrument {
    private final String ownerName;
    BillingInstrument(String ownerName) {
      this.ownerName = ownerName;
    }
  }

  static class CreditCard extends BillingInstrument {
    int cvv;
    CreditCard(String ownerName, int cvv) {
      super(ownerName);
      this.cvv = cvv;
    }
  }

  static class BankTransfer extends BillingInstrument {
    int bankAccount;
    BankTransfer(String ownerName, int bankAccount) {
      super(ownerName);
      this.bankAccount = bankAccount;
    }
  }
}
