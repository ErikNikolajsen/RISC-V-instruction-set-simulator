package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class Main {
	
    static int pc;
    static int[] reg;
    static byte[] memory;

    public static void main(String[] args) {
    	
    	if (args.length != 2) {
    		System.out.println("Incorrect number of arguments");
    	} else if (!(new File(args[0]).isFile())) {
    		System.out.println("First argument is not a valid file");
    	} else if (new File(args[0]).isFile()) { 
    		runProcessor(new File(args[0]), new File(args[1]));
    	} 
    	
    }
    
	public static void runProcessor(File inputFile, File outputFile) {
		pc = 0; // reset program counter
		reg = new int[32]; // 32 registers
        memory = new byte[1048576]; // 1 MiB memory
        
        loadProgramFromFile(inputFile);

        boolean branch = false;
        boolean halt = false;
        

        while(true) {
        	
            int instr = (memory[pc] & 0xff) | ((memory[pc + 1] & 0xff) << 8) | ((memory[pc + 2] & 0xff) << 16) | (memory[pc + 3] << 24);
            int opcode = instr & 0x7F;
            int rd = (instr >> 7) & 0x1F;
            int rs1 = (instr >> 15) & 0x1F;
            int rs2 = (instr >> 20) & 0x1F;
            int funct3 = (instr >> 12) & 0x7;
            int funct7 = (instr >> 25);
            int immI = (instr >> 20);
            int immS = ((instr >> 25) << 5) | ((instr >> 7) & 0x1F);
            int immB = ((instr >> 7) & 0x1E) | ((instr >> 20) & 0x7E0) | ((instr << 4) & 0x800) | ((instr >> 31) << 12);
            int immU = instr & 0xFFFFF000;
            int immJ = ((instr >> 20) & 0x7FE) | ((instr >> 9) & 0x800) | (instr & 0xFF000) | ((instr >> 31) << 19);
            
            
            switch (opcode) {
            	case 0x37: // LUI (load upper immediate)
            		reg[rd] = immU;
            		break;
            	case 0x17: // AUIPC (add upper immediate to PC)
            		reg[rd] = immU + pc;
            		break;
            	case 0x6F: // JAL (jump & link)
            		reg[rd] = pc + 4;
            		pc += immJ;
            		branch = true;
            		break;
            	case 0x67: // JALR (jump & link register)
            		reg[rd] = pc + 4;
            		pc = reg[rs1] + immI;
            		branch = true;
            		break;
            	case 0x63: // BEQ/BNE/BLT/BGE/BLTU/BGEU
            		
            		switch (funct3) { 
	                	case 0x0: // BEQ (branch if equal)
	                		if (reg[rs1] == reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x1: // BNE (branch if not equal)
	                		if (reg[rs1] != reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x4: // BLT (branch if less than)
	                		if (reg[rs1] < reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x5: // BGE (branch if greater than or equal)
	                		if (reg[rs1] >= reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x6: // BLTU (branch if less than unsigned)
	                		if ((reg[rs1] & 0xFFFFFFFFL) < (reg[rs2] & 0xFFFFFFFFL)) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x7: // BGEU (branch if greater than or equal)
	                		if ((reg[rs1] & 0xFFFFFFFFL) >= (reg[rs2] & 0xFFFFFFFFL)) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
	            	}
	            	break;
            		
            	case 0x3: // LB/LH/LW/LBU/LHU
            		
            		switch (funct3) { 
	                	case 0x0: // LB (load byte)
	                		reg[rd] = memory[reg[rs1] + immI];
	                		break;
	                	case 0x1: // LH (load halfword)
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | (memory[reg[rs1] + immI + 1] << 8);
	                		break;
	                	case 0x2: // LW (load word)
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | ((memory[reg[rs1] + immI + 1] & 0xff) << 8) | ((memory[reg[rs1] + immI + 2] & 0xff) << 16) | (memory[reg[rs1] + immI + 3] << 24);
	                		break;
	                	case 0x4: // LBU (load byte unsigned)
	                		reg[rd] = memory[reg[rs1] + immI] & 0xff;
	                		break;
	                	case 0x5: // LHU (load half unsigned)
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | ((memory[reg[rs1] + immI + 1] & 0xff) << 8);
	                		break;
	                	default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
	            	}
	            	break;
            		
            	case 0x23: // SB/SH/SW
            		
            		switch (funct3) { 
	                	case 0x0: // SB (store byte)
	                		memory[reg[rs1] + immS] = (byte) (reg[rs2]);
	                		break;
	                	case 0x1: // SH (store halfword)
	                		memory[reg[rs1] + immS] = (byte) (reg[rs2] & 0xff);
	                		memory[reg[rs1] + immS + 1] = (byte) ((reg[rs2] >> 8) & 0xff);
	                		break;
	                	case 0x2: // SW (store word)
	                		memory[reg[rs1] + immS] = (byte) (reg[rs2] & 0xff);
	                		memory[reg[rs1] + immS + 1] = (byte) ((reg[rs2] >> 8) & 0xff);
	                		memory[reg[rs1] + immS + 2] = (byte) ((reg[rs2] >> 16) & 0xff);
	                		memory[reg[rs1] + immS + 3] = (byte) ((reg[rs2] >> 24) & 0xff);
	                		break;
	                	default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
	            	}
	            	break;
            		
                case 0x13: // ADDI/SLTI/SLTIU/XORI/ORI/ANDI/SLLI/SRLI/SRAI
                	
                	switch (funct3) {  
	                    case 0x0: // ADDI (add Immediate)
	                        reg[rd] = reg[rs1] + immI;
	                        break;
	                    case 0x2: // SLTI (set less than immediate)
	                    	if (reg[rs1] < immI) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x3: // SLTIU (set less than immediate unsigned)
	                    	if (reg[rs1] < (immI & 0xFFFFFFFFL)) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x4: // XORI (exclusive or immediate)
	                    	reg[rd] = reg[rs1] ^ immI;
	                        break;
	                    case 0x6: // ORI (or immediate)
                    		reg[rd] = reg[rs1] | immI;
                    		break;
	                    case 0x7: // ANDI (and immediate)
                    		reg[rd] = reg[rs1] & immI;
                    		break;
	                    case 0x1: // SLLI (shift left logical immediate)
                    		reg[rd] = reg[rs1] << immI;
                    		break;
	                    case 0x5:
	                    	
	                    	switch (funct7) { 
		                    	case 0x0: // SRLI (shift right logical immediate)
		                    		reg[rd] = reg[rs1] >>> immI;
		                        	break;
			                    case 0x20: // SRAI (shift right arithmetic immediate)
		                    		reg[rd] = reg[rs1] >> immI;
		                        	break;
			                    default:
			                        System.out.println("Funct7 " + funct7 + " for Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
			                        break;
	                    	}
	                    	break;
	                    	
	                    default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
                    }
                    break;
                
                case 0x33: // ADD/SUB/SLL/SLT/SLTU/XOR/SRL/SRA/OR/AND
                	
                	switch (funct3) { 
	                	case 0x0: // ADD/SUB
	                		
	                		switch (funct7) { 
			                	case 0x0: // ADD (addition)
			                		reg[rd] = reg[rs1] + reg[rs2];
			                		break;
			                	case 0x20: // SUB (subtraction)
			                		reg[rd] = reg[rs1] - reg[rs2];
			                		break;
			                	default:
			                        System.out.println("Funct7 " + funct7 + " for Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
			                        break;
			            	}
			            	break;
			            
	                	case 0x1: // SLL (logical left shift)
	                		reg[rd] = reg[rs1] << reg[rs2];
	                		break;
	                	case 0x2: // SLT (set if less than)
	                		if (reg[rs1] < reg[rs2]) {
	                			reg[rd] = 1;
	                		} else {
	                			reg[rs1] = 0;
	                		}
	                		break;
	                	case 0x3: // SLTU (set if less than unsigned)
	                		if (reg[rs1] < (reg[rs2] & 0xFFFFFFFFL)) {
	                			reg[rd] = 1;
	                		} else {
	                			reg[rs1] = 0;
	                		}
	                		break;
	                	case 0x4: // XOR (bitwise exclusive or)
	                		reg[rd] = reg[rs1] ^ reg[rs2]; 
	                		break;
	                	case 0x5: // SRL/SRA
	                		
	                		switch (funct7) { 
			                	case 0x0: // SRL (shift right logical)
			                		reg[rd] = reg[rs1] >>> reg[rs2];
			                		break;
			                	case 0x20: // SRA (shift right arithmetic)
			                		reg[rd] = reg[rs1] >> reg[rs2];
			                		break;
			                	default:
			                        System.out.println("Funct7 " + funct7 + " for Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
			                        break;
			            	}
			            	break;
	                	
	                	case 0x6: // OR (bitwise or)
	                		reg[rd] = reg[rs1] | reg[rs2];
	                		break;
	                	case 0x7: // AND (bitwise and)
	                		reg[rd] = reg[rs1] & reg[rs2];
	                		break;
	                	default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
	                		
	            	}
	            	break;
                	
                case 0x73: // ECALL - Environment call
                	
                	switch (reg[17]) { 
	                	case 0xA: // exit - Halts the simulator
	                		halt = true;
	                		break;
	                	default:
	                        System.out.println("a7 " + reg[17] + " for Opcode " + opcode + " not yet implemented");
	                        break;
	            	}
	            	break;
                	
                default:
                    System.out.println("Opcode " + opcode + " not yet implemented");
                    break;
            }
            
            //ensure that the value of x0 remains zero
            reg[0] = 0;
            
            // stop the processor if ecall 10 occurred
            if (halt == true) {
            	break;
            }
            
            //increment program counter if no branching occurred
            if (branch == false) {
            	pc += 4; // One instruction is four bytes
            } else {
            	branch = false;
            }
            
            //exit program if condition is met
            if ((pc >> 2) >= memory.length) {
                break;
            }
        }
        
        System.out.println();
        System.out.println("Resulting registers x0-x31:");
        for (int i = 0; i < reg.length; ++i) {
            System.out.print(reg[i] + " ");
        }
        System.out.println();
        
        if (outputFile != null) {
        	binaryDumpToFile(outputFile);
        	System.out.println("The output file \""+outputFile+"\" has been created");
        }
        
    	
    }
    
    public static void loadProgramFromFile(File file) {
    	try {
    	    // create a reader
    	    FileInputStream fis = new FileInputStream(file);
    	    BufferedInputStream reader = new BufferedInputStream(fis);
    	    
    	    // write instructions into memory in little-endian order
    	    int byteAmount = reader.available();
    	    for (int i = 0; i < byteAmount; i++) {
	    		memory[i] = (byte) reader.read();
	    	}
    	    
    	    // close the reader
    	    reader.close();

    	} catch (IOException ex) {
    		System.out.println("Program load failure");
    	    ex.printStackTrace();
    	}
    }
    
    public static void binaryDumpToFile(File outputFile) {
    	try {
    		
    	    // create a writer
    	    FileOutputStream fos = new FileOutputStream(outputFile);
    	    BufferedOutputStream writer = new BufferedOutputStream(fos);
    	    
    	    // write integers as 32-bit binary
    	    for (int i = 0; i < reg.length; ++i) {
    	    	for (int j = 0; j < 4; j++) {
    	    		writer.write((reg[i] >> 8 * j) & 0xff);
    	    	}
            }
    	    
    	    // flush remaining bytes
    	    writer.flush();
    	    
    	    // close the writer
    	    writer.close();
    	    

    	} catch (IOException ex) {
    		System.out.println("Binary dump failure");
    		ex.printStackTrace();
    	}
    }
    
    // function used for loading .res files for unit tests
    public static int[] loadTestRegisterFromFile(File file) {
		try {
		    // create a reader
		    FileInputStream fis = new FileInputStream(file);
		    BufferedInputStream reader = new BufferedInputStream(fis);
		    
		    // load register values
		    int[] input = new int[reader.available() >> 2];
		    for (int i = 0; i < input.length; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		input[i] = (reader.read() << 8 * j) | input[i];
	    	    }
	    	}
		    
		    // close the reader
		    reader.close();
		    
		    return input;
	
		} catch (IOException ex) {
			System.out.println("Program load failure");
		    ex.printStackTrace();
		    return new int[0];
		}
	}
    
    // getter used for unit tests
 	public static int[] getReg() {
 		return reg;
 	}

}
