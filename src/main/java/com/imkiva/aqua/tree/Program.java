package com.imkiva.aqua.tree;

import java.util.List;

public class Program extends Node {
    public List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }
}
