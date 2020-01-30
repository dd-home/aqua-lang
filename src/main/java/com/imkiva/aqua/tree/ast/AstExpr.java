package com.imkiva.aqua.tree.ast;

import java.util.List;

public class AstExpr extends AstNode {
    public AstExpr typed;

    public static class App extends AstExpr {
        public AstAtom atom;
        public List<AstArgument> arguments;
    }

    public static class Arr extends AstExpr {
        public List<AstExpr> exprs;
    }

    public static class Lam extends AstExpr {
        public List<AstTele> teles;
        public AstExpr returnExpr;
        public AstExpr body;
    }

    public boolean isTyped() {
        return typed != null;
    }
}
