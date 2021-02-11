package com.arxit.geolocindoor.unit;

import com.arxit.geolocindoor.common.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StringStandardisation {

    private static final String raw1 = "È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô,Ç,ç,Ã,ã,Õ,õ";
    private static final String expected1 = "e,e,e,e,u,u,i,i,a,a,o,e,e,e,e,u,u,i,i,a,a,o,c,c,a,a,o,o";

    private static final String raw2 = "çáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
    private static final String expected2 = "caeiouyaeiouyaeiouaeiouaonaeiouyaeiouaonaeiouaeiou";

    private static final String raw3 = "Cafétéria CrOuS";
    private static final String expected3 = "cafeteria crous";

    @Test
    public void replacingAllAccents() {
        assertEquals(expected1,  StringUtils.unaccent(raw1));
        assertEquals(expected2, StringUtils.unaccent(raw2));
        assertEquals(expected3, StringUtils.unaccent(raw3));
    }
}
