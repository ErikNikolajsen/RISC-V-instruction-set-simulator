package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import main.Main;

class Task2Test {
	
	@Test
	void branchcnt() {
		Main.runProcessor(new File("./tests/task2/branchcnt.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task2/branchcnt.res")));
	}
	
	@Test
	void branchmany() {
		Main.runProcessor(new File("./tests/task2/branchmany.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task2/branchmany.res")));
	}
	
	@Test
	void branchtrap() {
		Main.runProcessor(new File("./tests/task2/branchtrap.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task2/branchtrap.res")));
	}

}
