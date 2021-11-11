package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.Main;

class Task1Test {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

//	for (int i : Main.getReg()) {
//		System.out.print(i+" ");
//	}
//	System.out.println();
//	for (int i : Main.loadProgramFromFile(new File("./tests/task1/addlarge.res"))) {
//		System.out.print(i+" ");
//	}
	
//	String[] task1 = new String[] {
//			"addlarge",
//			"addneg",
//			"addpos",
//			"bool",
//			"set",
//			"shift",
//			"shift2"
//	};
	
	@Test
	void addlarge() {
		Main.runProcessor(new File("./tests/task1/addlarge.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/addlarge.res")));
	}
	
	@Test
	void addneg() {
		Main.runProcessor(new File("./tests/task1/addneg.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/addneg.res")));
	}
	
	@Test
	void addpos() {
		Main.runProcessor(new File("./tests/task1/addpos.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/addpos.res")));
	}
	
	@Test
	void bool() {
		Main.runProcessor(new File("./tests/task1/bool.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/bool.res")));
	}
	
	@Test
	void set() {
		Main.runProcessor(new File("./tests/task1/set.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/set.res")));
	}
	
	@Test
	void shift() {
		Main.runProcessor(new File("./tests/task1/shift.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/shift.res")));
	}
	
	@Test
	void shift2() {
		Main.runProcessor(new File("./tests/task1/shift2.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task1/shift2.res")));
	}

}
