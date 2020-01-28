package com.imkiva.aqua.tree.ast;

import com.imkiva.aqua.tree.common.Name;

import java.util.List;

public abstract class AstTele extends AstNode {
    public static class TeleLit extends AstTele {
        public Name literal;

        public TeleLit(Name lit) {
            this.literal = lit;
        }
    }

    public static class TeleExpr extends AstTele {
        public AstExpr expr;

        public TeleExpr(AstExpr expr) {
            this.expr = expr;
        }
    }
}
