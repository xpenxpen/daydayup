package org.xpen.hello.dl.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.VerticalBarPlot;

/**
 * 演示tablesaw
 */
public class TablesawTest {
	
    /**
     * tablesaw转为图表
     */
    @Test
    public void testPlot() throws Exception {
    	String[] animals = {"bear", "cat", "giraffe"};
    	double[] cuteness = {90.1, 84.3, 99.7};

    	Table cuteAnimals = Table.create("Cute Animals")
    	    .addColumns(
    	        StringColumn.create("Animal types", animals),
    	        DoubleColumn.create("rating", cuteness)
    	    );
    	Plot.show(
    		VerticalBarPlot.create(
    			"Cute animals",
    			cuteAnimals, "Animal types", "rating"));
    }
	
    /**
     * tablesaw转为NDArray
     */
    @Test
	@SuppressWarnings("unchecked")
    public void testTablesaw2NdArray() throws Exception {
    	createCsv();
    	Table data = Table.read().file("target/data/house_tiny.csv");
    	Table inputs = Table.create(data.columns());
    	inputs.removeColumns("Price");
    	Table outputs = data.selectColumns("Price");

		Column<Integer> col = (Column<Integer>)inputs.column("NumRooms");
    	col.set(col.isMissing(), (int) inputs.nCol("NumRooms").mean());
    	
    	StringColumn col2 = (StringColumn) inputs.column("Alley");
    	List<BooleanColumn> dummies = col2.getDummies();
    	inputs.removeColumns(col2);
    	inputs.addColumns(DoubleColumn.create("Alley_Pave", dummies.get(0).asDoubleArray()), 
    	    DoubleColumn.create("Alley_nan", dummies.get(1).asDoubleArray())
    	);
    	                  
    	System.out.println(inputs);
    	System.out.println(outputs);
    	
    	NDManager nd = NDManager.newBaseManager();
    	NDArray x = nd.create(inputs.as().doubleMatrix());
    	System.out.println(x);
    }

	private void createCsv() throws IOException {
		File file = new File("target/data/");
    	file.mkdir();

    	String dataFile = "target/data/house_tiny.csv";

    	// Create file
    	File f = new File(dataFile);
    	f.createNewFile();

    	// Write to file
    	try (FileWriter writer = new FileWriter(dataFile)) {
    	    writer.write("NumRooms,Alley,Price\n");
    	    writer.write("NA,Pave,127500\n");
    	    writer.write("2,NA,106000\n");
    	    writer.write("4,NA,178100\n");
    	    writer.write("NA,NA,140000\n");
    	}
	}

}
