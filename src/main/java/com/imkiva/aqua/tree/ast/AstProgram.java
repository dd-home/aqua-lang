package com.imkiva.aqua.tree.ast;

import java.util.List;

public class AstProgram extends AstNode {
    public List<AstStatement> statements;

    public AstProgram(List<AstStatement> statements) {
        this.statements = statements;
    }
}
