package com.imkiva.aqua.tree.ast;

import com.imkiva.aqua.tree.common.Name;

public class AstAtom extends AstNode {
    public static class AtomLit extends AstAtom {
        public Name literal;

        public AtomLit(Name literal) {
            this.literal = literal;
        }
    }

    public static class AtomNum extends AstAtom {
        public int number;

        public AtomNum(int number) {
            this.number = number;
        }
    }
}
