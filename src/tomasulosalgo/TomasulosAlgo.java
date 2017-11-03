//Dylan Visto; Julian Thrash
//The main class that runs the simulation

package tomasulosalgo;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TomasulosAlgo {

    public static void main(String[] args) {
        
        //----------------------OPENING AND READING FILE------------------------//
        
        //Declares the name of the text file
        String fileName = "tomasulo.txt";
        //Variable to hold the total number of instructions
        int numInstr = 0; 
        //Variable holds the number of cycles we run the simmulation for
        int numCycles = 0;
        //Variable that holds the current cycle of execution
        int currentCycle = 0;
        //Variable to link a RS to an instruction
        int matcher = 0;
        //Varibles to hold the instruction parameters
        int opCode, destOp, sourceOp1, sourceOp2 = 0;
        //This holds all the different instructions, it can hold 10 possible instructions
        ArrayQueue<Instruction> instrHold = new ArrayQueue<>(10);
        //This array holds all the RF values
        int[] RF = new int[8];
        //This holds the tags or pointers to the RF
        String[] RAT = new String[8];
        //Creating instance of my PrintFunctions class
        PrintFunctions pf = new PrintFunctions();
        
        //This is a Try-Catch block that handles the file not found exception
        try {
            //Scanner object that will determine what values in the text file get assigned to variables in the program
            Scanner scanner = new Scanner(new File(fileName));
            //Using the scanner object to find the next integer in the text file and assigns it to numInstr
            numInstr = scanner.nextInt();
            //Using the scanner object to find the next integer in the text file and assigns it to numCycle
            numCycles = scanner.nextInt();
            //This creates a new instruction and stores it into our Array Queue after assigning variables by scanning the next integers
            //Also assigns matcher to a unique value so that each instruction is unique
            for(int i = 0; i < numInstr; i++){
                opCode = scanner.nextInt();
                destOp = scanner.nextInt();
                sourceOp1 = scanner.nextInt();
                sourceOp2 = scanner.nextInt();
                matcher = i+1;
                instrHold.enqueue(new Instruction(opCode, destOp, sourceOp1, sourceOp2, matcher));  
            }
            //Stores the rest of the integers in the text file and places them into the RF array
            for(int j = 0; j < 8; j++){
                RF[j] = scanner.nextInt();   
            }
            //Stores the initial pointers to the RF in the RAT
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
        
        //----------------------DONE OPENING AND READING FILE------------------------//
        
        
        //Printing the initial RAT, RF, and INSTRUCTION QUEUE
        System.out.println("Initial RAT, RF, and Instruction Queue");
        pf.printRFRAT(RF, RAT);
        pf.printInstructionQueue(instrHold, numInstr);
         
        
        
        
        //---------------------FINDING ISSUE EXECUTE WRITE CYCLES---------------------//
        
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
        //Counts the number of previous add or subtract instructions
        int addCount = 0;
        //Counts the number of previous multiply or divide instructions
        int multCount = 0;
        //This variable is used for a conditional statement if there is a WAW dependency
        int writeCheck = 0;
        //This variable allows you to enter a conditional statement that compensates for a RAW dependency
        int RAW = 0;
        //This variable determines which of the last three or two instructions (depending on add/sub or mult/div instructions) will broadcast first
        int lowestWrite = 0;
        //This variable determines what instruction has the highest write cycle with a RAW dependency
        int highestRaw = 0;
        int issueCycle = 1;
        //Keeps track of the current number of in use Reservation Stations for Add or Subtract
        int RSA = 0;
        //Keeps track of the current number of in use Reservation Stations for Mult or Divide
        int RSM = 0;
        //This allows you get access to the conditional statement that determines what order the instructions are written
        int go = 0;
        
        //Creates the issue, execute, and write cycles for each instruction
        for(int i = 0; i < numInstr; i++) {
            lowestWrite = 0;
            writeCheck = 0;
            multCount = 0;
            addCount = 0;
            go = 1;
            
            //-------------------Find Issue Cycle-------------------//
            
            
           
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
            
            //---------------------Issue Cycle Done-------------------//
            
            
            
            
            
            
            
            //-------------------Find Execution Cycle--------------//
            
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
            
            //------------------Execution Cycle Done----------------//
            
            
            
            //-----------------Find Write Cycle-------------------//
            
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
            
            //---------------Write Cycle Done---------------------//
            
            
        }
        
        
     
        //---------------Cycle Arrays Done (Issue, Execute, Write)---------------//
        
            //Prints off the cycle diagram
            pf.printInstInfo(instrHold, numInstr, issue, execute, write); 
        
        
            
            
            
            
            
            
            
           
        //---------------RS/RAT/RF Updating Algorithm------------------//
        //Declarations for RS/RAT/RF 
        
        //Update the RS, RAT, and RF for each instruction until the number of cycles in the text file
        ReservationStation rs1 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs2 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs3 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs4 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        ReservationStation rs5 = new ReservationStation("0", "   ", "  ", "  ", "   ", "   ", " ", 0);
        
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
        int specialCaseVar = 0;
        int twoFU = 0;
        
        
        //Begin looping through the cycles starting at 1
        for(int i = 1; i <= numCycles; i++){
            
            activeCycle = 0;
            instrIndexI = -1;
            instrIndexE = -1;
            instrIndexW = -1;
            
            //Loops through Issue Cycle array to see if 'i' is stored in there 
            for(int j = 0; j < numInstr; j++){
                if(issue[j] == i){
                    instrIndexI = j;
                    activeCycle = 1;
                    break;
                }
            }
            
            //Loops through Execution Cycle array to see if 'i' is stored in there 
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
            
            //Loops through Write Cycle array to see if 'i' is stored in there
            for(int j = 0; j < numInstr; j++){   
                if(write[j] == i){
                    instrIndexW = j;
                    activeCycle = 1;
                    break;
                }
            }
            
            //Checks for empty cycles
            if(activeCycle != 1){
                System.out.println("Cycle " + i + " was empty"); 
            }
            
            //Clears Reservations stations depending on the value of rsClear
            switch (rsClear) {
                case 1:
                    rsClear = 0;
                    rs1.setBusy("0");
                    rs1.setOp("   ");
                    rs1.setQj("   ");
                    rs1.setQk("   ");
                    rs1.setVj("  ");
                    rs1.setVk("  ");
                    rs1.setDisp("0");
                    break;
                case 2:
                    rsClear = 0;
                    rs2.setBusy("0");
                    rs2.setOp("   ");
                    rs2.setQj("   ");
                    rs2.setQk("   ");
                    rs2.setVj("  ");
                    rs2.setVk("  ");
                    rs2.setDisp("0");
                    break;
                case 3:
                    rsClear = 0;
                    rs3.setBusy("0");
                    rs3.setOp("   ");
                    rs3.setQj("   ");
                    rs3.setQk("   ");
                    rs3.setVj("  ");
                    rs3.setVk("  ");
                    rs3.setDisp("0");
                    break;
                case 4:
                    rsClear = 0;
                    rs4.setBusy("0");
                    rs4.setOp("   ");
                    rs4.setQj("   ");
                    rs4.setQk("   ");
                    rs4.setVj("  ");
                    rs4.setVk("  ");
                    rs4.setDisp("0");
                    break;
                case 5:
                    rsClear = 0;
                    rs5.setBusy("0");
                    rs5.setOp("   ");
                    rs5.setQj("   ");
                    rs5.setQk("   ");
                    rs5.setVj("  ");
                    rs5.setVk("  ");
                    rs5.setDisp("0");
                    break;
                default:
                    break;
            }
            
            
            
            
            
            
            
            //-----------------SPECIAL CASE: BROADCAST CYCLE == ISSUE CYCLE-------------------//
            //In this special case, you need to broadcast before you issue so that the issuing instruction can capture the values being written to the RF
            //Check the Write Stage at the bottom of the page to see comments for Writing
            int vj;
            int vk;
            int Opget = 0;
            int dc = 0;
            int value = 0;
            String currentRS =  "";
            if((instrIndexI != -1) && (instrIndexW != -1)){
                if(issue[instrIndexI] == write[instrIndexW]){
                    Opget = instrHold.observe(instrIndexW).getOpCode();
                if(Opget == 0 || Opget == 1){
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
                
                if( currentRS.equals(rs1.getQj())){
                    rs1.setVj(Integer.toString(value));
                    rs1.setQj("   ");
                }
                if (currentRS.equals(rs1.getQk())){
                    rs1.setVk(Integer.toString(value));
                    rs1.setQk("   ");
                }
                if (currentRS.equals(rs2.getQj())){
                    rs2.setVj(Integer.toString(value));
                    rs2.setQj("   ");
                }
                if (currentRS.equals(rs2.getQk())){
                    rs2.setVk(Integer.toString(value));
                    rs2.setQk("   ");
                }
                if (currentRS.equals(rs3.getQj())){
                    rs3.setVj(Integer.toString(value));
                    rs3.setQj("   ");
                }
                if (currentRS.equals(rs3.getQk())){
                    rs3.setVk(Integer.toString(value));
                    rs3.setQk("   ");
                }
                if (currentRS.equals(rs4.getQj())){
                    rs4.setVj(Integer.toString(value));
                    rs4.setQj("   ");
                }
                if (currentRS.equals(rs4.getQk())){
                    rs4.setVk(Integer.toString(value));
                    rs4.setQk("   ");
                }
                if (currentRS.equals(rs5.getQj())){
                    rs5.setVj(Integer.toString(value));
                    rs5.setQj("   ");
                }
                if (currentRS.equals(rs5.getQk())){
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
                //when taking the special case, you dont need to write then since this is what this case does
                specialCaseVar = 1;
                }
            }
            
            
            //------------Issue--------------//
            //Makes sure you don't place multiple of the same instruction in all of the RS
            dc = 0;
            //If instrIndexI was set to a value besides -1 then you know you have to issue this cycle
            if(instrIndexI != -1){
                //If instruction is add or sub
                if(instrHold.observe(instrIndexI).getOpCode() == 0 || instrHold.observe(instrIndexI).getOpCode() == 1){
                    //If RS1 is free and we have not double counted
                    if((freeRS1 == 1) && (dc != 1)){
                        //denies access to any other RS
                        dc = 1;
                        //Links an instruction to a reservation station
                        rs1.setMatcher(instrHold.observe(instrIndexI).getMatcher());
                        //This reservation station is in use now
                        freeRS1 = 0;
                        //Choose whether the op is add or sub
                        switch (instrHold.observe(instrIndexI).getOpCode()) {
                            case 0:
                                rs1.setOp("ADD");
                                break;
                            case 1:
                                rs1.setOp("SUB");
                                break;
                        }
                                //RS1 is busy now
                                rs1.setBusy("1");
                                //get the two source ops to look inside the RAT to see if its pointing to the RF
                                sourceOP1 = instrHold.observe(instrIndexI).getSourceOp1();
                                sourceOP2 = instrHold.observe(instrIndexI).getSourceOp2();
                                //If it is pointint to the RF, then Qj is empty and Vj gets the RF value
                                if(RAT[sourceOP1].equals("R"+(sourceOP1))){
                                        vjIntToString = Integer.toString(RF[sourceOP1]);
                                        rs1.setVj(vjIntToString);
                                        rs1.setQj("   ");
                                }
                                //Else Vj is empty and Qj gets the Tag
                                else{
                                    rs1.setVj("  ");
                                    rs1.setQj(RAT[sourceOP1]);
                                }
                                //Same thing as above
                                if(RAT[sourceOP2].equals("R"+(sourceOP2))){
                                        vkIntToString = Integer.toString(RF[sourceOP2]);
                                        rs1.setVk(vkIntToString);
                                        rs1.setQk("   ");
                                }
                                else{
                                    rs1.setVk("  ");
                                    rs1.setQk(RAT[sourceOP2]);
                                }
                                //Set the tag in the destination ops location to be the current RS you are in
                                RAT[instrHold.observe(instrIndexI).getDestOp()] = "RS1";
                                rs1.setDisp("0");
                    }
                    //Same thing as above
                    if((freeRS2 == 1) && (dc != 1)){
                        dc = 1;
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
                    //Same thing as above
                    if((freeRS3 == 1) && (dc != 1)){
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
                //Same thing as above but its Mul and Div
                else if(instrHold.observe(instrIndexI).getOpCode() == 2 || instrHold.observe(instrIndexI).getOpCode() == 3){
                    if(freeRS4 == 1 && dc != 1){
                        dc = 1;
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
                    //Same thing as above but its Mul and Div
                    if(freeRS5 == 1 && dc != 1){
                        dc = 1;
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
            
            
            //-----------------Issue Algorithm finished---------------//
            
            
            
            
            //-----------------Execute-----------------//
            
            
            //Compare using match value associated with both the instruction and the RS
            //setting dispatch to 1 if RS Matcher = Instuction Matcher. This means instruction is in that particular RS
            Opget = 0;
            //Allows access if we are executing this cycle
            if(instrIndexE != -1){
                //Opget stores the opcode value of the instruction
                Opget = instrHold.observe(instrIndexE).getOpCode();
                //If add or sub
                if(Opget == 0 || Opget == 1){
                    //This is where the linking system between instruction and reservation station really shines
                    //We know what instruction is in what reservation station now and we know the index of what instruction needs to execute so this sets the dispatch notifier
                    if(rs1.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs1.setDisp("1");
                    }
                    else if(rs2.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs2.setDisp("1");
                    }
                    else if(rs3.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs3.setDisp("1");
                    }
                }
                else if(Opget == 2 || Opget == 3){
                    if(rs4.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs4.setDisp("1");
                    }
                    else if(rs5.getMatcher() == instrHold.observe(instrIndexE).getMatcher()){
                        rs5.setDisp("1");
                    }
                }
            }
            //If we took the special case above where we need to broadcast before issuing then it enters this
            if(instrIndexW != -1 && specialCaseVar == 1){
                //set the var back to 0
                specialCaseVar = 0;
                    //free the reservation station for the next cycle
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
                    //set instrIndexW back to -1
                    instrIndexW = -1;
            }
            //------------------------Execute Finished-----------------------//
            
            
            
            //---------------------------WRITE---------------------------//
            //Stores the value of the evacuated instruction
            value = 0;
            //Set currentRS back to null
            currentRS =  null;
            //Only allows access during a cycle that needs to write
            if(instrIndexW != -1){
                //Gets the opcode
                Opget = instrHold.observe(instrIndexW).getOpCode();
                //If add or subtract
                if(Opget == 0 || Opget == 1){
                    //Finds the instruction in the reservation stations
                    if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                        //In write stage now so dispatch notifier needs to be set back to 0
                        rs1.setDisp("0");
                        //If the vj and vk values are not empty then enter
                        if(((!rs1.getVj().equals("  "))  || (!rs1.getVj().equals("   "))) && ((!rs1.getVk().equals("  ")) || (!rs1.getVk().equals("   ")))){
                            //evaluates the vj and vk values depending on the opcode
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
                    //same as above
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
                    //same as above
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
                //same as above except its multiply and divide
                else if(Opget == 2 || Opget == 3){
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
                //Sets the current Reservation station for the writing instructions
                //Also set the reservation station to be cleared after this cycle
                //Sets the current reservation station to be open
                //Also sets Busy to be 0 for the current reservation station
                if(rs1.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS1";
                    rsClear = 1;
                    freeRS1 = 1;
                    rs1.setBusy("0");
                }
                //same as above
                if(rs2.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS2";
                    rsClear = 2;
                    freeRS2 = 1;
                    rs2.setBusy("0");
                }
                //same as above
                if(rs3.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS3";
                    rsClear = 3;
                    freeRS3 = 1;
                    rs3.setBusy("0");
                }
                //same as above
                if(rs4.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS4";
                    rsClear = 4;
                    freeRS4 = 1;
                    rs4.setBusy("0");
                }
                //same as above
                if(rs5.getMatcher() == instrHold.observe(instrIndexW).getMatcher()){
                    currentRS = "RS5";
                    rsClear = 5;
                    freeRS5 = 1;
                    rs5.setBusy("0");
                }
                //Checks all the Qj and Qk fields to see if Vj or Vk need to be updated this cycle
                if(currentRS.equals(rs1.getQj())){
                    rs1.setVj(Integer.toString(value));
                    rs1.setQj("   ");
                }
                //same as above
                if (currentRS.equals(rs1.getQk())){
                    rs1.setVk(Integer.toString(value));
                    rs1.setQk("   ");
                }
                //same as above
                if (currentRS.equals(rs2.getQj())){
                    rs2.setVj(Integer.toString(value));
                    rs2.setQj("   ");
                }
                //same as above
                if (currentRS.equals(rs2.getQk())){
                    rs2.setVk(Integer.toString(value));
                    rs2.setQk("   ");
                }
                //same as above
                if (currentRS.equals(rs3.getQj())){
                    rs3.setVj(Integer.toString(value));
                    rs3.setQj("   ");
                }
                //same as above
                if (currentRS.equals(rs3.getQk())){
                    rs3.setVk(Integer.toString(value));
                    rs3.setQk("   ");
                }
                //same as above
                if (currentRS.equals(rs4.getQj())){
                    rs4.setVj(Integer.toString(value));
                    rs4.setQj("   ");
                }
                //same as above
                if (currentRS.equals(rs4.getQk())){
                    rs4.setVk(Integer.toString(value));
                    rs4.setQk("   ");
                }
                //same as above
                if (currentRS.equals(rs5.getQj())){
                    rs5.setVj(Integer.toString(value));
                    rs5.setQj("   ");
                }
                //same as above
                if (currentRS.equals(rs5.getQk())){
                    rs5.setVk(Integer.toString(value));
                    rs5.setQk("   ");
                }
                
                //Sets the RAT to point back to the RF
                //If its a stale result then it won't update the RAT or RF
                for(int q = 0; q < 8; q++){
                    if(RAT[q].equals(currentRS)){
                        RF[q] = value;
                        RAT[q] = "R" + q;
                        break;
                    }
                }
                }
            }
        //printing functions that display the RF, RAT, and Reservation Stations for each cycle
        pf.printRFRAT(RF, RAT);
        pf.printRS(rs1, rs2, rs3, rs4, rs5);
    }
}