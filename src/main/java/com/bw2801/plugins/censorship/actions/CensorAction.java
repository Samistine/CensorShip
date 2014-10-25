package com.bw2801.plugins.censorship.actions;

import java.util.List;

public class CensorAction {

    public final String word;

    public final List<String> commands;

    public final int penaltyPoints;
    public final int damage;

    public final Action action;

    public CensorAction(String word, List<String> commands, int penaltyPoints, int damage, Action action) {
        this.word = word;
        this.commands = commands;
        this.penaltyPoints = penaltyPoints;
        this.damage = damage;
        this.action = action;
    }
}
