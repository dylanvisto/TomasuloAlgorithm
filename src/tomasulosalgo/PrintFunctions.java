
package tomasulosalgo;

public class PrintFunctions {
    
    public void printRFRAT(int[] RF, String[] RAT){
        
        System.out.println("   RF                   RAT");
        System.out.println("-----------------------------"); 
            for(int i = 0; i < 8; i++){
                int length = String.valueOf(RF[i]).length();
                if(length == 2){
                    System.out.println("F" + i + ":     " + RF[i] + "      |        " + RAT[i] + "");
                }
                else
                    System.out.println("F" + i + ":     " + RF[i] + "       |        " + RAT[i] + "");
            }
        System.out.println();
        
    }
    
    public void printRS(ReservationStation rs1, ReservationStation rs2, ReservationStation rs3, ReservationStation rs4, ReservationStation rs5){
        System.out.println("_________________________________________________________________");
        System.out.println("       Busy     OP       VJ       VK       Qj       Qk       Disp");
        
        System.out.println("RS" + 1 + "    " + rs1.toString());
        System.out.println("RS" + 2 + "    " + rs2.toString());
        System.out.println("RS" + 3 + "    " + rs3.toString());
        System.out.println("RS" + 4 + "    " + rs4.toString());
        System.out.println("RS" + 5 + "    " + rs5.toString());
        
        System.out.println("_________________________________________________________________");
        System.out.println();
    }
    
    public void printInstructionQueue(ArrayQueue<Instruction> aq, int numInstr){
        System.out.println("Instruction Queue");
        for(int i = 0; i < numInstr; i++){
            switch (aq.observe(i).getOpCode()) {
                case 0:
                    System.out.println("Add R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2());
                    break;
                case 1:
                    System.out.println("Sub R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2());
                    break;
                case 2:
                    System.out.println("Mul R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2());
                    break;
                case 3:
                    System.out.println("Div R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2());
                    break;
                default:
                    break;
            }
        }
        System.out.println();
    }
    
    
    
}
