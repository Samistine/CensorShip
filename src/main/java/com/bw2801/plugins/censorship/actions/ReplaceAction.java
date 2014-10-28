package com.bw2801.plugins.censorship.actions;

import java.util.List;
import java.util.Objects;

public class ReplaceAction {

    public final String file;

    public final String word;
    public String replace;

    public String method;

    public int damage;
    public int penaltyPoints;

    public final List<String> exceptions;
    public final List<String> commands;

    public Action action;

    public ReplaceAction(String file, String word, String replace, String method, List<String> exceptions, List<String> commands, int damage, int penaltyPoints, Action action) {
        this.file = file;
        this.word = word;
        this.replace = replace;
        this.exceptions = exceptions;
        this.damage = damage;
        this.penaltyPoints = penaltyPoints;
        this.commands = commands;
        this.action = action;
        this.method = method;
    }

    @Override
    public String toString() {
        return "ReplaceAction{" + "word=" + word + ", replace=" + replace + ", method=" + method + ", damage=" + damage + ", penaltyPoints=" + penaltyPoints + ", exceptions=" + exceptions + ", commands=" + commands + ", action=" + action + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.file);
        hash = 71 * hash + Objects.hashCode(this.word);
        hash = 71 * hash + Objects.hashCode(this.replace);
        hash = 71 * hash + Objects.hashCode(this.method);
        hash = 71 * hash + this.damage;
        hash = 71 * hash + this.penaltyPoints;
        hash = 71 * hash + Objects.hashCode(this.exceptions);
        hash = 71 * hash + Objects.hashCode(this.commands);
        hash = 71 * hash + Objects.hashCode(this.action);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReplaceAction other = (ReplaceAction) obj;
        if (!Objects.equals(this.file, other.file))
            return false;
        if (!Objects.equals(this.word, other.word))
            return false;
        if (!Objects.equals(this.replace, other.replace))
            return false;
        if (!Objects.equals(this.method, other.method))
            return false;
        if (this.damage != other.damage)
            return false;
        if (this.penaltyPoints != other.penaltyPoints)
            return false;
        if (!Objects.equals(this.exceptions, other.exceptions))
            return false;
        if (!Objects.equals(this.commands, other.commands))
            return false;
        return this.action == other.action;
    }
}