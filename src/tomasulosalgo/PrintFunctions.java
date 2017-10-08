
package tomasulosalgo;

public class PrintFunctions {
    
    public void printRFRAT(int[] RF, String[] RAT){
        
        System.out.println("   RF                   RAT\n");
        System.out.println("-----------------------------\n"); 
            for(int i = 0; i < 8; i++){
                int length = String.valueOf(RF[i]).length();
                if(length == 2){
                    System.out.println(i+ ":     " + RF[i] + "      |        " + RAT[i] + "\n");
                }
                else
                    System.out.println(i+ ":     " + RF[i] + "       |        " + RAT[i] + "\n");
            }
        
    }
    
    public void printRS(ReservationStation[] rs){
        System.out.println("_________________________________________________________________\n");
        System.out.println("       Busy     OP       VJ       VK       Qj       Qk       Disp\n");
        for(int i = 0; i <5; i++){
        System.out.println("RS" + i + "    " + rs[i].toString());
        }
        System.out.println("_________________________________________________________________\n");
        
    }
    
    public void printInstructionQueue(ArrayQueue<Instruction> aq, int numInstr){
        System.out.println("Instruction Queue");
        for(int i = 0; i < numInstr; i++){
            switch (aq.observe(i).getOpCode()) {
                case 0:
                    System.out.println("Add R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2()+ "\n");
                    break;
                case 1:
                    System.out.println("Sub R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2()+ "\n");
                    break;
                case 2:
                    System.out.println("Mul R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2()+ "\n");
                    break;
                case 3:
                    System.out.println("Div R" + aq.observe(i).getDestOp()+ ", R" + aq.observe(i).getSourceOp1()+ ", R" + aq.observe(i).getSourceOp2()+ "\n");
                    break;
                default:
                    break;
            }
        }
    }
    
    
    
}
