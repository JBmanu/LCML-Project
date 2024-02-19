package src.svm;

import gen.svm.SVMParser;

public class ExecuteVM {

    public static final int CODESIZE = 10000;
    public static final int MEMSIZE = 10000;

    private final int[] code;
    private final int[] memory = new int[MEMSIZE];

    private int ip = 0;
    private int sp = MEMSIZE;

    private int hp = 0;
    private int fp = MEMSIZE;
    private int ra;
    private int tm;

    public ExecuteVM(int[] code) {
        this.code = code;
    }

    public void cpu() {
        while (true) {
            int bytecode = this.code[this.ip++]; // fetch
            int v1, v2;
            int address;
            switch (bytecode) {
                case SVMParser.PUSH:
                    this.push(this.code[this.ip++]);
                    break;
                case SVMParser.POP:
                    this.pop();
                    break;
                case SVMParser.ADD:
                    v1 = this.pop();
                    v2 = this.pop();
                    this.push(v2 + v1);
                    break;
                case SVMParser.MULT:
                    v1 = this.pop();
                    v2 = this.pop();
                    this.push(v2 * v1);
                    break;
                case SVMParser.DIV:
                    v1 = this.pop();
                    v2 = this.pop();
                    this.push(v2 / v1);
                    break;
                case SVMParser.SUB:
                    v1 = this.pop();
                    v2 = this.pop();
                    this.push(v2 - v1);
                    break;
                case SVMParser.STOREW: //
                    address = this.pop();
                    this.memory[address] = this.pop();
                    break;
                case SVMParser.LOADW: //
                    this.push(this.memory[this.pop()]);
                    break;
                case SVMParser.BRANCH:
                    address = this.code[this.ip];
                    this.ip = address;
                    break;
                case SVMParser.BRANCHEQ:
                    address = this.code[this.ip++];
                    v1 = this.pop();
                    v2 = this.pop();
                    if (v2 == v1) this.ip = address;
                    break;
                case SVMParser.BRANCHLESSEQ:
                    address = this.code[this.ip++];
                    v1 = this.pop();
                    v2 = this.pop();
                    if (v2 <= v1) this.ip = address;
                    break;
                case SVMParser.JS: //
                    address = this.pop();
                    this.ra = this.ip;
                    this.ip = address;
                    break;
                case SVMParser.STORERA: //
                    this.ra = this.pop();
                    break;
                case SVMParser.LOADRA: //
                    this.push(this.ra);
                    break;
                case SVMParser.STORETM:
                    this.tm = this.pop();
                    break;
                case SVMParser.LOADTM:
                    this.push(this.tm);
                    break;
                case SVMParser.LOADFP: //
                    this.push(this.fp);
                    break;
                case SVMParser.STOREFP: //
                    this.fp = this.pop();
                    break;
                case SVMParser.COPYFP: //
                    this.fp = this.sp;
                    break;
                case SVMParser.STOREHP: //
                    this.hp = this.pop();
                    break;
                case SVMParser.LOADHP: //
                    this.push(this.hp);
                    break;
                case SVMParser.PRINT:
                    System.out.println((this.sp < MEMSIZE) ? this.memory[this.sp] : "Empty stack!");
                    break;
                case SVMParser.HALT:
                    return;
            }
        }
    }

    private int pop() {
        return this.memory[this.sp++];
    }

    private void push(int v) {
        this.memory[--this.sp] = v;
    }

}