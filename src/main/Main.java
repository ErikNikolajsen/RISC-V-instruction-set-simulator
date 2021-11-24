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
    	
//    	if (new File(args[0]).isFile()) { 
//    		runProcessor(new File(args[0]), new File(args[1]));
//    	} else {
//    		System.out.println("Program file does not exist");
//    	}
    	
    	runProcessor(new File(args[0]), new File(args[1]));
//    	runProcessor(new File("./src/main/addlarge.bin"), new File("./src/main/addlarge.res"));
//    	runProcessor(new File("./src/main/addlarge.bin"));
    }
    
	public static void runProcessor(File inputFile, File outputFile) {
		pc = 0; // reset program counter
		reg = new int[32]; // 32 registers
        memory = new byte[1048576]; // 1 MiB memory
        
        loadProgramFromFile(inputFile);

        boolean branch = false;
        boolean halt = false;
        

        while(true) {
        	
//        	System.out.print("pc("+pc+") "); //test

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
            	case 0x37: // LUI (load upper immediate). Is used to build 32-bit constants and uses the U-type format. LUI places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest 12 bits with zeros.
            		reg[rd] = immU;
            		break;
            	case 0x17: // AUIPC (add upper immediate to PC). Is used to build pc-relative addresses and uses the U-type format. AUIPC forms a 32-bit offset from the 20-bit U-immediate, filling in the lowest 12 bits with zeros, adds this offset to the address of the AUIPC instruction, then places the result in register rd.
            		reg[rd] = immU + pc;
            		break;
            	case 0x6F: // JAL (jump & link). JAL saves the next address (program counter +4) to the destination register, adds the immediate value encoded in the instruction to the program counter, and jumps to that address.
            		reg[rd] = pc + 4;
            		pc += immJ;
            		branch = true;
            		break;
            	case 0x67: // JALR (jump & link register). JALR saves the next address (program counter +4) to the destination register, adds the immediate value encoded in the instruction to the source register, and jumps to that (even) address.
            		reg[rd] = pc + 4;
            		pc = reg[rs1] + immI;
            		branch = true;
            		break;
            	case 0x63: // BEQ/BNE/BLT/BGE/BLTU/BGEU
            		
            		switch (funct3) { 
	                	case 0x0: // BEQ (branch if equal). BEQ take the branch if registers rs1 and rs2 are equal.
	                		if (reg[rs1] == reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x1: // BNE (branch if not equal). BNE take the branch if registers rs1 and rs2 are unequal.
	                		if (reg[rs1] != reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x4: // BLT (branch if less than). BLT take the branch if rs1 is less than rs2, using signed comparison.
	                		if (reg[rs1] < reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x5: // BGE (branch if greater than or equal). BGE take the branch if rs1 is greater than or equal to rs2, using signed comparison.
	                		if (reg[rs1] >= reg[rs2]) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x6: // BLTU (branch if less than unsigned). BLTU take the branch if rs1 is less than rs2, using unsigned comparison.
	                		if ((reg[rs1] & 0xFFFFFFFFL) < (reg[rs2] & 0xFFFFFFFFL)) {
	                			pc += immB;
	                			branch = true;
	                		}
	                		break;
	                	case 0x7: // BGEU (branch if greater than or equal). BGEU take the branch if rs1 is greater than or equal to rs2, using unsigned comparison.
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
	                	case 0x0: // LB (load byte). LB and LBU are defined analogously for 8-bit values.
	                		reg[rd] = memory[reg[rs1] + immI];
	                		break;
	                	case 0x1: // LH (load halfword). LH loads a 16-bit value from memory, then sign-extends to 32-bits before storing in rd.
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | (memory[reg[rs1] + immI + 1] << 8);
	                		break;
	                	case 0x2: // LW (load word). The LW instruction loads a 32-bit value from memory into rd.
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | ((memory[reg[rs1] + immI + 1] & 0xff) << 8) | ((memory[reg[rs1] + immI + 2] & 0xff) << 16) | (memory[reg[rs1] + immI + 3] << 24);
	                		break;
	                	case 0x4: // LBU (load byte unsigned). LB and LBU are defined analogously for 8-bit values.
	                		reg[rd] = memory[reg[rs1] + immI] & 0xff;
	                		break;
	                	case 0x5: // LHU (load half unsigned). LHU loads a 16-bit value from memory but then zero extends to 32-bits before storing in rd.
	                		reg[rd] = (memory[reg[rs1] + immI] & 0xff) | ((memory[reg[rs1] + immI + 1] & 0xff) << 8);
	                		break;
	                	default:
	                        System.out.println("Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
	                        break;
	            	}
	            	break;
            		
            	case 0x23: // SB/SH/SW
            		
            		switch (funct3) { 
	                	case 0x0: // SB (store byte). The SB instruction store a 8-bit value from the low bits of register rs2 to memory.
	                		memory[reg[rs1] + immS] = (byte) (reg[rs2]);
	                		break;
	                	case 0x1: // SH (store halfword). The SH instructions store a 16-bit value from the low bits of register rs2 to memory.
	                		memory[reg[rs1] + immS] = (byte) (reg[rs2] & 0xff);
	                		memory[reg[rs1] + immS + 1] = (byte) ((reg[rs2] >> 8) & 0xff);
	                		break;
	                	case 0x2: // SW (store word). The SW instructions store a 32-bit value from the low bits of register rs2 to memory.
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
	                    case 0x0: // ADDI (add Immediate). ADDI adds the sign-extended 12-bit immediate to register rs1. Arithmetic overflow is ignored and the result is simply the low XLEN bits of the result. ADDI rd, rs1, 0 is used to implement the MV rd, rs1 assembler pseudoinstruction.
	                        reg[rd] = reg[rs1] + immI;
	                        break;
	                    case 0x2: // SLTI (set less than immediate). Places the value 1 in register rd if register rs1 is less than the sign-extended immediate when both are treated as signed numbers, else 0 is written to rd.
	                    	if (reg[rs1] < immI) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x3: // SLTIU (set less than immediate unsigned). SLTIU is similar but compares the values as unsigned numbers (i.e., the immediate is first sign-extended to XLEN bits then treated as an unsigned number). Note, SLTIU rd, rs1, 1 sets rd to 1 if rs1 equals zero, otherwise sets rd to 0 (assembler pseudoinstruction SEQZ rd, rs).
	                    	if (reg[rs1] < (immI & 0xFFFFFFFFL)) {
	                    		reg[rd] = 1;
	                    	} else {
	                    		reg[rd] = 0;
	                    	}
	                        break;
	                    case 0x4: // XORI (exclusive or immediate). ANDI, ORI, XORI are logical operations that perform bitwise AND, OR, and XOR on register rs1 and the sign-extended 12-bit immediate and place the result in rd. Note, XORI rd, rs1, -1 performs a bitwise logical inversion of register rs1 (assembler pseudoinstruction NOT rd, rs).
	                    	reg[rd] = reg[rs1] ^ immI;
	                        break;
	                    case 0x6: // ORI (or immediate)
                    		reg[rd] = reg[rs1] | immI;
                    		break;
	                    case 0x7: // ANDI (and immediate)
                    		reg[rd] = reg[rs1] & immI;
                    		break;
	                    case 0x1: // SLLI (shift left logical immediate).  SLLI is a logical left shift (zeros are shifted into the lower bits).
                    		reg[rd] = reg[rs1] << immI;
                    		break;
	                    case 0x5:
	                    	
	                    	switch (funct7) { 
		                    	case 0x0: // SRLI (shift right logical immediate). SRLI is a logical right shift (zeros are shifted into the upper bits).
		                    		reg[rd] = reg[rs1] >>> immI;
		                        	break;
			                    case 0x20: // SRAI (shift right arithmetic immediate). SRAI is an arithmetic right shift (the original sign bit is copied into the vacated upper bits).
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
			                	case 0x0: // ADD (addition). ADD performs the addition of rs1 and rs2. Overflows are ignored and the low XLEN bits of results are written to the destination rd.
			                		reg[rd] = reg[rs1] + reg[rs2];
			                		break;
			                	case 0x20: // SUB (subtraction). SUB performs the subtraction of rs2 from rs1. Overflows are ignored and the low XLEN bits of results are written to the destination rd.
			                		reg[rd] = reg[rs1] - reg[rs2];
			                		break;
			                	default:
			                        System.out.println("Funct7 " + funct7 + " for Funct3 " + funct3 + " for Opcode " + opcode + " not yet implemented");
			                        break;
			            	}
			            	break;
			            
	                	case 0x1: // SLL (logical left shift). SLL perform logical left shift on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2.
	                		reg[rd] = reg[rs1] << reg[rs2];
	                		break;
	                	case 0x2: // SLT (set if less than). SLT perform signed compares, writing 1 to rd if rs1 < rs2, 0 otherwise.
	                		if (reg[rs1] < reg[rs2]) {
	                			reg[rd] = 1;
	                		} else {
	                			reg[rs1] = 0;
	                		}
	                		break;
	                	case 0x3: // SLTU (set if less than unsigned). SLTU perform unsigned compares, writing 1 to rd if rs1 < rs2, 0 otherwise. Note, SLTU rd, x0, rs2 sets rd to 1 if rs2 is not equal to zero, otherwise sets rd to zero (assembler pseudoinstruction SNEZ rd, rs).
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
			                	case 0x0: // SRL (shift right logical). SRL perform logical right shifts on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2.
			                		reg[rd] = reg[rs1] >>> reg[rs2];
			                		break;
			                	case 0x20: // SRA (shift right arithmetic). SRA perform arithmetic right shifts on the value in register rs1 by the shift amount held in the lower 5 bits of register rs2.
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
	                	case 0x0: // print_int - Prints the value located in a0 as a signed integer
	                		System.out.print(reg[10]);
	                		break;
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
            
            
            
//            //print register values for debugging
//            for (int i = 0; i < reg.length; ++i) {
//                System.out.print(reg[i] + " ");
//            }
//            System.out.println();
            
          //exit program if condition is met
            if ((pc >> 2) >= memory.length) {
                break;
            }
        }
        
        
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
