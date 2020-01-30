package com.imkiva.aqua.tree.concrete;

import com.imkiva.aqua.tree.common.NamePair;
import com.imkiva.aqua.tree.common.Type;

import java.util.List;

public class LambdaExpr extends Expr {
    public List<NamePair> lambdaTeles;
    public Type lambdaType;
    public ApplicableBody lambdaBody;
}
