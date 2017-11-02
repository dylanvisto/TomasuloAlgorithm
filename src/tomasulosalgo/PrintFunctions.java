
package tomasulosalgo;

public class PrintFunctions {
    
    public void printRFRAT(int[] RF, String[] RAT){
        
        System.out.println("   RF                   RAT");
        System.out.println("-----------------------------"); 
            for(int i = 0; i < 8; i++){
                int length = String.valueOf(RF[i]).length();
                if(length == 2){
                    System.out.println("R" + i + ":     " + RF[i] + "      |        " + RAT[i] + "");
                }
                else
                    System.out.println("R" + i + ":     " + RF[i] + "       |        " + RAT[i] + "");
            }
        System.out.println();
        
    }
    
    public void printRS(ReservationStation rs1, ReservationStation rs2, ReservationStation rs3, ReservationStation rs4, ReservationStation rs5){
        System.out.println("_________________________________________________________________");
        System.out.println("       Busy     OP       VJ       VK       Qj       Qk       Disp");
        
        if(rs1.getVj().length() == 1 && rs1.getVk().length() == 1){
        System.out.println("RS" + 1 + "    " + rs1.getBusy() + "        "  + rs1.getOp() + "       " + rs1.getVj() + "        " + rs1.getVk() + "       " + rs1.getQj() + "      " + rs1.getQk() + "       " + rs1.getDisp());
        }
        else if(rs1.getVj().length() == 2 && rs1.getVk().length() == 1){
        System.out.println("RS" + 1 + "    " + rs1.getBusy() + "        "  + rs1.getOp() + "      " + rs1.getVj() + "        " + rs1.getVk() + "       " + rs1.getQj() + "      " + rs1.getQk() + "       " + rs1.getDisp());  
        }
        else if(rs1.getVj().length() == 1 && rs1.getVk().length() == 2){
        System.out.println("RS" + 1 + "    " + rs1.getBusy() + "        "  + rs1.getOp() + "       " + rs1.getVj() + "       " + rs1.getVk() + "       " + rs1.getQj() + "      " + rs1.getQk() + "       " + rs1.getDisp());  
        }
        else {
        System.out.println("RS" + 1 + "    " + rs1.getBusy() + "        "  + rs1.getOp() + "      " + rs1.getVj() + "       " + rs1.getVk() + "       " + rs1.getQj() + "      " + rs1.getQk() + "       " + rs1.getDisp());  
        }
        
        if(rs2.getVj().length() == 1 && rs2.getVk().length() == 1){
        System.out.println("RS" + 2 + "    " + rs2.getBusy() + "        "  + rs2.getOp() + "       " + rs2.getVj() + "        " + rs2.getVk() + "       " + rs2.getQj() + "      " + rs2.getQk() + "       " + rs2.getDisp());
        }
        else if(rs2.getVj().length() == 2 && rs2.getVk().length() == 1){
        System.out.println("RS" + 2 + "    " + rs2.getBusy() + "        "  + rs2.getOp() + "      " + rs2.getVj() + "        " + rs2.getVk() + "       " + rs2.getQj() + "      " + rs2.getQk() + "       " + rs2.getDisp());  
        }
        else if(rs2.getVj().length() == 1 && rs2.getVk().length() == 2){
        System.out.println("RS" + 2 + "    " + rs2.getBusy() + "        "  + rs2.getOp() + "       " + rs2.getVj() + "       " + rs2.getVk() + "       " + rs2.getQj() + "      " + rs2.getQk() + "       " + rs2.getDisp());  
        }
        else {
        System.out.println("RS" + 2 + "    " + rs2.getBusy() + "        "  + rs2.getOp() + "      " + rs2.getVj() + "       " + rs2.getVk() + "       " + rs2.getQj() + "      " + rs2.getQk() + "       " + rs2.getDisp());  
        }
        
        if(rs3.getVj().length() == 1 && rs3.getVk().length() == 1){
        System.out.println("RS" + 3 + "    " + rs3.getBusy() + "        "  + rs3.getOp() + "       " + rs3.getVj() + "        " + rs3.getVk() + "       " + rs3.getQj() + "      " + rs3.getQk() + "       " + rs3.getDisp());
        }
        else if(rs3.getVj().length() == 2 && rs3.getVk().length() == 1){
        System.out.println("RS" + 3 + "    " + rs3.getBusy() + "        "  + rs3.getOp() + "      " + rs3.getVj() + "        " + rs3.getVk() + "       " + rs3.getQj() + "      " + rs3.getQk() + "       " + rs3.getDisp());  
        }
        else if(rs3.getVj().length() == 1 && rs3.getVk().length() == 2){
        System.out.println("RS" + 3 + "    " + rs3.getBusy() + "        "  + rs3.getOp() + "       " + rs3.getVj() + "       " + rs3.getVk() + "       " + rs3.getQj() + "      " + rs3.getQk() + "       " + rs3.getDisp());  
        }
        else {
        System.out.println("RS" + 3 + "    " + rs3.getBusy() + "        "  + rs3.getOp() + "      " + rs3.getVj() + "       " + rs3.getVk() + "       " + rs3.getQj() + "      " + rs3.getQk() + "       " + rs3.getDisp());  
        }
        
        if(rs4.getVj().length() == 1 && rs4.getVk().length() == 1){
        System.out.println("RS" + 4 + "    " + rs4.getBusy() + "        "  + rs4.getOp() + "       " + rs4.getVj() + "        " + rs4.getVk() + "       " + rs4.getQj() + "      " + rs4.getQk() + "       " + rs4.getDisp());
        }
        else if(rs4.getVj().length() == 2 && rs4.getVk().length() == 1){
        System.out.println("RS" + 4 + "    " + rs4.getBusy() + "        "  + rs4.getOp() + "      " + rs4.getVj() + "        " + rs4.getVk() + "       " + rs4.getQj() + "      " + rs4.getQk() + "       " + rs4.getDisp());  
        }
        else if(rs4.getVj().length() == 1 && rs4.getVk().length() == 2){
        System.out.println("RS" + 4 + "    " + rs4.getBusy() + "        "  + rs4.getOp() + "       " + rs4.getVj() + "       " + rs4.getVk() + "       " + rs4.getQj() + "      " + rs4.getQk() + "       " + rs4.getDisp());  
        }
        else {
        System.out.println("RS" + 4 + "    " + rs4.getBusy() + "        "  + rs4.getOp() + "      " + rs4.getVj() + "       " + rs4.getVk() + "       " + rs4.getQj() + "      " + rs4.getQk() + "       " + rs4.getDisp());  
        }
        
        if(rs5.getVj().length() == 1 && rs5.getVk().length() == 1){
        System.out.println("RS" + 5 + "    " + rs5.getBusy() + "        "  + rs5.getOp() + "       " + rs5.getVj() + "        " + rs5.getVk() + "       " + rs5.getQj() + "      " + rs5.getQk() + "       " + rs5.getDisp());
        }
        else if(rs5.getVj().length() == 2 && rs5.getVk().length() == 1){
        System.out.println("RS" + 5 + "    " + rs5.getBusy() + "        "  + rs5.getOp() + "      " + rs5.getVj() + "        " + rs5.getVk() + "       " + rs5.getQj() + "      " + rs5.getQk() + "       " + rs5.getDisp());  
        }
        else if(rs5.getVj().length() == 1 && rs5.getVk().length() == 2){
        System.out.println("RS" + 5 + "    " + rs5.getBusy() + "        "  + rs5.getOp() + "       " + rs5.getVj() + "       " + rs5.getVk() + "       " + rs5.getQj() + "      " + rs5.getQk() + "       " + rs5.getDisp());  
        }
        else {
        System.out.println("RS" + 5 + "    " + rs5.getBusy() + "        "  + rs5.getOp() + "      " + rs5.getVj() + "       " + rs5.getVk() + "       " + rs5.getQj() + "      " + rs5.getQk() + "       " + rs5.getDisp());  
        }
        
        System.out.println("_________________________________________________________________");
        System.out.println();
        System.out.println("This is length of vk" + rs1.getVj().length());
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
