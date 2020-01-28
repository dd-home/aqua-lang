package com.imkiva.aqua.tree.ast;

public class AstStatement extends AstNode {
    public AstDefinition def;

    public AstStatement(AstDefinition def) {
        this.def = def;
    }
}
