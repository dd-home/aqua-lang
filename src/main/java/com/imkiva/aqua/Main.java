package com.imkiva.aqua;

import com.imkiva.aqua.tree.Program;
import com.imkiva.aqua.tree.TreeBuilder;
import com.imkiva.aqua.tree.TreeResolver;

public class Main {
    public static void main(String[] args) {
        String code = "今天、写了程序\n" +
                "\\func dbi (a : Int) => \\lam (f : F) => f a\n" +
                "呜哇啊";

        Program program = TreeResolver.resolve(TreeBuilder.build(code));
        System.out.println(program);
    }
}
