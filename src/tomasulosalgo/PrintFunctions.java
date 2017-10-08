
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
    
    
    
}
