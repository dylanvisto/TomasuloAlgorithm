
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
//        ReservationStation rs1 = new ReservationStation("1" , "Add", " 3", " 3", "RS1", "RS2", "1");
//        ReservationStation rs2 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
//        ReservationStation rs3 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
//        ReservationStation rs4 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
//        ReservationStation rs5 = new ReservationStation(" " , "   ", "  ", "  ", "   ", "   ", " ");
//        ReservationStation[] rsArray = new ReservationStation[5];
//        rsArray[0] = rs1;
//        rsArray[1] = rs2;
//        rsArray[2] = rs3;
//        rsArray[3] = rs4;
//        rsArray[4] = rs5;
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
            for(int k = 0; k < 8; k++){
                RAT[k] = "F" + k;
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        
        //-------------------------------------------------------------
        //pf.printRS(rsArray);
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
            //Need to add functionality for if the FU is still in use. If it is
            
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
                       } 
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
        
            
        //---------------RS/RAT/RF Updating Algorithm------------------//
        int instrIndexI = 0;
        int instrIndexE = 0;
        int instrIndexW = 0;
        int activeCycle = 0;
        int sourceOP1 = 0;
        int sourceOP2 = 0;
        String vjIntToString;
        String vkIntToString;
        
        int rsValue = 1;
        int rsValue2 = 4;
        for(int i = 1; i <= numCycles; i++){
            ReservationStation rs1 = new ReservationStation();
            ReservationStation rs2 = new ReservationStation();
            ReservationStation rs3 = new ReservationStation();
            ReservationStation rs4 = new ReservationStation();
            ReservationStation rs5 = new ReservationStation();
            activeCycle = 0;
            instrIndexI = -1;
            instrIndexE = -1;
            instrIndexW = -1;
            for(int j = 0; j < numInstr; j++){
                if(issue[j] == i){
                    
                    instrIndexI = j;
                    activeCycle = 1;
                }
                else if(execute[j] == i){
                    
                    instrIndexE = j;
                    activeCycle = 1;
                }
                else if(write[j] == i){
                    
                    instrIndexW = j;
                    activeCycle = 1;
                }
                
                
            }
            if(activeCycle != 1){
                System.out.println("Cycle " + i + " was empty"); 
            }
            
            //------------Issue Algorithm--------------//
            
            if(instrIndexI != -1){
                
                if(instrHold.observe(instrIndexI).getOpCode() == 0 || instrHold.observe(instrIndexI).getOpCode() == 1){
                
                    if(rsValue == 1){

                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 0:
                                rs1.setOp("ADD");
                                break;
                            case 1:
                                rs1.setOp("SUB");
                                break;
                        }

                                rs1.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("F"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs1.setVj(vjIntToString);

                                }
                                else{

                                    rs1.setVj("   ");

                                }

                                if(RAT[sourceOP2].equals("F"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs1.setVk(vkIntToString);

                                }
                                else{

                                    rs1.setVk("   ");

                                }
                                if(rsValue == 4){
                                    rsValue = 1;
                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS" + rsValue;

                                rs1.setQj("   ");
                                rs1.setQk("   ");
                                rs1.setDisp(" ");
                                rsValue++;
                    }
                    if(rsValue == 2){

                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 0:
                                rs2.setOp("ADD");
                                break;
                            case 1:
                                rs2.setOp("SUB");
                                break;
                        }

                                rs2.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("F"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs2.setVj(vjIntToString);

                                }
                                else{

                                    rs2.setVj("   ");

                                }

                                if(RAT[sourceOP2].equals("F"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs2.setVk(vkIntToString);

                                }
                                else{

                                    rs2.setVk("   ");

                                }
                                if(rsValue == 4){
                                    rsValue = 1;
                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS" + rsValue;

                                rs2.setQj("   ");
                                rs2.setQk("   ");
                                rs2.setDisp(" ");
                                rsValue++;
                    }
                    if(rsValue == 3){

                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 0:
                                rs3.setOp("ADD");
                                break;
                            case 1:
                                rs3.setOp("SUB");
                                break;
                        }

                                rs3.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("F"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs3.setVj(vjIntToString);

                                }
                                else{

                                    rs3.setVj("   ");

                                }

                                if(RAT[sourceOP2].equals("F"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs3.setVk(vkIntToString);

                                }
                                else{

                                    rs3.setVk("   ");

                                }
                                if(rsValue == 4){
                                    rsValue = 1;
                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS" + rsValue;

                                rs3.setQj("   ");
                                rs3.setQk("   ");
                                rs3.setDisp(" ");
                                rsValue++;
                    }
                    
                }
                else if(instrHold.observe(instrIndexI).getOpCode() == 2 || instrHold.observe(instrIndexI).getOpCode() == 3){
                    
                    if(rsValue2 == 4){

                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 2:
                                rs4.setOp("MUL");
                                break;
                            case 3:
                                rs4.setOp("DIV");
                                break;
                        }

                                rs4.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("F"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs4.setVj(vjIntToString);

                                }
                                else{

                                    rs4.setVj("   ");

                                }

                                if(RAT[sourceOP2].equals("F"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs4.setVk(vkIntToString);

                                }
                                else{

                                    rs4.setVk("   ");

                                }
                                if(rsValue2 == 5){
                                    rsValue2 = 4;
                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS" + rsValue2;

                                rs4.setQj("   ");
                                rs4.setQk("   ");
                                rs4.setDisp(" ");
                                rsValue2++;
                    }
                    if(rsValue2 == 5){

                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 0:
                                rs5.setOp("ADD");
                                break;
                            case 1:
                                rs5.setOp("SUB");
                                break;
                        }

                                rs5.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("F"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs5.setVj(vjIntToString);

                                }
                                else{

                                    rs5.setVj("   ");

                                }

                                if(RAT[sourceOP2].equals("F"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs5.setVk(vkIntToString);

                                }
                                else{

                                    rs5.setVk("   ");

                                }
                                if(rsValue2 == 6){
                                    rsValue2 = 4;
                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS" + rsValue2;

                                rs5.setQj("   ");
                                rs5.setQk("   ");
                                rs5.setDisp(" ");
                                rsValue2++;
                    }
                    
                }
                
            }
            else if(instrIndexI == -1){
                
            }
            
            //-----------------Issue Algorithm finished---------------//
            
            
            //compare using rsValue and name, use mod? issued in order; find all previous instructions of add/sub and mul/div; increment a seperat
            //dispatch notice only in same cycle it dispatches?
            int Opget = 0;
            int sameInstr = 0;
            int rsLocation = 0;
            if(instrIndexE != -1){
                sameInstr = 0;
                Opget = instrHold.observe(instrIndexE).getOpCode();
                
                if(Opget == 0 || Opget == 1){
                    for(int h = instrIndexE-1; h >= 0; h--){
                        if(instrHold.observe(h).getOpCode() == 0 || instrHold.observe(h).getOpCode() == 1){
                            sameInstr++;
                            
                        }
                    }
                    
                    rsLocation = sameInstr % 3;
                    if(rsLocation == 0){
                        
                        rsLocation = 3;
                        
                    }
                    switch(rsLocation){
                        case 1:
                            rs1.setDisp("1");
                        case 2:
                            rs2.setDisp("1");
                        case 3:
                            rs3.setDisp("1");   
                    }
                }
                else if(Opget == 2 || Opget == 3){
                    for(int h = instrIndexE-1; h >= 0; h--){
                        if(instrHold.observe(h).getOpCode() == 2 || instrHold.observe(h).getOpCode() == 3){
                            sameInstr++;
                            
                        } 
                    }
                    
                    rsLocation = sameInstr % 2;
                    if(rsLocation == 0){
                        
                        rsLocation = 2;
                        
                    }
                    switch(rsLocation){
                        case 1:
                            rs4.setDisp("1");
                        case 2:
                            rs5.setDisp("1");  
                    }
                }
                
                
                
                
                
              
                
                
                
            }
            
            
            
            
            
            
            
        }
        
        
        
    }
    
}
