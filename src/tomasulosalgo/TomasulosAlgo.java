package tomasulosalgo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TomasulosAlgo {

    public static void main(String[] args) {
        
        //The name of the text file
        String fileName = "tomasulo.txt";
        //Declarations
        int numInstr = 0, numCycles = 0, currentCycle = 0;
        int matcher = 0;
        int opCode, destOp, sourceOp1, sourceOp2 = 0;
        ArrayQueue<Instruction> instrHold = new ArrayQueue<>(10);
        int[] RF = new int[8];
        String[] RAT = new String[8];
        //Creating instance of my PrintFunctions class
        PrintFunctions pf = new PrintFunctions();
        
        //tests to make sure that the file is opened correctly
        try {
            //Scans the entire text file and assigns the values to variables
            Scanner scanner = new Scanner(new File(fileName));
            numInstr = scanner.nextInt();
            numCycles = scanner.nextInt();
            //Loops through the instructions and puts those instructions into an array queue
            for(int i = 0; i < numInstr; i++){
                opCode = scanner.nextInt();
                destOp = scanner.nextInt();
                sourceOp1 = scanner.nextInt();
                sourceOp2 = scanner.nextInt();
                matcher = i+1;
                instrHold.enqueue(new Instruction(opCode, destOp, sourceOp1, sourceOp2, matcher));  
            }
            //Loops through the 8 different RF values and stores them in an int array
            for(int j = 0; j < 8; j++){
                RF[j] = scanner.nextInt();   
            }
            //Initializes the RAT with initial RF pointers
            for(int k = 0; k < 8; k++){
                RAT[k] = "R" + k;
            }
        }
        //Catches the exception for when the file is not found
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
        
        //These arrays will hold the Issue, Execution, and Write cycles
        int[] issue = new int[numInstr];
        int[] execute = new int[numInstr];
        int[] write = new int[numInstr];
        //Initializing each of those arrays to be -1
        for(int k = 0; k < numInstr; k++){
            issue[k] = -1;
            execute[k] = -1;
            write[k] = -1;
        }
        //Declarations
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
        int specialCaseVar = 0;
        
        //------------------------Cycle Arrays Start-------------------//
        //Creates the issue, execute, and write cycles for each instruction
        for(int i = 0; i < numInstr; i++){
            lowestWrite = 0;
            writeCheck = 0;
            multCount = 0;
            addCount = 0;
            go = 1;
            
            //-------------------Issue Start-------------------//
            //Checks if the opcode is add or subtract and if the reservation stations are not full
            if(((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)) && RSA != 3){
                //Increments issue cycle and RSA after assigning the issue cycle
                issue[i] = issueCycle;
                issueCycle++;
                RSA++;
            }
            //Checks if the opcode is multiply or divide and if the reservation stations are not full
            else if(((instrHold.observe(i).getOpCode() == 2) || (instrHold.observe(i).getOpCode() == 3)) && RSM != 2){
                //Increments issue cycle and RSM after assigning the issue cycle
                issue[i] = issueCycle;
                issueCycle++;
                RSM++;
            }
            //Checks if the opcdoe is add or subtract and if the reservation stations are full
            else if(((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)) && RSA == 3){
                //if the reservation stations are full, we need to check three instruction of the same type before the one we are currently on
                for(int j = i-1; j >= 0; j--){
                    if((instrHold.observe(j).getOpCode() == 0) || (instrHold.observe(j).getOpCode() == 1)){
                        //Grabs the lowest write value for the last three instructions
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
                //Makes sure that the lowest write value + 1 doesnt get assigned as the issue cycle for the current instruction if the issue cycle is actually greater than lowest write + 1
                if((lowestWrite+1) >= issueCycle){
                    issue[i] = lowestWrite+1;
                    issueCycle = lowestWrite+1;
                    issueCycle++;
                }
                //if the issue cycle is greater than lowest write + 1 then it needs to use the current issue cycle
                else{
                    issue[i] = issueCycle;
                    issueCycle++;
                }
            }
            //Does the same thing as above excpet for multiply and divide instructions
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
            //Checks for RAW dependencies between the current instruction and every previous instruction
            for(int c = i-1; c >= 0; c--){
               if((instrHold.observe(i).getSourceOp1() == instrHold.observe(c).getDestOp()) || (instrHold.observe(i).getSourceOp2() == instrHold.observe(c).getDestOp())){
                   if((write[c] != -1) && write[c] > highestRaw){
                       highestRaw = write[c];
                   }
                   RAW = 1;
               }
            }
            //If raw dependency assign highestRaw + 1 to current execute cycle
            if(RAW == 1){
                execute[i] = highestRaw + 1;
                RAW = 0;
            }
            else{
                execute[i] = issueCycle;
                
            }
            //If the current issue cycle is equal to or greater than the highest Raw, then you need to assign the current execute cycle to be the current issueCycle
            if(issueCycle >= highestRaw+1){
                
                execute[i] = issueCycle;
            }
            
            //Checks if a functional unit is in use; if so, then execute cycle for the current instruction get the write cycle of the previous instruction, since that is when the functional unit is not being used
            if((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)){
                for(int p = i-1; p >= 0; p--){
                    if((instrHold.observe(p).getOpCode() == 0) || (instrHold.observe(p).getOpCode() == 1)){
                       if(execute[i] >= execute[p] && execute[i] < write[p]){
                           execute[i] = write[p];
                       }
                    }
                }
            }
            //same thing as above
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
            
            // Adds the latencies to the issue cycle to give the write cycle for the current instruction
            if((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1)){
                write[i] = execute[i] + 2; 
            }
            else if(instrHold.observe(i).getOpCode() == 2){
                write[i] = execute[i] + 10; 
            }
            else if(instrHold.observe(i).getOpCode() == 3){
                write[i] = execute[i] + 40;
            }
            
            //Checks for one or more instructions broadcasting at the same time. Multiply and Divide instructions will get precedence over Add and Subtract instructions
            while(writeCheck == 0){
                writeCheck = 1;
                for(int h = i-1; h >= 0; h--){
                    if(write[i] == write[h]){
                        if(((instrHold.observe(h).getOpCode() == 2) || (instrHold.observe(h).getOpCode() == 3)) && ((instrHold.observe(i).getOpCode() == 0) || (instrHold.observe(i).getOpCode() == 1))){
                            write[i] = write[h] + 1;
                            writeCheck = 0;
                        }
                        else if(((instrHold.observe(h).getOpCode() == 0) || (instrHold.observe(h).getOpCode() == 1)) && ((instrHold.observe(i).getOpCode() == 2) || (instrHold.observe(i).getOpCode() == 3))){
                            write[h] = write[i] + 1;
                            writeCheck = 0;
                        }
                        else{
                            write[i] = write[h] + 1;
                            writeCheck = 0;
                            
                        }
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
        //Declarations
        
        int rsClear = 0;
        
        int instrIndexI = 0;
        int instrIndexE = 0;
        int instrIndexE2 = 0;
        int instrIndexW = 0;
        int activeCycle = 0;
        int sourceOP1 = 0;
        int sourceOP2 = 0;
        String vjIntToString;
        String vkIntToString;
        
        //indicates if RS is in use
        //if FreeRS# = 0 it is in use
        int freeRS1 = 1;
        int freeRS2 = 1;
        int freeRS3 = 1;
        int freeRS4 = 1;
        int freeRS5 = 1;
        
        int twoFU = 0;
        //Update the RS, RAT, and RF for each instruction until the number of cycles in the text file
        ReservationStation rs1 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs2 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs3 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs4 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs5 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        for(int i = 1; i <= numCycles; i++){
            
            activeCycle = 0;
            instrIndexI = -1;
            instrIndexE = -1;
            instrIndexW = -1;
            for(int j = 0; j < numInstr; j++){
                if(issue[j] == i){
                    
                    instrIndexI = j;
                    activeCycle = 1;
                    break;
                }
                
            }
            for(int j = 0; j < numInstr; j++){
                if(execute[j] == i){
                    
                    instrIndexE = j;
                    activeCycle = 1;
                    twoFU++;
                }
                if(twoFU == 2){
                    instrIndexE2 = j;
                }
                
            }
            for(int j = 0; j < numInstr; j++){   
                if(write[j] == i){
                    
                    instrIndexW = j;
                    activeCycle = 1;
                    break;
                }
                
            }
            
                
                
            
            if(activeCycle != 1){
                System.out.println("Cycle " + i + " was empty"); 
            }
            
            
            
            
            if(rsClear == 1){
                rsClear = 0;
                rs1.setBusy("0");
                rs1.setOp("   ");
                rs1.setQj("   ");
                rs1.setQk("   ");
                rs1.setVj("  ");
                rs1.setVk("  ");
                rs1.setDisp("0");
                
            }
            else if(rsClear == 2){
                rsClear = 0;
                rs2.setBusy("0");
                rs2.setOp("   ");
                rs2.setQj("   ");
                rs2.setQk("   ");
                rs2.setVj("  ");
                rs2.setVk("  ");
                rs2.setDisp("0");
                
            }
            else if(rsClear == 3){
                rsClear = 0;
                rs3.setBusy("0");
                rs3.setOp("   ");
                rs3.setQj("   ");
                rs3.setQk("   ");
                rs3.setVj("  ");
                rs3.setVk("  ");
                rs3.setDisp("0");
                
            }
            else if(rsClear == 4){
                rsClear = 0;
                rs4.setBusy("0");
                rs4.setOp("   ");
                rs4.setQj("   ");
                rs4.setQk("   ");
                rs4.setVj("  ");
                rs4.setVk("  ");
                rs4.setDisp("0");
                
            }
            else if(rsClear == 5){
                rsClear = 0;
                rs5.setBusy("0");
                rs5.setOp("   ");
                rs5.setQj("   ");
                rs5.setQk("   ");
                rs5.setVj("  ");
                rs5.setVk("  ");
                rs5.setDisp("0");
                
            }
            
            
            
            
            
            
            
            //-----------------SPECIAL CASE: BROADCAST CYCLE == ISSUE CYCLE-------------------//
            //------we need to broadcast first before issue--------------//
            int vj;
            int vk;
            int value = 0;
            String currentRS =  null;
            int Opget = 0;
            int dc = 0;
            if((instrIndexI != -1) && (instrIndexW != -1)){
                if(issue[instrIndexI] == write[instrIndexW]){
                       System.out.println("----------TOOK THE SPECIAL CASE-------");
                    
                    Opget = instrHold.observe(instrIndexW).getOpCode();
                
                if(Opget == 0 || Opget == 1){
                    //if true --> instruction is in RS
                    if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs1.setDisp("0");
                        if(((!rs1.getVj().equals("  "))  || (!rs1.getVj().equals("   "))) && ((!rs1.getVk().equals("  ")) || (!rs1.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs1.getVj());
                            vk = Integer.parseInt(rs1.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }    
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs2.setDisp("0");
                        if(((!rs2.getVj().equals("  "))  || (!rs2.getVj().equals("   "))) && ((!rs2.getVk().equals("  ")) || (!rs2.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs2.getVj());
                            vk = Integer.parseInt(rs2.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs3.setDisp("0");
                        if(((!rs3.getVj().equals("  "))  || (!rs3.getVj().equals("   "))) && ((!rs3.getVk().equals("  ")) || (!rs3.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs3.getVj());
                            vk = Integer.parseInt(rs3.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }

                    }
                    
                    
                    
                }
                else if(Opget == 2 || Opget == 3){
                    //if true --> instruction is in RS
                    if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs4.setDisp("0");
                        if(((!rs4.getVj().equals("  "))  || (!rs4.getVj().equals("   "))) && ((!rs4.getVk().equals("  ")) || (!rs4.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs4.getVj());
                            vk = Integer.parseInt(rs4.getVk());
                            if(Opget == 2){
                                 value = vj * vk;
                            }
                            else if(Opget == 3){
                                value = vj / vk;
                            }
                        }
                        
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs5.setDisp("0");
                        if(((!rs5.getVj().equals("  "))  || (!rs5.getVj().equals("   "))) && ((!rs5.getVk().equals("  ")) || (!rs5.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs5.getVj());
                            vk = Integer.parseInt(rs5.getVk());
                            if(Opget == 2){
                                 value = vj * vk;
                            }
                            else if(Opget == 3){
                                value = vj / vk;
                            }
                        }
                        
                    }
                    
                    
                     
                }
                
                if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS1";
                    rsClear = 1;
                    rs1.setBusy("0");
                    
                }
                if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS2";
                    rsClear = 2;
                    rs2.setBusy("0");
                }
                if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS3";
                    rsClear = 3;
                    rs3.setBusy("0");
                }
                if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS4";
                    rsClear = 4;
                    rs4.setBusy("0");
                }
                if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    
                    currentRS = "RS5";
                    rsClear = 5;
                    rs5.setBusy("0");
                }
                
                if(currentRS == rs1.getQj()){
                    
                  
                    rs1.setVj(Integer.toString(value));
                    rs1.setQj("   ");
                    
                }
                if (currentRS == rs1.getQk()){
                    
                    
                    rs1.setVk(Integer.toString(value));
                    rs1.setQk("   ");
                    
                }
                if (currentRS == rs2.getQj()){
                    
                    rs2.setVj(Integer.toString(value));
                    rs2.setQj("   ");
                }
                if (currentRS == rs2.getQk()){
                    
                    rs2.setVk(Integer.toString(value));
                    rs2.setQk("   ");
                }
                if (currentRS == rs3.getQj()){
                    
                    rs3.setVj(Integer.toString(value));
                    rs3.setQj("   ");
                    
                }
                if (currentRS == rs3.getQk()){
                    
                    rs3.setVk(Integer.toString(value));
                    rs3.setQk("   ");
                    
                }
                if (currentRS == rs4.getQj()){
                    
                    rs4.setVj(Integer.toString(value));
                    rs4.setQj("   ");
                    
                }
                if (currentRS == rs4.getQk()){
                    
                    rs4.setVk(Integer.toString(value));
                    rs4.setQk("   ");
                }
                if (currentRS == rs5.getQj()){
                    
                    rs5.setVj(Integer.toString(value));
                    rs5.setQj("   ");
                }
                if (currentRS == rs5.getQk()){
                    
                    rs5.setVk(Integer.toString(value));
                    rs5.setQk("   ");
                }
                
                
                
                for(int q = 0; q < 8; q++){
                    if(RAT[q].equals(currentRS)){
                        RF[q] = value;
                        RAT[q] = "R" + q;
                        break;
                    }
                   
                }

                specialCaseVar = 1;
                
                }
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            //------------Issue Algorithm--------------//
            dc = 0;
            if(instrIndexI != -1){
                
                if(instrHold.observe(instrIndexI).getOpCode() == 0 || instrHold.observe(instrIndexI).getOpCode() == 1){
                
                    if((freeRS1 == 1) && (dc != 1)){
                        dc = 1;
                        //Setting RS matcher = Instruction matcher
                        rs1.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        
                        
                        
                        freeRS1 = 0;
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
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs1.setVj(vjIntToString);
                                        rs1.setQj("   ");

                                }
                                else{

                                    rs1.setVj("  ");
                                    rs1.setQj(RAT[sourceOP1]);

                                }

                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs1.setVk(vkIntToString);
                                        rs1.setQk("   ");

                                }
                                else{

                                    rs1.setVk("  ");
                                    rs1.setQk(RAT[sourceOP2]);

                                }
                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS1";

                                rs1.setDisp("0");
                    }
                    if((freeRS2 == 1) && (dc != 1)){
                        dc = 1;
                        //Setting RS matcher = Instruction matcher
                        rs2.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        
                        
                        freeRS2 = 0;
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
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs2.setVj(vjIntToString);
                                        rs2.setQj("   ");

                                }
                                else{

                                    rs2.setVj("  ");
                                    rs2.setQj(RAT[sourceOP1]);

                                }

                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs2.setVk(vkIntToString);
                                        rs2.setQk("   ");

                                }
                                else{

                                    rs2.setVk("  ");
                                    rs2.setQk(RAT[sourceOP2]);

                                }
                                
                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS2";
                                rs2.setDisp("0");
                    }
                    if((freeRS3 == 1) && (dc != 1)){
                        //Setting RS matcher = Instruction matcher
                        rs3.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        
                        freeRS3 = 0;
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
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs3.setVj(vjIntToString);
                                        rs3.setQj("   ");

                                }
                                else{

                                    rs3.setVj("  ");
                                    rs3.setQj(RAT[sourceOP1]);

                                }

                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs3.setVk(vkIntToString);
                                        rs3.setQk("   ");

                                }
                                else{

                                    rs3.setVk("  ");
                                    rs3.setQk(RAT[sourceOP2]);

                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS3";

                                rs3.setDisp("0");
                    }
                    
                }
                else if(instrHold.observe(instrIndexI).getOpCode() == 2 || instrHold.observe(instrIndexI).getOpCode() == 3){
                    
                    if(freeRS4 == 1 && dc != 1){
                        dc = 1;
                        //Setting RS matcher = Instruction matcher
                        rs4.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        
                        freeRS4 = 0;
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
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs4.setVj(vjIntToString);
                                        rs4.setQj("   ");

                                }
                                else{

                                    rs4.setVj("  ");
                                    rs4.setQj(RAT[sourceOP1]);

                                }

                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs4.setVk(vkIntToString);
                                        rs4.setQk("   ");

                                }
                                else{

                                    rs4.setVk("  ");
                                    rs4.setQk(RAT[sourceOP2]);

                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS4";
                                
                                rs4.setDisp("0");
                    }
                    if(freeRS5 == 1 && dc != 1){
                        dc = 1;
                        //Setting RS matcher = Instruction matcher
                        rs5.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        
                        freeRS5 = 0;
                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 2:
                                rs5.setOp("MUL");
                                break;
                            case 3:
                                rs5.setOp("DIV");
                                break;
                        }

                                rs5.setBusy("1");
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs5.setVj(vjIntToString);
                                        rs5.setQj("   ");

                                }
                                else{

                                    rs5.setVj("  ");
                                    rs5.setQj(RAT[sourceOP1]);

                                }

                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs5.setVk(vkIntToString);
                                        rs5.setQk("   ");

                                }
                                else{

                                    rs5.setVk("  ");
                                    rs5.setQk(RAT[sourceOP2]);

                                }

                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS5";

                                rs5.setDisp("0");
                    }
                    
                }
                
            }
            else if(instrIndexI == -1){
                
                //do nothing
                
            }
            
            //-----------------Issue Algorithm finished---------------//
            
            
            
            //Compare using match value associated with both the instruction and the RS
            //setting dispatch to 1 if RS Matcher = Instuction Matcher. This means instruction is in that particular RS
            Opget = 0;
            if(instrIndexE != -1){
                 //get opcode from instruction
                Opget = instrHold.observe(instrIndexE).getOpCode();
                
                if(Opget == 0 || Opget == 1){
                    
                    //if true --> instruction is in RS
                    if(rs1.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs1.setDisp("1");
                        
                        
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs2.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs2.setDisp("1");
                        
                        
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs3.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs3.setDisp("1");
                        
                        
                        
                    }
                    
                    
                    
                }
                else if(Opget == 2 || Opget == 3){
                    //if true --> instruction is in RS
                    if(rs4.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs4.setDisp("1");
                        
                        
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs5.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs5.setDisp("1");
                        
                        
                        
                    }
                     
                }
                
            }
            
            if(instrIndexW != -1 && specialCaseVar == 1){
                specialCaseVar = 0;
                    if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                          freeRS1 = 1;
                    }
                    if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                          freeRS2 = 1;
                    }
                    if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                          freeRS3 = 1;
                    }
                    if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                          freeRS4 = 1;
                    }
                    if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                          freeRS5 = 1;
                    }
            instrIndexW = -1;
            }
            
            
            //---------------------------WRITE---------------------------//
            
            value = 0;
            currentRS =  null;
            if(instrIndexW != -1){
                
                Opget = instrHold.observe(instrIndexW).getOpCode();
                
                if(Opget == 0 || Opget == 1){
                    //if true --> instruction is in RS
                    if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs1.setDisp("0");
                        if(((!rs1.getVj().equals("  "))  || (!rs1.getVj().equals("   "))) && ((!rs1.getVk().equals("  ")) || (!rs1.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs1.getVj());
                            vk = Integer.parseInt(rs1.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }    
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs2.setDisp("0");
                        if(((!rs2.getVj().equals("  "))  || (!rs2.getVj().equals("   "))) && ((!rs2.getVk().equals("  ")) || (!rs2.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs2.getVj());
                            vk = Integer.parseInt(rs2.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs3.setDisp("0");
                        if(((!rs3.getVj().equals("  "))  || (!rs3.getVj().equals("   "))) && ((!rs3.getVk().equals("  ")) || (!rs3.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs3.getVj());
                            vk = Integer.parseInt(rs3.getVk());
                            if(Opget == 0){
                                 value = vj + vk;
                            }
                            else if(Opget == 1){
                                value = vj - vk;
                            }
                        }

                    }
                    
                    
                    
                }
                else if(Opget == 2 || Opget == 3){
                    //if true --> instruction is in RS
                    if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs4.setDisp("0");
                        if(((!rs4.getVj().equals("  "))  || (!rs4.getVj().equals("   "))) && ((!rs4.getVk().equals("  ")) || (!rs4.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs4.getVj());
                            vk = Integer.parseInt(rs4.getVk());
                            if(Opget == 2){
                                 value = vj * vk;
                            }
                            else if(Opget == 3){
                                value = vj / vk;
                            }
                        }
                        
                        
                    }
                    //if true --> instruction is in RS
                    else if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        rs5.setDisp("0");
                        if(((!rs5.getVj().equals("  "))  || (!rs5.getVj().equals("   "))) && ((!rs5.getVk().equals("  ")) || (!rs5.getVk().equals("   ")))){
                            vj = Integer.parseInt(rs5.getVj());
                            vk = Integer.parseInt(rs5.getVk());
                            if(Opget == 2){
                                 value = vj * vk;
                            }
                            else if(Opget == 3){
                                value = vj / vk;
                            }
                        }
                        
                    }
                    
                    
                     
                }
                
                if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS1";
                    rsClear = 1;
                    freeRS1 = 1;
                    rs1.setBusy("0");
                    
                }
                if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS2";
                    rsClear = 2;
                    freeRS2 = 1;
                    rs2.setBusy("0");
                }
                if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS3";
                    rsClear = 3;
                    freeRS3 = 1;
                    rs3.setBusy("0");
                }
                if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS4";
                    rsClear = 4;
                    freeRS4 = 1;
                    rs4.setBusy("0");
                }
                if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS5";
                    rsClear = 5;
                    freeRS5 = 1;
                    rs5.setBusy("0");
                }
                
                if(currentRS == rs1.getQj()){
                    
                  
                    rs1.setVj(Integer.toString(value));
                    rs1.setQj("   ");
                    
                }
                if (currentRS == rs1.getQk()){
                    
                    
                    rs1.setVk(Integer.toString(value));
                    rs1.setQk("   ");
                    
                }
                if (currentRS == rs2.getQj()){
                    
                    rs2.setVj(Integer.toString(value));
                    rs2.setQj("   ");
                }
                if (currentRS == rs2.getQk()){
                    
                    rs2.setVk(Integer.toString(value));
                    rs2.setQk("   ");
                }
                if (currentRS == rs3.getQj()){
                    
                    rs3.setVj(Integer.toString(value));
                    rs3.setQj("   ");
                    
                }
                if (currentRS == rs3.getQk()){
                    
                    rs3.setVk(Integer.toString(value));
                    rs3.setQk("   ");
                    
                }
                if (currentRS == rs4.getQj()){
                    
                    rs4.setVj(Integer.toString(value));
                    rs4.setQj("   ");
                    
                }
                if (currentRS == rs4.getQk()){
                    
                    rs4.setVk(Integer.toString(value));
                    rs4.setQk("   ");
                }
                if (currentRS == rs5.getQj()){
                    
                    rs5.setVj(Integer.toString(value));
                    rs5.setQj("   ");
                }
                if (currentRS == rs5.getQk()){
                    
                    rs5.setVk(Integer.toString(value));
                    rs5.setQk("   ");
                }
                
                
                
                for(int q = 0; q < 8; q++){
                    if(RAT[q].equals(currentRS)){
                        RF[q] = value;
                        RAT[q] = "R" + q;
                        break;
                    }
                   
                }
                
                }
            }
            
        pf.printRFRAT(RF, RAT);
        pf.printRS(rs1, rs2, rs3, rs4, rs5);
        
        
        
    }
    
}
