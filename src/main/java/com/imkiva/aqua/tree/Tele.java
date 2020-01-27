package com.imkiva.aqua.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class Tele extends Node {
    public abstract int findSymbol(String name);
    public abstract int getSymbolCount();

    public static class TeleLit extends Tele {
        public Literal literal;

        public TeleLit(Literal lit) {
            this.literal = lit;
        }

        @Override
        public int findSymbol(String name) {
            return name.equals(literal.name) ? 0 : -1;
        }

        @Override
        public int getSymbolCount() {
            return 1;
        }
    }

    public static class TeleExpr extends Tele {
        public Expr expr;

        private List<String> symbolList;

        public TeleExpr(Expr expr) {
            this.expr = expr;
        }

        @Override
        public int findSymbol(String name) {
            if (!expr.isTyped()) {
                throw new ResolveException("Tele should be typed");
            }

            if (!(expr instanceof Expr.App)) {
                throw new ResolveException("Tele should only be Atom(s)");
            }

            if (symbolList == null) {
                symbolList = toSymbolList(((Expr.App) expr));
            }

            return symbolList.indexOf(name);
        }

        @Override
        public int getSymbolCount() {
            if (symbolList == null) {
                symbolList = toSymbolList(((Expr.App) expr));
            }
            return symbolList.size();
        }

        private List<String> toSymbolList(Expr.App app) {
            if (!(app.atom instanceof Atom.AtomLit)) {
                throw new ResolveException("Tele should be names instead of numbers");
            }

            List<String> symbols = new ArrayList<>(1 + app.arguments.size());
            symbols.add(((Atom.AtomLit) app.atom).literal.name);

            app.arguments.stream()
                    .map(a -> {
                        if (!(a.explicitAtom instanceof Atom.AtomLit)) {
                            throw new ResolveException("Tele should be names instead of numbers");
                        }

                        return ((Atom.AtomLit) a.explicitAtom).literal.name;
                    })
                    .forEach(symbols::add);

            return symbols;
        }
    }
}
