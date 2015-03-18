/*
 * Copyright 2014 Global Biodiversity Information Facility (GBIF)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.util;

import org.gbif.api.vocabulary.Country;
import org.gbif.api.vocabulary.TechnicalInstallationType;
import org.gbif.api.vocabulary.ContactType;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.IdentifierType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VocabularyUtilsTest {

  @Test(expected = IllegalArgumentException.class)
  public void testParseContactType() throws Exception {
    assertEquals(ContactType.AUTHOR, VocabularyUtils.parseContactType("author"));
    assertEquals(ContactType.ADMINISTRATIVE_POINT_OF_CONTACT,
      VocabularyUtils.parseContactType("ADMINISTRATIVE POINT_of_CONTACT"));
    VocabularyUtils.parseContactType("bad");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseEndpointType() throws Exception {
    assertEquals(EndpointType.TAPIR, VocabularyUtils.parseEndpointType("Tapir"));
    assertEquals(EndpointType.DWC_ARCHIVE, VocabularyUtils.parseEndpointType("DWC_ARCHIVE"));
    VocabularyUtils.parseEndpointType("bad");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseIdentifierType() throws Exception {
    assertEquals(IdentifierType.LSID, VocabularyUtils.parseIdentifierType("lsid"));
    assertEquals(IdentifierType.DOI, VocabularyUtils.parseIdentifierType("d.o.i."));
    VocabularyUtils.parseIdentifierType("bad");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseTechnicalInstallationType() throws Exception {
    assertEquals(TechnicalInstallationType.IPT_INSTALLATION,
      VocabularyUtils.parseTechnicalInstallationType("ipt installation"));
    VocabularyUtils.parseTechnicalInstallationType("bad");
  }

  @Test
  public void testReflectionLookup() {
    assertEquals(Country.class, VocabularyUtils.lookupVocabulary(Country.class.getName()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReflectionLookupError() {
    VocabularyUtils.lookupVocabulary("WTFMate");
  }
}