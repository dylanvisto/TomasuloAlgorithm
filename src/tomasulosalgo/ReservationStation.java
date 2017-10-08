
package tomasulosalgo;

public class ReservationStation {
    
    Boolean Busy, Disp;
    String Op, Qj, Qk;
    int Vj, Vk;
    
    public ReservationStation(){}
    
    public ReservationStation(Boolean Busy, String Op, int Vj, int Vk, String Qj, String Qk, Boolean Disp){
        this.Busy = Busy;
        this.Op = Op;
        this.Vj = Vj;
        this.Vk = Vk;
        this.Qj = Qj;
        this.Qk = Qk;
        this.Disp = Disp;
        
    }

    public Boolean getBusy() {
        return Busy;
    }

    public void setBusy(Boolean Busy) {
        this.Busy = Busy;
    }

    public Boolean getDisp() {
        return Disp;
    }

    public void setDisp(Boolean Disp) {
        this.Disp = Disp;
    }

    public String getOp() {
        return Op;
    }

    public void setOp(String Op) {
        this.Op = Op;
    }

    public String getQj() {
        return Qj;
    }

    public void setQj(String Qj) {
        this.Qj = Qj;
    }

    public String getQk() {
        return Qk;
    }

    public void setQk(String Qk) {
        this.Qk = Qk;
    }

    public int getVj() {
        return Vj;
    }

    public void setVj(int Vj) {
        this.Vj = Vj;
    }

    public int getVk() {
        return Vk;
    }

    public void setVk(int Vk) {
        this.Vk = Vk;
    }

    @Override
    public String toString() {
        return "ReservationStation{" + "Busy=" + Busy + ", Disp=" + Disp + ", Op=" + Op + ", Qj=" + Qj + ", Qk=" + Qk + ", Vj=" + Vj + ", Vk=" + Vk + '}';
    }
    
    
    
}
