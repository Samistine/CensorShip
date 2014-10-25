/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bw2801.plugins.censorship;

/**
 *
 * @author Benedikt
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] list = {
            "whore",
            "w h o r e",
            "w hor e",
            "who o o o o oo r r rr e",
            "whore.",
            "w hore",
            "whoooooore",
            "who o o oo o ooo oorre",
            "who re!",
            "who re",
            "who.re!",
            "who.re.",
            "w.h.o.r.e",
            "w.h.o.r.e.",
            " whore",
            " whore !",
            "who re?",
            "---",
            "who removed it?",
            "but who removed it?",
            "who are you?"
        };

        for (String list1 : list) {
            String result = CensorUtil.replace(list1, "whore");
            if (result.contains("whore")) {
                System.out.println("found \"whore\" in \"" + list1 + "\" (\"" + result + "\")");
            } else {
                System.out.println("didn't find \"whore\" in \"" + list1 + "\" (\"" + result + "\")");
            }
        }
    }

}
