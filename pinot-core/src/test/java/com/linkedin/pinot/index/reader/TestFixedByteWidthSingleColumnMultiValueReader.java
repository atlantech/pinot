package com.linkedin.pinot.index.reader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.linkedin.pinot.core.index.reader.impl.FixedByteWidthRowColDataFileReader;
import com.linkedin.pinot.core.index.reader.impl.FixedByteWidthSingleColumnMultiValueReader;

public class TestFixedByteWidthSingleColumnMultiValueReader {

	@Test
	public void testSingleColMultiValue() throws Exception {
		String fileName = "test_single_col.dat";
		File f = new File(fileName);
		f.delete();
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
		int[][] data = new int[100][];
		Random r = new Random();
		for (int i = 0; i < data.length; i++) {
			int numValues = r.nextInt(100) + 1;
			data[i] = new int[numValues];
			for (int j = 0; j < numValues; j++) {
				data[i][j] = r.nextInt();
			}
		}
		int cumValues = 0;
		for (int i = 0; i < data.length; i++) {
			dos.writeInt(cumValues);
			dos.writeInt(data[i].length);
			cumValues += data[i].length;
		}
		for (int i = 0; i < data.length; i++) {
			int numValues = data[i].length;
			for (int j = 0; j < numValues; j++) {
				dos.writeInt(data[i][j]);
			}
		}
		dos.flush();
		dos.close();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		System.out.println("file size: " + raf.getChannel().size());
		FixedByteWidthSingleColumnMultiValueReader reader;
		reader = new FixedByteWidthSingleColumnMultiValueReader(f, data.length,
				4, true);
		reader.open();
		int[] readValues = new int[100];
		for (int i = 0; i < data.length; i++) {
			int numValues = reader.getIntArray(i, readValues);
			Assert.assertEquals(numValues, data[i].length);
			for(int j=0;j<numValues;j++){
				Assert.assertEquals(readValues[j], data[i][j]);
			}
		}
		reader.close();
		raf.close();
		f.delete();
	}
}
