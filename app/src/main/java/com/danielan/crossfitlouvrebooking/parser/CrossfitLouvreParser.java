package com.danielan.crossfitlouvrebooking.parser;

import com.danielan.crossfitlouvrebooking.holder.Creneau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Daniel AN on 13/09/2015.
 */
public final class CrossfitLouvreParser {

    private CrossfitLouvreParser() {
    }

    public static Map<String, TreeSet<Creneau>> parseWeeklyContent(String content) {
        Map<String, TreeSet<Creneau>> creneaux = new HashMap<String, TreeSet<Creneau>>();
        String[] splittedByDate = content.split("ladate");
        String[] splittedByCrenoD = content.split("horaireD");
        String[] splittedByCrenoF = content.split("horaireF");
        for (int i = 1; i < splittedByDate.length; i++) {
            String ladate = splittedByDate[i].substring(3, 13);
            final Creneau lecreno = new Creneau(splittedByCrenoD[i].substring(3, 8), splittedByCrenoF[i].substring(3, 8));
            if (!creneaux.containsKey(ladate)) {
                creneaux.put(ladate, new TreeSet<Creneau>() {{
                    add(lecreno);
                }});
            } else {
                creneaux.get(ladate).add(lecreno);
            }
        }

        return creneaux;
    }
}
