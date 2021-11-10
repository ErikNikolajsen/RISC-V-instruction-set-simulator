package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    static int pc;
    static int[] reg = new int[32];
    static int[] progr;

    public static void main(String[] args) {

        pc = 0;
        
        progr = loadProgramFromFile(new File("./tests/task1/addlarge.bin")); //test

        for (;;) {

            int instr = progr[pc >> 2]; //divide by 4
            int opcode = instr & 0x7F;
            int rd = (instr >> 7) & 0x1F;
            int rs1 = (instr >> 15) & 0x1F;
            int rs2 = (instr >> 20) & 0x1F;
            int funct3 = (instr >> 12) & 0x7;
            int funct7 = (instr >> 25);
            int immI = (instr >> 20);
            int immS = ((instr >> 20) & 0xFE0) | ((instr >> 7) & 0x1F);
            int immB = ((instr >> 7) & 0x1E) | ((instr >> 20) & 0x7E0) | ((instr << 4) & 0x800) | ((instr >> 19) & 0x1000);
            int immU = instr & 0xFFFFF000;
            int immJ = ((instr >> 20) & 0x7FE) | ((instr >> 9) & 0x800) | (instr & 0xFF000) | ((instr >> 11) & 0x100000);

            switch (opcode) {
            	case 0x37: // LUI - Load upper immediate - 0110111 - 55 - U-type
            		reg[rd] = immU << 12;
            		break;
            	case 0x17: // AUIPC - Add Upper Imm to PC - 0010111 - 23 - U-type //this sets rd to the sum of the current PC and a 32-bit value with the low 12 bits as 0 and the high 20 bits coming from the U-type immediate.
            		reg[rd] = (immU << 12) + pc;
            		break;
            	case 0x6F: // JAL - Jump & Link - 1101111 - 111 - J-type
            		reg[rd] = pc+4;
            		pc += immJ * 4;
            	case 0x67: // JALR - Jump & Link Register - 1100111 - 103 - I-type
            		reg[rd] = pc+4;
            		pc = (immI + rs1) & 0xFFFFFFFE;  
                case 0x13: // Immediate calculations - I-Type
                	switch (funct3) {  
	                    case 0x0: // ADDI - Add Immediate - 000
	                        reg[rd] = reg[rs1] + immI;
	                        break;
	                    case 0x2: // SLTI - 010
	                    	if (reg[rs1] < immI) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x3: // SLTIU - 011
	                    	if (reg[rs1] < (immI>>>20)) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x4: // XORI - 100
	                    		reg[rd] = reg[rs1] ^ immI;
	                        break;
	                    case 0x6: // ORI - 110
                    		reg[rd] = reg[rs1] | immI;
                        break;
	                    case 0x7: // ANDI - 111
                    		reg[rd] = reg[rs1] & immI;
                        break;
	                    case 0x1: // SLLI - 001
                    		reg[rd] = reg[rs1] << immI;
                        break;
	                    case 0x5:
	                    	switch (funct7) { 
		                    	case 0x0: // SRLI - 0000000
		                    		reg[rd] = reg[rs1] >>> immI;
		                        break;
			                    case 0x20: // SRAI - 0100000
		                    		reg[rd] = reg[rs1] >> immI;
		                        break;
			                    default:
			                        System.out.println("Funct7 "+funct7+" for opcode " + opcode + " not yet implemented");
			                        break;
	                    	}
	                    default:
	                        System.out.println("Funct3 "+funct3+" for opcode " + opcode + " not yet implemented");
	                        break;
                    }
                    break;
                case 0x63: // Branches - 1100011
                	switch (funct3) { 
	                	case 0x0: // BEQ - 000
	                		if (reg[rs1] == reg[rs2]) {
	                			//!!!!!
	                		}
	                    break;
	                    default:
	                        System.out.println("Funct7 "+funct7+" for opcode " + opcode + " not yet implemented");
	                        break;
                	}
                	break;
                case 0x33: // ADD (51)
                	reg[rd] = reg[rs1] + reg[rs2];
                	break;
                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }
            
            //increment program counter
            pc += 4; // One instruction is four bytes
            
            
            
            //print register values for debugging
            for (int i = 0; i < reg.length; ++i) {
                System.out.print(reg[i] + " ");
            }
            System.out.println();
            
          //exit program if condition is met
            if ((pc >> 2) >= progr.length) {
                break;
            }
        }

        System.out.println("Program exit");
        
        
        
        binaryDumpToFile(); //test
        
        //long startTime = System.currentTimeMillis();
        //long estimatedTime = System.currentTimeMillis() - startTime;
        //System.out.println(estimatedTime); //test
		
    }
    
    public static int[] loadProgramFromFile(File file) {
    	try {
    	    // create a reader
    	    FileInputStream fis = new FileInputStream(file);
    	    BufferedInputStream reader = new BufferedInputStream(fis);

    	    // set program memory to size of program
    	    int[] input = new int[reader.available()];
    	    
    	    // read bytes into program memory as 32-bit instructions
    	    for (int i = 0; i < reader.available(); i++) {
    	    	for (int j = 0; j < 4; j++) {
    	    		input[i] = (reader.read() << 8 * j) | input[i];
	    	    }
	    	}
    	    
    	    // close the reader
    	    reader.close();
    	    
    	    return input;

    	} catch (IOException ex) {
    	    ex.printStackTrace();
    	    return new int[0];
    	}
    }
    
    public static void binaryDumpToFile() {
    	try {
    		
    	    // create a writer
    	    FileOutputStream fos = new FileOutputStream(new File("output"));
    	    BufferedOutputStream writer = new BufferedOutputStream(fos);
    	    
    	    // write integers as 32-bit binary
    	    for (int i = 0; i < 32; ++i) {
    	    	for (int j = 0; j < 4; j++) {
    	    		writer.write((reg[i] >> 8 * j) & 0xff);
    	    	}
            }
    	    
    	    // flush remaining bytes
    	    writer.flush();
    	    
    	    // close the writer
    	    writer.close();
    	    

    	} catch (IOException ex) {
    		ex.printStackTrace();
    	    //System.out.println("Binary dump failure");
    	}
    }
    
    String[] task1 = new String[] {
		"addlarge",
		"addneg",
		"addpos",
		"bool",
		"set",
		"shift",
		"shift2"
	};
	
	String[] task2 = new String[] {
		"branchcnt",
		"branchmany",
		"branchtrap"
	};
	
	String[] task3 = new String[] {
		"loop",
		"recursive",
		"string",
		"width"
	};
	/*
	public void testProcessor() {
		Main.loadProgramFromFile("./tests/task1/addlarge.bin").equals(main.reg);
	}
	*/
}
