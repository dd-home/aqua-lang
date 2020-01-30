package com.imkiva.aqua.tree.concrete;

import com.imkiva.aqua.tree.ast.*;
import com.imkiva.aqua.tree.common.Name;
import com.imkiva.aqua.tree.common.NamePair;
import com.imkiva.aqua.tree.common.Type;
import com.imkiva.aqua.tree.concrete.utils.DBICounter;
import com.imkiva.aqua.tree.concrete.utils.DeferredType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TreeResolver {
    public static Program resolve(AstProgram program) {
        return new Program(
                program.statements.stream()
                        .map(TreeResolver::resolveStatement)
                        .collect(Collectors.toList())
        );
    }

    private static Statement resolveStatement(AstStatement statement) {
        return resolveDefStatement(statement.def);
    }

    private static DefinitionStatement resolveDefStatement(AstDefinition def) {
        if (def instanceof AstDefinition.Func) {
            return resolveDefFunc(((AstDefinition.Func) def));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static FunctionDefStatement resolveDefFunc(AstDefinition.Func func) {
        DBICounter dbi = new DBICounter(null);
        FunctionDefStatement function = new FunctionDefStatement();

        // function can be regarded as an combination of a lambda and a string
        function.functionName = new Name(func.name);
        function.lambdaExpr = resolveLambda(dbi, func.teles,
                func.returnExpr, null);
        function.lambdaExpr.lambdaBody = resolveFuncBody(dbi, func.body);

        return function;
    }

    private static LambdaExpr resolveLambda(DBICounter dbi, List<AstTele> teles,
                                            AstExpr returnExpr, AstExpr bodyExpr) {
        LambdaExpr expr = new LambdaExpr();

        // resolve parameters
        expr.lambdaTeles = resolveTeles(dbi, teles);

        // resolve return type
        Type returnType = resolveTypeExpr(dbi, returnExpr);
        expr.lambdaType = buildLambdaType(expr.lambdaTeles, returnType);

        if (bodyExpr != null) {
            expr.lambdaBody = new WildcardApplicableBody(resolveExpr(dbi, bodyExpr));
        }
        return expr;
    }

    private static ApplicableBody resolveFuncBody(DBICounter dbi, AstDefinition.Func.Body body) {
        if (body instanceof AstDefinition.Func.Body.WithoutElim) {
            AstExpr expr = ((AstDefinition.Func.Body.WithoutElim) body).expr;
            return new WildcardApplicableBody(resolveExpr(dbi, expr));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr resolveExpr(DBICounter dbi, AstExpr expr) {
        if (expr instanceof AstExpr.Arr) {
            throw new ResolveException("ArrExpr should be TypeExpr");
        }

        if (expr instanceof AstExpr.App) {
            AstExpr.App app = (AstExpr.App) expr;
            ApplyExpr applyExpr = new ApplyExpr();
            applyExpr.applicable = resolveAtomExpr(dbi, app.atom);
            applyExpr.arguments = app.arguments.stream()
                    .map(a -> resolveAtomExpr(dbi, a.explicitAtom))
                    .collect(Collectors.toList());
            return applyExpr;
        }

        if (expr instanceof AstExpr.Lam) {
            AstExpr.Lam lam = (AstExpr.Lam) expr;
            DBICounter lamDbi = new DBICounter(dbi);
            return resolveLambda(lamDbi, lam.teles, lam.returnExpr, lam.body);
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr resolveAtomExpr(DBICounter dbi, AstAtom atom) {
        if (atom instanceof AstAtom.AtomLit) {
            return new LiteralNameExpr(resolveName(dbi, ((AstAtom.AtomLit) atom).literal));
        }

        if (atom instanceof AstAtom.AtomNum) {
            return new LiteralNumberExpr(((AstAtom.AtomNum) atom).number);
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Name resolveName(DBICounter dbi, Name literal) {
        return dbi.resolve(literal);
    }

    private static Type resolveTypeExpr(DBICounter dbi, AstExpr typeExpr) {
        if (typeExpr.isTyped()) {
            throw new ResolveException("TypeExpr cannot have a type");
        }

        if (typeExpr instanceof AstExpr.App) {
            // currently, no dependent-type support
            // we only support AtomLit
            AstExpr.App app = (AstExpr.App) typeExpr;
            if (!app.arguments.isEmpty()) {
                throw new ResolveException("TypeExpr must be a name (no dependent-type support yet)");
            }

            if (!(app.atom instanceof AstAtom.AtomLit)) {
                throw new ResolveException("TypeExpr must be a name, instead of a number");
            }

            return new Type(((AstAtom.AtomLit) app.atom).literal);
        }

        if (typeExpr instanceof AstExpr.Arr) {
            AstExpr.Arr arr = (AstExpr.Arr) typeExpr;
            Type type = resolveTypeExpr(dbi, arr.exprs.get(0));
            Type cursor = type;

            for (int i = 1; i < arr.exprs.size(); i++) {
                cursor.resultType = resolveTypeExpr(dbi, arr.exprs.get(i));
                cursor = cursor.resultType;
            }
            return type;
        }

        if (typeExpr instanceof AstExpr.Lam) {
            throw new ResolveException("TypeExpr cannot be lambdas");
        }

        throw new IllegalStateException("should not reach here");
    }

    private static List<NamePair> resolveTeles(DBICounter dbi, List<AstTele> teles) {
        // convert arguments into individual typed names
        List<NamePair> namePairs = flattenTeles(teles);

        // resolve deferred types
        namePairs.forEach(np -> {
            if (np.type instanceof DeferredType) {
                DeferredType df = ((DeferredType) np.type);
                df.resolve(dbi, TreeResolver::resolveTypeExpr);
            }
            dbi.addName(np);
        });

        return namePairs;
    }

    private static Type buildLambdaType(List<NamePair> namePairs, Type returnType) {
        Type teleType = buildType(namePairs);
        Type cursor = teleType;

        while (cursor.isApplicable()) {
            cursor = cursor.resultType;
        }
        cursor.resultType = returnType;

        return teleType;
    }

    private static Type buildType(List<NamePair> namePairs) {
        if (namePairs.isEmpty()) {
            throw new IllegalArgumentException("Missing result type");
        }

        // we make an copy of namePars.get(0).type because
        // namePairs will be used in later dbi.resolve()
        // so changing it here may cause unexpected behavior
        Type type = new Type(namePairs.get(0).type);

        if (namePairs.size() > 1) {
            List<NamePair> resultType = namePairs.subList(1, namePairs.size());
            type.resultType = buildType(resultType);
        }
        return type;
    }

    private static List<NamePair> flattenTeles(List<AstTele> teles) {
        return teles.stream()
                .map(TreeResolver::flattenTele)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<NamePair> flattenTele(AstTele tele) {
        if (tele instanceof AstTele.TeleLit) {
            return flattenTeleLit(((AstTele.TeleLit) tele));
        } else {
            return flattenTeleExpr(((AstTele.TeleExpr) tele));
        }
    }

    private static List<NamePair> flattenTeleLit(AstTele.TeleLit tele) {
        return List.of(new NamePair(tele.literal, Type.TYPE_NO_NEED));
    }

    private static List<NamePair> flattenTeleExpr(AstTele.TeleExpr tele) {
        AstExpr expr = tele.expr;
        if (!expr.isTyped()) {
            throw new ResolveException("TeleExpr should be typed");
        }

        if (!(expr instanceof AstExpr.App)) {
            throw new ResolveException("Tele should only be Atom(s)");
        }

        AstExpr.App app = ((AstExpr.App) expr);
        Type type = new DeferredType(expr.typed);

        if (!(app.atom instanceof AstAtom.AtomLit)) {
            throw new ResolveException("Tele should be names instead of numbers");
        }

        List<NamePair> symbols = new ArrayList<>(1 + app.arguments.size());
        symbols.add(new NamePair(((AstAtom.AtomLit) app.atom).literal, type));

        app.arguments.stream()
                .map(a -> {
                    if (!(a.explicitAtom instanceof AstAtom.AtomLit)) {
                        throw new ResolveException("Tele should be names instead of numbers");
                    }

                    return new NamePair(((AstAtom.AtomLit) a.explicitAtom).literal, type);
                })
                .forEach(symbols::add);

        return symbols;
    }
}
