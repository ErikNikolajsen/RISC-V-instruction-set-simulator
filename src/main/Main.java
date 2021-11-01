package main;

public class Main {

    static int pc;
    static int[] reg = new int[32];

    // Here the first program hard coded as an array
    static int[] progr = {
            0x00200093, // addi x1 x0 2 (1000000000000010010011)
            0x00300113, // addi x2 x0 3
            0x002081b3, // add x3 x1 x2
    };

    public static void main(String[] args) {

        System.out.println("Hello RISC-V World!");

        pc = 0;

        
        
        for (; ; ) {

            int instr = progr[pc >> 2]; //divide by 4
            int opcode = instr & 0x7f;
            int rd = (instr >> 7) & 0x01f;
            int rs1 = (instr >> 15) & 0x01f;
            int rs2 = (instr >> 20) & 0x01f;
            int iImm = (instr >> 20);
            int uImm = (instr >> 12);
            int jImm1 = (instr >> 31) & 0x1;
            int jImm2 = (instr >> 21) & 0x3FF;
            int jImm3 = (instr >> 20) & 0x1;
            int jImm4 = (instr >> 12) & 0xFF;

            switch (opcode) {
            	case 0x37: // LUI - Load upper immediate - 0110111 - 55 - U-type
            		reg[rd] = uImm << 12;
            		break;
            	case 0x17: // AUIPC - Add Upper Imm to PC - 0010111 - 23 - U-type //this sets rd to the sum of the current PC and a 32-bit value with the low 12 bits as 0 and the high 20 bits coming from the U-type immediate.
            		reg[rd] = (uImm << 12) + pc;
            		break;
            	case 0x6F: // JAL - Jump & Link - 1101111 - 111 - J-type
            		reg[rd] = pc+4;
            		pc += ((((((jImm1 << 8) | jImm4) << 1) | jImm3) << 10) | jImm2) * 4;
            	case 0x67: // JALR - Jump & Link Register - 1100111 - 103 - I-type
            		reg[rd] = pc+4;
            		pc = (iImm + rs1) & 0xFFFFFFFE;  
                case 0x13: //ADDI
                    reg[rd] = reg[rs1] + iImm;
                    break;
                case 0x33: //ADD (51)
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

    }

}
