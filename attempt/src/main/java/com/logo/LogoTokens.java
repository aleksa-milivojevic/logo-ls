package com.logo;

import java.util.Set;

public class LogoTokens {
    public final Set<String> functions = Set.of(
            "forward", "back", "left", "right", "home", "setx", "sety", "setxy", "setpos", "setheading",
            "arc", "ellipse", "fd", "bk", "lt", "rt", "seth", "pos", "xcor", "ycor", "heading", "towards",
            "showturtle", "st", "hideturtle", "ht", "clean", "cs", "clearscreen", "fill", "filled", "fillcolor",
            "label", "setlabelheight", "wrap", "window", "fence", "shownp", "shown?", "labelsize",
            "penup", "pu", "pendown", "pd", "setcolor", "setpencolor", "setwidth", "changeshape", "csh",
            "pendownp", "pendown?", "pencolor", "pc", "pensize", "list", "first", "butfirst",
            "last", "butlast", "item", "pick", "sum", "minus", "random", "modulo", "power",
            "readword", "readlist"
    );
    public final Set<String> keywords = Set.of(
            "word", "litp", "list?", "arrayp", "array?", "numberp", "number?", "emptyp", "empty?",
            "equalp", "equal?", "notequalp", "notequal?", "beforep", "before?", "substringp", "substring?",
            "repeat", "for", "repcount", "if", "ifelse", "test", "iftrue", "iffalse", "wait", "bye",
            "dotimes", "do.while", "while", "do.until", "until", "make", "name", "localmake", "thing",
            "to", "define", "def", "end"
    );
}
