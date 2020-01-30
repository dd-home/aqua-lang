package com.imkiva.aqua.tree.common;

public class Type {
    public static final Type TYPE_NO_NEED = new Type(new Name("- Type No Need -"));

    public Name typeName;
    public Type resultType;

    public Type(Type t) {
        assign(t);
    }

    public Type(Name typeName) {
        this.typeName = typeName;
    }

    public boolean isApplicable() {
        return resultType != null;
    }

    public void assign(Type t) {
        this.typeName = t.typeName;
        this.resultType = t.resultType;
    }

    @Override
    public String toString() {
        if (resultType != null) {
            return typeName.name + " -> " + resultType.toString();
        }
        return typeName.name;
    }
}
