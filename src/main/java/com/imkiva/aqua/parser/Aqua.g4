grammar Aqua;

program : //'今天、写了程序'
          statement*
          //'呜哇啊'
          EOF
        ;

statement : definition
          ;

definition : '\\func' ID tele* ':' returnExpr funcBody  # defFunc
           ;

funcBody : '=>' expr  # withoutElim
         ;

returnExpr : expr  # returnExprExpr
           ;

expr : atom argument*                  # exprApp
     | <assoc=right> expr '->' expr    # exprArr
     | '\\lam' tele+ '=>' expr         # exprLam
     ;

typedExpr : expr          # notTyped
          | expr ':' expr # typed
          ;

argument : atom                         # argumentExplicit
         ;

tele : literal           # teleLiteral
     | '(' typedExpr ')' # teleExplicit
     ;

atom  : literal                 # atomLiteral
      | NUMBER                  # atomNumber
      ;

literal : ID             # name
        | '_'            # unknown
        ;

NUMBER : '-'? [0-9]+;

WS : [ \t\r\n]+ -> skip;
LINE_COMMENT : '--' '-'* (~[~!@#$%^&*\-+=<>?/|:[\u005Da-zA-Z_0-9'\r\n] ~[\r\n]* | ) -> skip;
COMMENT : '{-' (COMMENT|.)*? '-}' -> skip;

fragment START_CHAR : [~!@#$%^&*\-+=<>?/|:[\u005Da-zA-Z_];
ID : START_CHAR (START_CHAR | [0-9'])*;
INVALID_KEYWORD : '\\' ID;
ERROR_CHAR : .;
