package com.imkiva.aqua.tree.concrete;

import com.imkiva.aqua.tree.common.Name;

public class LiteralNameExpr extends LiteralExpr {
    public Name literalName;

    public LiteralNameExpr(Name literalName) {
        this.literalName = literalName;
    }
}
