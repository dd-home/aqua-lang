package com.imkiva.aqua.tree.concrete;

import java.util.List;

public class ApplyExpr extends Expr {
    public Expr applicable;
    public List<Expr> arguments;
}
