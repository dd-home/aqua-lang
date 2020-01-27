package com.imkiva.aqua.tree;

public class Literal extends Node {
    public static final Literal UNKNOWN = new Literal("_");

    public String name;
    public int nameDBI;

    public Literal(String name) {
        this.name = name;
        this.nameDBI = DBI.UNRESOLVED;
    }

    public boolean isUnknown() {
        return this == UNKNOWN
                || nameDBI == DBI.UNKNOWN
                || "_".equals(name);
    }

    public int getNameDBI() {
        return nameDBI;
    }

    public void setNameDBI(int nameDBI) {
        this.nameDBI = nameDBI;
    }

    public boolean isResolved() {
        return nameDBI != DBI.UNRESOLVED;
    }
}
