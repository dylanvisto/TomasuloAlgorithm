
package tomasulosalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class TomasulosAlgo {

    public static void main(String[] args) {
        
        String fileName = "tomasulo.txt";
        int numInstr = 0, numCycles = 0, currentCycle = 0;
        int opCode, destOp, sourceOp1, sourceOp2 = 0;
        ArrayQueue<Instruction> instrHold = new ArrayQueue<>(10);
        int[] RF = new int[8];
        String[] RAT = new String[8];
        PrintFunctions pf = new PrintFunctions();
        //-------For Testing Only--------//
        ReservationStation rs1 = new ReservationStation("1" , "Add", " 3", " 3", "RS1", "RS2", "1");
        ReservationStation rs2 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
        ReservationStation rs3 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
        ReservationStation rs4 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
        ReservationStation rs5 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
        ReservationStation[] rsArray = new ReservationStation[5];
        rsArray[0] = rs1;
        rsArray[1] = rs2;
        rsArray[2] = rs3;
        rsArray[3] = rs4;
        rsArray[4] = rs5;
        //-------For Testing Only-------//
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
        pf.printRS(rsArray);
        pf.printRFRAT(RF, RAT);
        pf.printInstructionQueue(instrHold, numInstr);
        
        
        //First need to write code to track dependencies and keep track of cycles.
        //Obj: Track dependencies, Track execution times, Special Cases (WAW stale result, ...)
        
        int[] issue = new int[numInstr];
        int[] execute = new int[numInstr];
        int[] write = new int[numInstr];
        for(int k = 0; k < numInstr; k++){
            issue[k] = -1;
            execute[k] = -1;
            write[k] = -1;
        }
        
        int addCount = 0;
        int multCount = 0;
        int writeCheck = 0;
        int RAW = 0;
        int lowestWrite = 0;
        int highestRaw = 0;
        int issueCycle = 1;
        int RSA = 0;
        int RSM = 0;
        int go = 0;
        
        //------------------------Cycle Arrays Start-------------------//
        
        for(int i = 0; i < numInstr; i++){
            lowestWrite = 0;
            writeCheck = 0;
            multCount = 0;
            addCount = 0;
            go = 1;
            
            //-------------------Issue Start-------------------//
            
            if(((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)) && RSA != 3){
                issue[i] = issueCycle;
                issueCycle++;
                RSA++;
            }
            else if(((instrHold.observe(i).getOpCode() == 2) || (instrHold.observe(i).getOpCode() == 3)) && RSM != 2){
                issue[i] = issueCycle;
                issueCycle++;
                RSM++;
            }
            else if(((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)) && RSA == 3){
                for(int j = i-1; j >= 0; j--){
                    if((instrHold.observe(j).getOpCode() == 0) || (instrHold.observe(j).getOpCode() == 1)){
                        if(addCount != 3){
                            if(go == 1){
                            lowestWrite = write[j];
                            }
                            if(write[j] <= lowestWrite){
                                go = 0;
                                lowestWrite = write[j];
                            }
                        }
                        addCount++;
                    }    
                }
                if((lowestWrite+1) >= issueCycle){
                    issue[i] = lowestWrite+1;
                    issueCycle = lowestWrite+1;
                    issueCycle++;
                }
                else{
                    issue[i] = issueCycle;
                    issueCycle++;
                }
            }
            else if(((instrHold.observe(i).getOpCode() == 2) || (instrHold.observe(i).getOpCode() == 3)) && RSM == 2){
                for(int j = i-1; j >= 0; j--){
                    if((instrHold.observe(j).getOpCode() == 2) || (instrHold.observe(j).getOpCode() == 3)){
                        if(multCount != 2){
                            if(go == 1){
                            lowestWrite = write[j];
                            }
                            if(write[j] <= lowestWrite){
                                go = 0;
                                lowestWrite = write[j];
                            }
                        }
                        multCount++;
                    }    
                }
                if((lowestWrite+1) >= issueCycle){
                    issue[i] = lowestWrite+1;
                    issueCycle = lowestWrite+1;
                    issueCycle++;
                }
                else{
                    issue[i] = issueCycle;
                    issueCycle++;
                } 
            }
            
            //---------------------Issue Done-------------------//
            
            
            //----------------------Start Execution--------------//
            
            for(int c = i-1; c >= 0; c--){
               if((instrHold.observe(i).getSourceOp1() == instrHold.observe(c).getDestOp()) || (instrHold.observe(i).getSourceOp2() == instrHold.observe(c).getDestOp())){
                   if((write[c] != -1) && write[c] > highestRaw){
                       highestRaw = write[c];
                   }
                   RAW = 1;
               }
            }
            if(RAW == 1){
                execute[i] = highestRaw + 1;
                RAW = 0;
            }
            else{
                execute[i] = issueCycle;
                
            }
            
            
            if((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)){
                for(int p = i-1; p >= 0; p--){
                    if((instrHold.observe(p).getOpCode() == 0) || (instrHold.observe(p).getOpCode() == 1)){
                       if(execute[i] >= execute[p] && execute[i] < write[p]){
                           execute[i] = write[p];
                       }
                    }
                }
            }
            else if((instrHold.observe(i).getOpCode() == 2) || (instrHold.observe(i).getOpCode() == 3)){
                for(int p = 0; p < i; p++){
                    if((instrHold.observe(p).getOpCode() == 2) || (instrHold.observe(p).getOpCode() == 3)){
                       if(execute[i] >= execute[p] && execute[i] < write[p]){
                           execute[i] = write[p];
                       }; 
                    }
                }
            }
            
            //------------------Execution Done----------------//
            
            
            
            //-----------------Start Write-------------------//
            
            
            if((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)){
                write[i] = execute[i] + 2; 
            }
            else if(instrHold.observe(i).getOpCode() == 2){
                write[i] = execute[i] + 10; 
            }
            else if(instrHold.observe(i).getOpCode() == 3){
                write[i] = execute[i] + 40;
            }
            
            
            while(writeCheck == 0){
                writeCheck = 1;
                for(int h = i-1; h >= 0; h--){
                    if(write[i] == write[h]){
                        write[i] = write[h] + 1;
                        writeCheck = 0;
                    }
                }
                
            }
            
            //---------------Write Done---------------------//
            
            
        }
        //---------------Cycle Arrays Done (Issue, Execute, Write)---------------//
        
        
            System.out.println(Arrays.toString(issue));
            System.out.println(Arrays.toString(execute));
            System.out.println(Arrays.toString(write)); 
        
        
        
        
        
    }
    
}
