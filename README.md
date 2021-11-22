# RISC-V-instruction-set-simulator 
The program is a simulation model that mimics the behavior of a RISC-V CPU by reading instructions and maintaining internal variables which represent the CPU's program-counter, registers, and memory.
An input file containing an executeable program in binary format is feed to the program which results in an output file that contains the resulting values of the CPU's registers in binary format.

The simulator implements the RISC-V integer instruction set ([RV32I](https://riscv.org/technical/specifications/)), with exception of the instructions: `fence`, `fence.i`, `ebreak`, `csrrw`, `csrrs`, `csrrc`, `csrrwi`, `csrrsi`, `csrrci`.

The ecall instruction only supports the exit function `ecall 10` (see [Ripes env. call](https://github.com/mortbopet/Ripes/wiki/Environment-calls)).

## Requirements
* JDK 11

## Setup
Compile the .jar file:
```

```


Run the program:
```
java -jar riscSimulator.jar <inputFile> <outputFile>
```