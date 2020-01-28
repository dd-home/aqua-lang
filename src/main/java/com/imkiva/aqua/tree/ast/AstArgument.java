package com.imkiva.aqua.tree.ast;

public class AstArgument extends AstNode {
    public AstAtom explicitAtom;

    public AstArgument(AstAtom explicitAtom) {
        this.explicitAtom = explicitAtom;
    }
}
