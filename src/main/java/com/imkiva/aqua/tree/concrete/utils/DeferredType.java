package com.imkiva.aqua.tree.concrete.utils;

import com.imkiva.aqua.tree.ast.AstExpr;
import com.imkiva.aqua.tree.common.Name;
import com.imkiva.aqua.tree.common.Type;

public final class DeferredType extends Type {
    public interface DeferredTypeResolver {
        Type resolveTypeExpr(DBICounter dbi, AstExpr expr);
    }

    public AstExpr deferredWith;

    public DeferredType(AstExpr expr) {
        super(new Name("- Type Deferred with " + expr.toString()));
        this.deferredWith = expr;
    }

    public void resolve(DBICounter dbi, DeferredTypeResolver resolver) {
        assign(resolver.resolveTypeExpr(dbi, deferredWith));
    }
}
