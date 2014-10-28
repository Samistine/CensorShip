package com.bw2801.plugins.censorship;

import com.bw2801.plugins.censorship.actions.Action;
import com.bw2801.plugins.censorship.actions.ReplaceAction;
import com.bw2801.plugins.censorship.actions.ReplaceActionManager;
import java.util.ArrayList;

public class WordHandler {

    public static boolean addWord(String word, String replace, String action, String method, int dmg, int pp) {
        if (getAction(word) != null) return false;

        try {
            Action.valueOf(action.toUpperCase());
        } catch (Exception ex) {
            return false;
        }

        ReplaceAction ra = new ReplaceAction(Config.getWordSaveFile(), word, replace, method, new ArrayList<String>(), new ArrayList<String>(), dmg, pp, Action.valueOf(action.toUpperCase()));
        return ReplaceActionManager.add(ra);
    }

    public static boolean removeWord(String word) {
        ReplaceAction ra = getAction(word);
        if (ra == null) return false;
        return ReplaceActionManager.remove(ra);
    }

    public static boolean addException(String word, String exception) {
        ReplaceAction ra = getAction(word);
        if (ra == null) return false;
        return ra.exceptions.add(word);
    }

    public static boolean removeException(String word, String exception) {
        ReplaceAction ra = getAction(word);
        if (ra == null) return false;
        return ra.exceptions.remove(word);
    }

    public static boolean updateWord(String word, String replace, String action, String method, int dmg, int pp) {
        ReplaceAction ra = getAction(word);
        if (ra == null) return false;

        ra.replace = replace;

        try {
            ra.action = Action.valueOf(action.toUpperCase());
        } catch (Exception ex) {
            return false;
        }

        ra.method = method;
        ra.damage = dmg;
        ra.penaltyPoints = pp;

        return true;
    }

    private static ReplaceAction getAction(String word) {
        for (ReplaceAction ra : ReplaceActionManager.getActions()) {
            if (ra.word.equals(word)) {
                return ra;
            }
        }
        return null;
    }
}
