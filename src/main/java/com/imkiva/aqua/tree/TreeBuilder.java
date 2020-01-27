package com.imkiva.aqua.tree;

import com.imkiva.aqua.parser.AquaLexer;
import com.imkiva.aqua.parser.AquaParser;
import com.imkiva.aqua.parser.AquaParser.*;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.stream.Collectors;

public class TreeBuilder {
    public static Program build(String src) {
        AquaLexer lexer = new AquaLexer(CharStreams.fromString(src));
        AquaParser parser = new AquaParser(new BufferedTokenStream(lexer));
        return buildProgram(parser.program());
    }

    private static Program buildProgram(ProgramContext programCtx) {
        return new Program(programCtx.statement().stream()
                .map(TreeBuilder::buildStatement)
                .collect(Collectors.toList()));
    }

    private static Statement buildStatement(StatementContext statementCtx) {
        return new Statement(buildDef(statementCtx.definition()));
    }

    private static Def buildDef(DefinitionContext defCtx) {
        if (defCtx instanceof DefFuncContext) {
            return buildDefFunc(((DefFuncContext) defCtx));
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Def.Func buildDefFunc(DefFuncContext ctx) {
        Def.Func func = new Def.Func();
        func.name = ctx.ID().getSymbol().getText();
        func.teles = ctx.tele().stream()
                .map(TreeBuilder::buildTele)
                .collect(Collectors.toList());
        if (ctx.returnExpr() != null) {
            func.returnExpr = buildReturnExpr(ctx.returnExpr());
        }
        func.body = buildFuncBody(ctx.funcBody());
        return func;
    }

    private static Def.Func.Body buildFuncBody(FuncBodyContext ctx) {
        if (ctx instanceof WithoutElimContext) {
            Def.Func.Body.WithoutElim body = new Def.Func.Body.WithoutElim();
            body.expr = buildExpr(((WithoutElimContext) ctx).expr());
            return body;
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildReturnExpr(ReturnExprContext ctx) {
        if (ctx instanceof ReturnExprExprContext) {
            return buildExpr(((ReturnExprExprContext) ctx).expr());
        }
        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildExpr(ExprContext ctx) {
        if (ctx instanceof ExprAppContext) {
            return buildAppExpr(((ExprAppContext) ctx));
        }

        if (ctx instanceof ExprLamContext) {
            ExprLamContext piContext = (ExprLamContext) ctx;
            Expr.Lam lam = new Expr.Lam();
            lam.body = buildExpr(piContext.expr());
            lam.teles = piContext.tele().stream()
                    .map(TreeBuilder::buildTele)
                    .collect(Collectors.toList());
            return lam;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildAppExpr(ExprAppContext ctx) {
        Expr.App app = new Expr.App();
        app.atom = buildAtom(ctx.atom());
        app.arguments = ctx.argument().stream()
                .map(TreeBuilder::buildArgument)
                .collect(Collectors.toList());
        return app;
    }

    private static Atom buildAtom(AtomContext ctx) {
        if (ctx instanceof AtomLiteralContext) {
            return new Atom.AtomLit(
                    buildLiteral(((AtomLiteralContext) ctx).literal()));
        }

        if (ctx instanceof AtomNumberContext) {
            return new Atom.AtomNum(Integer.parseInt(
                    ((AtomNumberContext) ctx).NUMBER().getSymbol().getText()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Argument buildArgument(ArgumentContext ctx) {
        if (ctx instanceof ArgumentExplicitContext) {
            return new Argument(
                    buildAtom(((ArgumentExplicitContext) ctx).atom()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Expr buildTypedExpr(TypedExprContext ctx) {
        if (ctx instanceof NotTypedContext) {
            return buildExpr(((NotTypedContext) ctx).expr());
        }

        if (ctx instanceof TypedContext) {
            TypedContext typedContext = (TypedContext) ctx;
            Expr expr = buildExpr(typedContext.expr(0));
            expr.typed = buildExpr(typedContext.expr(1));
            return expr;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Tele buildTele(TeleContext ctx) {
        if (ctx instanceof TeleLiteralContext) {
            Literal lit = buildLiteral(((TeleLiteralContext) ctx).literal());
            return new Tele.TeleLit(lit);
        }

        if (ctx instanceof TeleExplicitContext) {
            return new Tele.TeleExpr(
                    buildTypedExpr(((TeleExplicitContext) ctx).typedExpr()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Literal buildLiteral(LiteralContext ctx) {
        if (ctx instanceof NameContext) {
            return new Literal(((NameContext) ctx).ID().getSymbol().getText());
        }

        if (ctx instanceof UnknownContext) {
            return Literal.UNKNOWN;
        }

        throw new IllegalStateException("should not reach here");
    }
}
