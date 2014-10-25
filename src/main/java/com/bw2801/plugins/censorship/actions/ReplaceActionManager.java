package com.bw2801.plugins.censorship.actions;

import com.bw2801.plugins.censorship.Censorship;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReplaceActionManager {

    private static final Set<ReplaceAction> actions = new HashSet<>();

    public static void loadActions(String jsonFile) {
        Censorship.print("Info", "Trying to parse \"" + jsonFile + "\"");

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(jsonFile));

            JSONArray words = (JSONArray) jsonObj.get("words");
            for (Object obj : words) {
                JSONObject sequence = (JSONObject) obj;

                String word = (String) sequence.get("word");
                String replaceWith = (String) sequence.get("replace_with");
                String action = (String) sequence.get("action");

                List<String> exceptions = new ArrayList<>();
                for (Object exception : (JSONArray) sequence.get("exceptions")) {
                    exceptions.add((String) exception);
                }

                List<String> commands = new ArrayList<>();
                for (Object command : (JSONArray) sequence.get("commands")) {
                    commands.add((String) command);
                }

                actions.add(new ReplaceAction(word, replaceWith, exceptions, commands, 0, 0, Action.valueOf(action.toUpperCase())));
                System.out.println(new ReplaceAction(word, replaceWith, exceptions, commands, 0, 0, Action.valueOf(action.toUpperCase())));
            }
        } catch (IOException | ParseException ex) {
            Censorship.print("Error", "Could not parse \"" + jsonFile + "\"!");
        }

        Censorship.print("Info", "Done parsing \"" + jsonFile + "\".");
    }

    public static Set<ReplaceAction> getActions() {
        return actions;
    }
}
