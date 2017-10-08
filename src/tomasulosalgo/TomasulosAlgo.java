
package tomasulosalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class TomasulosAlgo {

    public static void main(String[] args) {
        
        String fileName = "tomasulo.txt";
        int numInstr, numCycles = 0, currentCycle = 0;
        int opCode, destOp, sourceOp1, sourceOp2 = 0;
        ArrayQueue<Instruction> instrHold = new ArrayQueue<>(10);
        int[] RF = new int[8];
        String[] RAT = new String[8];
        PrintFunctions pf = new PrintFunctions();
        
        
        try {
            Scanner scanner = new Scanner(new File(fileName));
            numInstr = scanner.nextInt();
            System.out.println("Number of Instructions: " + numInstr);
            numCycles = scanner.nextInt();
            System.out.println("Number of Cycles: " + numCycles);
            for(int i = 0; i < numInstr; i++){
                opCode = scanner.nextInt();
                destOp = scanner.nextInt();
                sourceOp1 = scanner.nextInt();
                sourceOp2 = scanner.nextInt();
                instrHold.enqueue(new Instruction(opCode, destOp, sourceOp1, sourceOp2));  
            }
            System.out.println(instrHold.observe(0).toString());
            System.out.println(instrHold.observe(1).toString());
            
            for(int j = 0; j < 8; j++){
                RF[j] = scanner.nextInt();   
            }
            System.out.println(Arrays.toString(RF));
            
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        
        //-------------------------------------------------------------
        System.out.println("\nInitial RF and RAT: \n");
        pf.printRFRAT(RF, RAT);
        for(int k = 0; k < numCycles; k++){
            
           
        }
        
        
        
        
        
    }
    
}
