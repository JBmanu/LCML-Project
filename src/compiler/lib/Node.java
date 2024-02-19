package src.compiler.lib;

public abstract class Node implements Visitable {

    int line = -1;  // line -1 means unset

    public void setLine(int l) {
        this.line = l;
    }

    public int getLine() {
        return this.line;
    }

}

	  