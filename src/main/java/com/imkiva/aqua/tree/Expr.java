package com.imkiva.aqua.tree;

import java.util.List;

public class Expr extends Node {
    public Expr typed;

    public static class App extends Expr {
        public Atom atom;
        public List<Argument> arguments;
    }

    public static class Lam extends Expr {
        public List<Tele> teles;
        public Expr body;
    }

    public boolean isTyped() {
        return typed != null;
    }
}
