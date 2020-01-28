package com.imkiva.aqua.tree;

import java.util.List;

public class TreeResolver {
    private static final class DBICounter {
        private DBICounter parent;
        private List<Tele> teles;

        public DBICounter(DBICounter parent, List<Tele> teles) {
            this.parent = parent;
            this.teles = teles;
        }

        public int resolveName(String name) {
            int symbolCount = 0;

            for (Tele tele : teles) {
                int index = tele.findSymbol(name);
                if (index >= 0) {
                    return symbolCount + index;
                }
                symbolCount += tele.getSymbolCount();
            }

            return parent == null ? DBI.FREE : symbolCount + parent.resolveName(name);
        }
    }

    public static Program resolve(Program program) {
        program.statements.forEach(TreeResolver::resolveStatements);
        return program;
    }

    private static void resolveStatements(Statement statement) {
        resolveDef(statement.def);
    }

    private static void resolveDef(Def def) {
        if (def instanceof Def.Func) {
            resolveDefFunc(((Def.Func) def));
        }
    }

    private static void resolveDefFunc(Def.Func func) {
        DBICounter dbi = new DBICounter(null, func.teles);
        resolveExpr(dbi, func.returnExpr);
        resolveFuncBody(dbi, func.body);
    }

    private static void resolveFuncBody(DBICounter dbi, Def.Func.Body body) {
        if (body instanceof Def.Func.Body.WithoutElim) {
            resolveExpr(dbi, ((Def.Func.Body.WithoutElim) body).expr);
        }
    }

    private static void resolveExpr(DBICounter dbi, Expr expr) {
        if (expr instanceof Expr.App) {
            Expr.App app = ((Expr.App) expr);
            resolveAtom(dbi, app.atom);
            app.arguments.forEach(a -> resolveArgument(dbi, a));
        }

        if (expr instanceof Expr.Arr) {
            Expr.Arr arr = ((Expr.Arr) expr);
            arr.exprs.forEach(e -> resolveExpr(dbi, e));
        }

        if (expr instanceof Expr.Lam) {
            Expr.Lam lam = ((Expr.Lam) expr);
            DBICounter lamDBI = new DBICounter(dbi, lam.teles);
            resolveExpr(lamDBI, lam.body);
        }
    }

    private static void resolveArgument(DBICounter dbi, Argument argument) {
        resolveAtom(dbi, argument.explicitAtom);
    }

    private static void resolveAtom(DBICounter dbi, Atom atom) {
        if (atom instanceof Atom.AtomLit) {
            Literal literal = ((Atom.AtomLit) atom).literal;
            resolveLiteral(dbi, literal);
        }
    }

    private static void resolveLiteral(DBICounter dbi, Literal literal) {
        if (literal.isResolved()) {
            return;
        }

        if (literal.isUnknown()) {
            literal.setNameDBI(DBI.UNKNOWN);
            return;
        }

        literal.setNameDBI(dbi.resolveName(literal.name));
    }
}
