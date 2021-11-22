package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import main.Main;

class Task1Test {
	
	@Test
	void addlarge() {
		Main.runProcessor(new File("./tests/task1/addlarge.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/addlarge.res")));
	}
	
	@Test
	void addneg() {
		Main.runProcessor(new File("./tests/task1/addneg.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/addneg.res")));
	}
	
	@Test
	void addpos() {
		Main.runProcessor(new File("./tests/task1/addpos.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/addpos.res")));
	}
	
	@Test
	void bool() {
		Main.runProcessor(new File("./tests/task1/bool.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/bool.res")));
	}
	
	@Test
	void set() {
		Main.runProcessor(new File("./tests/task1/set.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/set.res")));
	}
	
	@Test
	void shift() {
		Main.runProcessor(new File("./tests/task1/shift.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/shift.res")));
	}
	
	@Test
	void shift2() {
		Main.runProcessor(new File("./tests/task1/shift2.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task1/shift2.res")));
	}

}
