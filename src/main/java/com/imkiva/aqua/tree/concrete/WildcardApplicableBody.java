package com.imkiva.aqua.tree.concrete;

public class WildcardApplicableBody extends ApplicableBody {
    public Expr bodyExpr;

    public WildcardApplicableBody(Expr bodyExpr) {
        this.bodyExpr = bodyExpr;
    }
}
