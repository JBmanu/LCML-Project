package src.compiler.exc;

import src.compiler.lib.FOOLlib;

public class TypeException extends Exception {

    private static final long serialVersionUID = 1L;

    public String text;

    public TypeException(String t, int line) {
        FOOLlib.typeErrors++;
        this.text = t + " at line " + line;
    }

}
