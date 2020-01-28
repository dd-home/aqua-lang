package com.imkiva.aqua.tree.ast;

import java.util.List;

public class AstDefinition extends AstNode {
    public static class Func extends AstDefinition {
        public static class Body extends AstNode {
            public static class WithoutElim extends Body {
                public AstExpr expr;
            }
        }

        public String name;
        public List<AstTele> teles;
        public AstExpr returnExpr;
        public Body body;
    }
}
