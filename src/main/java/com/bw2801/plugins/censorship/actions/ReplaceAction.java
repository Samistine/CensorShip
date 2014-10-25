package com.bw2801.plugins.censorship.actions;

import java.util.List;

public class ReplaceAction {

    public final String word;
    public final String replace;

    public final int damage;
    public final int penaltyPoints;

    public final List<String> exceptions;
    public final List<String> commands;

    public final Action action;

    public ReplaceAction(String word, String replace, List<String> exceptions, List<String> commands, int damage, int penaltyPoints, Action action) {
        this.word = word;
        this.replace = replace;
        this.exceptions = exceptions;
        this.damage = damage;
        this.penaltyPoints = penaltyPoints;
        this.commands = commands;
        this.action = action;
    }

    @Override
    public String toString() {
        return "ReplaceAction{" + "word=" + word + ", replace=" + replace + ", damage=" + damage + ", penaltyPoints=" + penaltyPoints + ", exceptions=" + exceptions + ", commands=" + commands + ", action=" + action + '}';
    }
}