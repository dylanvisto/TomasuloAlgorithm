
package tomasulosalgo;

public class Instruction {
    
    int opCode, destOp, sourceOp1, sourceOp2, Matcher;
    
    public Instruction(){}
    
    public Instruction(int opCode, int destOp, int sourceOp1, int sourceOp2, int Matcher){
        
        this.opCode = opCode;
        this.destOp = destOp;
        this.sourceOp1 = sourceOp1;
        this.sourceOp2 = sourceOp2;
        this.Matcher = Matcher;
        
        
    }

    public int getMatcher() {
        return Matcher;
    }

    public void setMatcher(int Matcher) {
        this.Matcher = Matcher;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public int getDestOp() {
        return destOp;
    }

    public void setDestOp(int destOp) {
        this.destOp = destOp;
    }

    public int getSourceOp1() {
        return sourceOp1;
    }

    public void setSourceOp1(int sourceOp1) {
        this.sourceOp1 = sourceOp1;
    }

    public int getSourceOp2() {
        return sourceOp2;
    }

    public void setSourceOp2(int sourceOp2) {
        this.sourceOp2 = sourceOp2;
    }

    @Override
    public String toString() {
        return "Instruction{" + "opCode=" + opCode + ", destOp=" + destOp + ", sourceOp1=" + sourceOp1 + ", sourceOp2=" + sourceOp2 + '}';
    }
    
    
}
