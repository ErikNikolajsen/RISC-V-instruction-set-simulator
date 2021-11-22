package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import main.Main;

class Task3Test {

	@Test
	void loop() {
		Main.runProcessor(new File("./tests/task3/loop.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task3/loop.res")));
	}
	
	@Test
	void recursive() {
		Main.runProcessor(new File("./tests/task3/recursive.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task3/recursive.res")));
	}
	
	@Test
	void string() {
		Main.runProcessor(new File("./tests/task3/string.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task3/string.res")));
	}
	
	@Test
	void width() {
		Main.runProcessor(new File("./tests/task3/width.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task3/width.res")));
	}

}
