package com.imkiva.aqua.tree;

public class Atom extends Node {
    public static class AtomLit extends Atom {
        public Literal literal;

        public AtomLit(Literal literal) {
            this.literal = literal;
        }
    }

    public static class AtomNum extends Atom {
        public int number;

        public AtomNum(int number) {
            this.number = number;
        }
    }
}
