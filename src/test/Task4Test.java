package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import main.Main;

class Task4Test {

	@Test
	void t1() {
		Main.runProcessor(new File("./tests/task4/t1.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t1.res")));
	}
	
	@Test
	void t2() {
		Main.runProcessor(new File("./tests/task4/t2.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t2.res")));
	}
	
	@Test
	void t3() {
		Main.runProcessor(new File("./tests/task4/t3.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t3.res")));
	}
	
	@Test
	void t4() {
		Main.runProcessor(new File("./tests/task4/t4.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t4.res")));
	}
	
	@Test
	void t5() {
		Main.runProcessor(new File("./tests/task4/t5.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t5.res")));
	}
	
	@Test
	void t6() {
		Main.runProcessor(new File("./tests/task4/t6.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t6.res")));
	}
	
	@Test
	void t7() {
		Main.runProcessor(new File("./tests/task4/t7.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t7.res")));
	}
	
	@Test
	void t8() {
		Main.runProcessor(new File("./tests/task4/t8.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t8.res")));
	}
	
	@Test
	void t9() {
		Main.runProcessor(new File("./tests/task4/t9.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t9.res")));
	}
	
	@Test
	void t10() {
		Main.runProcessor(new File("./tests/task4/t10.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t10.res")));
	}
	
	@Test
	void t11() {
		Main.runProcessor(new File("./tests/task4/t11.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t11.res")));
	}
	
	@Test
	void t12() {
		Main.runProcessor(new File("./tests/task4/t12.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t12.res")));
	}
	
	@Test
	void t13() {
		Main.runProcessor(new File("./tests/task4/t13.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t13.res")));
	}
	
	@Test
	void t14() {
		Main.runProcessor(new File("./tests/task4/t14.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t14.res")));
	}
	
	@Test
	void t15() {
		Main.runProcessor(new File("./tests/task4/t15.bin"), null);
		assertArrayEquals(Main.getReg(), Main.loadTestRegisterFromFile(new File("./tests/task4/t15.res")));
	}

}
