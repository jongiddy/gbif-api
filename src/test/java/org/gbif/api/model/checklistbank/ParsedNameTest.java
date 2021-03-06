package org.gbif.api.model.checklistbank;

import org.gbif.api.SerdeTestUtils;
import org.gbif.api.vocabulary.NamePart;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.Rank;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ParsedNameTest {

  @Test
  public void testCanonicalName() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Abies");
    assertEquals("Abies", pn.canonicalName());

    pn.setSpecificEpithet("alba");
    assertEquals("Abies alba", pn.canonicalName());

    pn = new ParsedName();
    pn.setGenusOrAbove("Abies");
    pn.setSpecificEpithet("alba");
    pn.setRank(Rank.VARIETY);
    pn.setAuthorship("Mill.");
    pn.setBracketAuthorship("Carl.");
    pn.setNotho(NamePart.GENERIC);
    pn.setInfraSpecificEpithet("alpina");
    pn.setYear("1887");
    pn.setSensu("Döring");
    pn.setRemarks("lost");
    pn.setNomStatus("nom. illeg.");
    pn.setAuthorsParsed(true);

    assertEquals("Abies alba alpina", pn.canonicalName());
    assertEquals("×Abies alba var. alpina", pn.canonicalNameWithMarker());
    assertEquals("×Abies alba var. alpina (Carl.) Mill., 1887", pn.canonicalNameComplete());
    assertEquals("Abies alba", pn.canonicalSpeciesName());
    assertEquals("×Abies alba var. alpina (Carl.) Mill., 1887 Döring, nom. illeg. [lost]", pn.fullName());

    pn.setGenusOrAbove(null);
    assertEquals("alba alpina", pn.canonicalName());
    assertEquals("alba var. alpina", pn.canonicalNameWithMarker());
    assertEquals("alba var. alpina (Carl.) Mill., 1887", pn.canonicalNameComplete());
    assertNull(pn.canonicalSpeciesName());
    assertEquals("alba var. alpina (Carl.) Mill., 1887 Döring, nom. illeg. [lost]", pn.fullName());
  }

  @Test
  public void testTerminalEpithet() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Abies");
    assertNull(pn.getTerminalEpithet());

    pn.setInfraSpecificEpithet("abieta");
    assertEquals("abieta", pn.getTerminalEpithet());

    pn.setSpecificEpithet("vulgaris");
    assertEquals("abieta", pn.getTerminalEpithet());

    pn.setInfraSpecificEpithet(null);
    assertEquals("vulgaris", pn.getTerminalEpithet());

    pn.setGenusOrAbove(null);
    assertEquals("vulgaris", pn.getTerminalEpithet());
  }

  @Test
  public void testIndet() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Abies");
    assertFalse(pn.isIndetermined());

    pn.setRank(Rank.SPECIES);
    assertTrue(pn.isIndetermined());

    pn.setSpecificEpithet("vulgaris");
    assertFalse(pn.isIndetermined());

    pn.setRank(Rank.SUBSPECIES);
    assertTrue(pn.isIndetermined());

    pn.setInfraSpecificEpithet("kingkong");
    assertFalse(pn.isIndetermined());

    for (Rank r : Rank.values()) {
      if (r.isInfraspecific()) {
        assertFalse(r.toString(), pn.isIndetermined());
      }
    }

    pn.setInfraSpecificEpithet(null);
    for (Rank r : Rank.values()) {
      if (r.isInfraspecific()) {
        assertTrue(r.toString(), pn.isIndetermined());
      }
    }

    pn.setRank(Rank.SUBGENUS);
    pn.setSpecificEpithet(null);
    assertTrue(pn.isIndetermined());

    pn.setInfraGeneric("Mysubgenus");
    assertFalse(pn.isIndetermined());
  }

  @Test
  public void testCanonicalAscii() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Abies");
    pn.setSpecificEpithet("vülgårîs");
    pn.setInfraSpecificEpithet("æbiéñtø");
    assertEquals("Abies vulgaris aebiento", pn.canonicalName());
  }

  @Test
  public void testUnparsableCanonical() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setScientificName("? hostilis Gravenhorst, 1829");
    pn.setType(NameType.PLACEHOLDER);
    assertNull(pn.canonicalName());
  }

  /**
   * http://dev.gbif.org/issues/browse/POR-2624
   */
  @Test
  public void testSubgenus() throws Exception {
    // Brachyhypopomus (Odontohypopomus) Sullivan, Zuanon & Cox Fernandes, 2013
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Brachyhypopomus");
    pn.setInfraGeneric("Odontohypopomus");
    pn.setAuthorship("Sullivan, Zuanon & Cox Fernandes");
    pn.setYear("2013");
    assertEquals("Odontohypopomus", pn.canonicalName());
    assertEquals("Odontohypopomus", pn.canonicalNameWithMarker());
    assertEquals("Brachyhypopomus (Odontohypopomus) Sullivan, Zuanon & Cox Fernandes, 2013", pn.canonicalNameComplete());
    // with given rank marker it is shown instead of brackets
    pn.setRank(Rank.SUBGENUS);
    assertEquals("Odontohypopomus", pn.canonicalName());
    assertEquals("subgen. Odontohypopomus", pn.canonicalNameWithMarker());
    assertEquals("Brachyhypopomus subgen. Odontohypopomus Sullivan, Zuanon & Cox Fernandes, 2013", pn.canonicalNameComplete());

    // Achillea sect. Ptarmica (Mill.) W.D.J.Koch
    pn = new ParsedName();
    pn.setGenusOrAbove("Achillea");
    pn.setInfraGeneric("Ptarmica");
    pn.setAuthorship("W.D.J.Koch");
    pn.setBracketAuthorship("Mill.");

    assertEquals("Ptarmica", pn.canonicalName());
    assertEquals("Achillea (Ptarmica) (Mill.) W.D.J.Koch", pn.canonicalNameComplete());
    pn.setRank(Rank.SECTION);
    assertEquals("Achillea sect. Ptarmica (Mill.) W.D.J.Koch", pn.canonicalNameComplete());
  }

  @Test
  public void testBuildName() throws Exception {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Pseudomonas");
    assertBuildName(pn, "Pseudomonas");

    pn.setSpecificEpithet("syringae");
    assertBuildName(pn, "Pseudomonas syringae");

    pn.setAuthorsParsed(true);
    pn.setAuthorship("Van Hall");
    assertBuildName(pn, "Pseudomonas syringae Van Hall", "Pseudomonas syringae", "Pseudomonas syringae Van Hall", "Pseudomonas syringae");

    pn.setYear("1904");
    assertBuildName(pn, "Pseudomonas syringae Van Hall, 1904", "Pseudomonas syringae", "Pseudomonas syringae Van Hall, 1904", "Pseudomonas syringae");

    pn.setBracketAuthorship("Carl.");
    assertBuildName(pn, "Pseudomonas syringae (Carl.) Van Hall, 1904", "Pseudomonas syringae", "Pseudomonas syringae (Carl.) Van Hall, 1904", "Pseudomonas syringae");

    pn.setRank(Rank.PATHOVAR);
    pn.setInfraSpecificEpithet("aceris");
    pn.setBracketAuthorship(null);
    assertBuildName(pn, "Pseudomonas syringae pv. aceris Van Hall, 1904", "Pseudomonas syringae aceris", "Pseudomonas syringae pv. aceris Van Hall, 1904", "Pseudomonas syringae pv. aceris");

    pn.setStrain("CFBP 2339");
    assertBuildName(pn, "Pseudomonas syringae pv. aceris Van Hall, 1904 CFBP 2339", "Pseudomonas syringae aceris", "Pseudomonas syringae pv. aceris Van Hall, 1904 CFBP 2339", "Pseudomonas syringae pv. aceris CFBP 2339");

    pn.setYear(null);
    pn.setAuthorship(null);
    assertBuildName(pn, "Pseudomonas syringae pv. aceris CFBP 2339", "Pseudomonas syringae aceris", "Pseudomonas syringae pv. aceris CFBP 2339", "Pseudomonas syringae pv. aceris CFBP 2339");

    pn = new ParsedName();
    pn.setGenusOrAbove("Abax");
    pn.setSpecificEpithet("carinatus");
    pn.setInfraSpecificEpithet("carinatus");
    pn.setBracketAuthorship("Duftschmid");
    pn.setBracketYear("1812");
    pn.setRank(Rank.UNRANKED);
    assertBuildName(pn, "Abax carinatus carinatus", "Abax carinatus carinatus", "Abax carinatus carinatus", "Abax carinatus carinatus");

    pn.setRank(null);
    assertBuildName(pn, "Abax carinatus carinatus", "Abax carinatus carinatus", "Abax carinatus carinatus", "Abax carinatus carinatus");

    pn.setInfraSpecificEpithet("urinatus");
    assertBuildName(pn, "Abax carinatus urinatus (Duftschmid, 1812)", "Abax carinatus urinatus", "Abax carinatus urinatus (Duftschmid, 1812)", "Abax carinatus urinatus");

    pn.setRank(null);
    assertBuildName(pn, "Abax carinatus urinatus (Duftschmid, 1812)", "Abax carinatus urinatus", "Abax carinatus urinatus (Duftschmid, 1812)", "Abax carinatus urinatus");

    pn.setRank(Rank.SUBSPECIES);
    assertBuildName(pn, "Abax carinatus subsp. urinatus (Duftschmid, 1812)", "Abax carinatus urinatus", "Abax carinatus subsp. urinatus (Duftschmid, 1812)", "Abax carinatus subsp. urinatus");

    pn = new ParsedName();
    pn.setGenusOrAbove("Polypodium");
    pn.setSpecificEpithet("vulgare");
    pn.setInfraSpecificEpithet("mantoniae");
    pn.setBracketAuthorship("Rothm.");
    pn.setAuthorship("Schidlay");
    pn.setRank(Rank.SUBSPECIES);
    pn.setNotho(NamePart.INFRASPECIFIC);
    assertBuildName(pn, "Polypodium vulgare nothosubsp. mantoniae (Rothm.) Schidlay",
        "Polypodium vulgare mantoniae",
        "Polypodium vulgare nothosubsp. mantoniae (Rothm.) Schidlay",
        "Polypodium vulgare nothosubsp. mantoniae");
  }

  @Test
  public void testJsonSerde() throws IOException {
    ParsedName pn = new ParsedName();
    pn.setGenusOrAbove("Brachyhypopomus");
    pn.setInfraGeneric("Odontohypopomus");
    pn.setAuthorship("Sullivan, Zuanon & Cox Fernandes");
    pn.setYear("2013");
    pn.setRank(Rank.SUBGENUS);
    SerdeTestUtils.testSerDe(pn, ParsedName.class);

    pn = new ParsedName();
    pn.setKey(123);
    pn.setType(NameType.SCIENTIFIC);
    pn.setGenusOrAbove("Abax");
    pn.setSpecificEpithet("carinatus");
    pn.setInfraSpecificEpithet("carinatus");
    pn.setBracketAuthorship("Duftschmid");
    pn.setBracketYear("1812");
    pn.setRank(Rank.INFRASUBSPECIFIC_NAME);
    SerdeTestUtils.testSerDe(pn, ParsedName.class);

    pn.setAuthorship("Carlos & Kamera");
    pn.setYear("1999");

    for (Rank rank : Rank.values()) {
      pn.setRank(rank);
      // UNRANKED intentionally becomes null!
      if (rank.notOtherOrUnknown()) {
        String json = SerdeTestUtils.testSerDe(pn, ParsedName.class);
        assertTrue(json.contains("\"rankMarker\""));
      }
    }
    // check that rankMarker=null gets removed from json
    pn.setRank(null);
    String json = SerdeTestUtils.serialize(pn);
    assertFalse(json.contains("\"rankMarker\""));
  }

  /**
   * assert all build name methods return the same string
   * @param pn
   * @param name expected string
   */
  private void assertBuildName(ParsedName pn, String name) {
    assertEquals(name, pn.fullName());
    assertEquals(name, pn.canonicalName());
    assertEquals(name, pn.canonicalNameComplete());
    assertEquals(name, pn.canonicalNameWithMarker());
  }

  private void assertBuildName(ParsedName pn, String full, String canonical, String canonicalComplete, String canonicalMarker) {
    assertEquals("wrong fullName", full, pn.fullName());
    assertEquals("wrong canonicalName", canonical, pn.canonicalName());
    assertEquals("wrong canonicalNameComplete", canonicalComplete, pn.canonicalNameComplete());
    assertEquals("wrong canonicalNameWithMarker", canonicalMarker, pn.canonicalNameWithMarker());
  }
}
