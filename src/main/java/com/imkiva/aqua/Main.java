package com.imkiva.aqua;

import com.imkiva.aqua.tree.ast.AstBuilder;
import com.imkiva.aqua.tree.ast.AstProgram;
import com.imkiva.aqua.tree.concrete.Program;
import com.imkiva.aqua.tree.concrete.TreeResolver;

public class Main {
    public static void main(String[] args) {
        String code = "\n" +
                "\\func dbi (a b c : A) : A -> B -> B => \\lam (f : A -> B) => f a\n" +
                "";

        AstProgram astProgram = AstBuilder.build(code);
        Program program = TreeResolver.resolve(astProgram);
        System.out.println(program);
    }
}
