package com.bw2801.plugins.censorship.actions;

import com.bw2801.plugins.censorship.Censorship;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReplaceActionManager {

    private static final Set<ReplaceAction> actions = new HashSet<>();
    private static final Map<String, Set<ReplaceAction>> files = new HashMap<>();

    public static boolean add(ReplaceAction ra) {
        actions.add(ra);
        if (!files.containsKey(ra.file)) {
            files.put(ra.file, new HashSet<ReplaceAction>());
        }
        return files.get(ra.file).add(ra);
    }

    public static boolean remove(ReplaceAction ra) {
        actions.remove(ra);
        return files.get(ra.file).remove(ra);
    }

    public static void saveActions() {
        for (String fileName : files.keySet()) {
            try {
                saveFile(fileName);
                Censorship.print("Info", "Successfully saved words to file \"" + fileName + "\".");
            } catch (IOException ex) {
                Censorship.print("Error", "Could not save words to file \"" + fileName + "\".");
            }
        }
    }

    public static void saveFile(String fileName) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray words = new JsonArray();

        for (ReplaceAction action : files.get(fileName)) {
            words.add(createJsonFromWord(action));
        }

        root.add("words", words);

        System.out.println(fileName);
        try (FileOutputStream stream = new FileOutputStream(new File("plugins/CensorShip/words/" + fileName))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            stream.write(gson.toJson(root).getBytes());
            System.out.println(gson.toJson(root));
        }
    }

    public static JsonObject createJsonFromWord(ReplaceAction action) throws IOException {
        JsonObject word = new JsonObject();

        word.addProperty("word", action.word);
        word.addProperty("replace_with", action.replace);
        word.addProperty("action", action.action.name().toLowerCase());
        word.addProperty("method", action.method);

        word.addProperty("damage", action.damage);
        word.addProperty("penalty_points", action.penaltyPoints);

        JsonArray exceptions = new JsonArray();
        for (String exception : action.exceptions) {
            exceptions.add(new JsonPrimitive(exception));
        }
        word.add("exceptions", exceptions);

        JsonArray commands = new JsonArray();
        for (String command : action.commands) {
            commands.add(new JsonPrimitive(command));
        }
        word.add("commands", commands);

        return word;
    }

    public static void loadActions(String jsonFile) {
        Censorship.print("Info", "Trying to parse \"" + jsonFile + "\"");

        files.put(jsonFile, new HashSet<ReplaceAction>());

        String file = "plugins/CensorShip/words/" + jsonFile;

        try {
            JsonParser parser = new JsonParser();
            JsonObject root = (JsonObject) parser.parse(new JsonReader(new FileReader(file)));
            JsonArray words = root.getAsJsonArray("words");

            for (JsonElement element : words) {
                JsonObject block = (JsonObject) element;

                String word = block.get("word").getAsString();
                String replaceWith = block.get("replace_with").getAsString();
                String action = block.get("action").getAsString();
                String method = block.get("method").getAsString();

                int damage = block.get("damage").getAsInt();
                int points = block.get("penalty_points").getAsInt();

                List<String> exceptions = new ArrayList<>();
                for (JsonElement exception : block.get("exceptions").getAsJsonArray()) {
                    exceptions.add(exception.getAsString());
                }

                List<String> commands = new ArrayList<>();
                for (JsonElement command : block.get("commands").getAsJsonArray()) {
                    commands.add(command.getAsString());
                }

                ReplaceAction ra = new ReplaceAction(jsonFile, word, replaceWith, method.toLowerCase(), exceptions, commands, (int) damage, (int) points, Action.valueOf(action.toUpperCase()));
                actions.add(ra);
                files.get(jsonFile).add(ra);
            }
        } catch (FileNotFoundException ex) {
            Censorship.print("Error", "Could not parse \"" + jsonFile + "\"!");
        }

        Censorship.print("Info", "Done parsing \"" + jsonFile + "\".");
    }

    public static Set<ReplaceAction> getActions() {
        return actions;
    }
}
