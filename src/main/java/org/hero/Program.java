package org.hero;

import org.hero.chatai.LocalAi;
import org.hero.chatai.Llama3b;
import org.hero.chatgui.Client;
import org.hero.chatgui.ScannerInput;

public class Program {

    public static LocalAi ai;
    public static Client gui;

    public Program() {
        gui = new ScannerInput();
        ai = new Llama3b();
    }

    public void launch() {
        gui.launchApplication();
    }
}
