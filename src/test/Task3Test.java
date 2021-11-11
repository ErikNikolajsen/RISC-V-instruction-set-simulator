package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import main.Main;

class Task3Test {


	String[] task3 = new String[] {
		"loop",
		"recursive",
		"string",
		"width"
	};
	
	@Test
	void loop() {
		Main.runProcessor(new File("./tests/task3/loop.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task3/loop.res")));
	}
	
	@Test
	void recursive() {
		Main.runProcessor(new File("./tests/task3/recursive.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task3/recursive.res")));
	}
	
	@Test
	void string() {
		Main.runProcessor(new File("./tests/task3/string.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task3/string.res")));
	}
	
	@Test
	void width() {
		Main.runProcessor(new File("./tests/task3/width.bin"));
		assertArrayEquals(Main.getReg(), Main.loadProgramFromFile(new File("./tests/task3/width.res")));
	}

}
