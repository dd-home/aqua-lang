package com.imkiva.aqua.tree.ast;

import com.imkiva.aqua.parser.AquaLexer;
import com.imkiva.aqua.parser.AquaParser;
import com.imkiva.aqua.parser.AquaParser.*;
import com.imkiva.aqua.tree.common.Name;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;

import java.util.stream.Collectors;

public class AstBuilder {
    public static AstProgram build(String src) {
        AquaLexer lexer = new AquaLexer(CharStreams.fromString(src));
        AquaParser parser = new AquaParser(new BufferedTokenStream(lexer));
        return buildProgram(parser.program());
    }

    private static AstProgram buildProgram(ProgramContext programCtx) {
        return new AstProgram(programCtx.statement().stream()
                .map(AstBuilder::buildStatement)
                .collect(Collectors.toList()));
    }

    private static AstStatement buildStatement(StatementContext statementCtx) {
        return new AstStatement(buildDef(statementCtx.definition()));
    }

    private static AstDefinition buildDef(DefinitionContext defCtx) {
        if (defCtx instanceof DefFuncContext) {
            return buildDefFunc(((DefFuncContext) defCtx));
        }
        throw new IllegalStateException("should not reach here");
    }

    private static AstDefinition.Func buildDefFunc(DefFuncContext ctx) {
        AstDefinition.Func func = new AstDefinition.Func();
        func.name = ctx.ID().getSymbol().getText();
        func.teles = ctx.tele().stream()
                .map(AstBuilder::buildTele)
                .collect(Collectors.toList());
        func.returnExpr = buildReturnExpr(ctx.returnExpr());
        func.body = buildFuncBody(ctx.funcBody());
        return func;
    }

    private static AstDefinition.Func.Body buildFuncBody(FuncBodyContext ctx) {
        if (ctx instanceof WithoutElimContext) {
            AstDefinition.Func.Body.WithoutElim body = new AstDefinition.Func.Body.WithoutElim();
            body.expr = buildExpr(((WithoutElimContext) ctx).expr());
            return body;
        }
        throw new IllegalStateException("should not reach here");
    }

    private static AstExpr buildReturnExpr(ReturnExprContext ctx) {
        if (ctx instanceof ReturnExprExprContext) {
            return buildExpr(((ReturnExprExprContext) ctx).expr());
        }
        throw new IllegalStateException("should not reach here");
    }

    private static AstExpr buildExpr(ExprContext ctx) {
        if (ctx instanceof ExprAppContext) {
            return buildAppExpr(((ExprAppContext) ctx));
        }

        if (ctx instanceof ExprArrContext) {
            return buildArrExpr(((ExprArrContext) ctx));
        }

        if (ctx instanceof ExprLamContext) {
            return buildLamExpr((ExprLamContext) ctx);
        }

        throw new IllegalStateException("should not reach here");
    }

    private static AstExpr buildAppExpr(ExprAppContext ctx) {
        AstExpr.App app = new AstExpr.App();
        app.atom = buildAtom(ctx.atom());
        app.arguments = ctx.argument().stream()
                .map(AstBuilder::buildArgument)
                .collect(Collectors.toList());
        return app;
    }

    private static AstExpr buildArrExpr(ExprArrContext ctx) {
        AstExpr.Arr arr = new AstExpr.Arr();
        arr.exprs = ctx.expr().stream()
                .map(AstBuilder::buildExpr)
                .collect(Collectors.toList());
        return arr;
    }

    private static AstExpr buildLamExpr(ExprLamContext ctx) {
        AstExpr.Lam lam = new AstExpr.Lam();
        lam.body = buildExpr(ctx.expr());
        lam.teles = ctx.tele().stream()
                .map(AstBuilder::buildTele)
                .collect(Collectors.toList());
        return lam;
    }

    private static AstAtom buildAtom(AtomContext ctx) {
        if (ctx instanceof AtomLiteralContext) {
            return new AstAtom.AtomLit(
                    buildLiteral(((AtomLiteralContext) ctx).literal()));
        }

        if (ctx instanceof AtomNumberContext) {
            return new AstAtom.AtomNum(Integer.parseInt(
                    ((AtomNumberContext) ctx).NUMBER().getSymbol().getText()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static AstArgument buildArgument(ArgumentContext ctx) {
        if (ctx instanceof ArgumentExplicitContext) {
            return new AstArgument(
                    buildAtom(((ArgumentExplicitContext) ctx).atom()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static AstExpr buildTypedExpr(TypedExprContext ctx) {
        if (ctx instanceof NotTypedContext) {
            return buildExpr(((NotTypedContext) ctx).expr());
        }

        if (ctx instanceof TypedContext) {
            TypedContext typedContext = (TypedContext) ctx;
            AstExpr expr = buildExpr(typedContext.expr(0));
            expr.typed = buildExpr(typedContext.expr(1));
            return expr;
        }

        throw new IllegalStateException("should not reach here");
    }

    private static AstTele buildTele(TeleContext ctx) {
        if (ctx instanceof TeleLiteralContext) {
            Name lit = buildLiteral(((TeleLiteralContext) ctx).literal());
            return new AstTele.TeleLit(lit);
        }

        if (ctx instanceof TeleExplicitContext) {
            return new AstTele.TeleExpr(
                    buildTypedExpr(((TeleExplicitContext) ctx).typedExpr()));
        }

        throw new IllegalStateException("should not reach here");
    }

    private static Name buildLiteral(LiteralContext ctx) {
        if (ctx instanceof NameContext) {
            return new Name(((NameContext) ctx).ID().getSymbol().getText());
        }

        if (ctx instanceof UnknownContext) {
            return Name.UNKNOWN;
        }

        throw new IllegalStateException("should not reach here");
    }
}
