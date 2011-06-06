package tablegen;

import java.util.Arrays;
import java.util.Comparator;

//converts raw huffman tables into usable format
public class HuffmanTables implements HuffmanTableData {

	private static final boolean[] UNSIGNED = {false, false, true, true, false, false, true, true, true, true, true};
	private static final int[] LAV = {1, 1, 2, 2, 4, 4, 7, 7, 12, 12, 16};

	public static void main(String[] args) {
		printTable(HCB1, 1, "HCB1");
		printTable(HCB2, 2, "HCB2");
		printTable(HCB3, 3, "HCB3");
		printTable(HCB4, 4, "HCB4");
		printTable(HCB5, 5, "HCB5");
		printTable(HCB6, 6, "HCB6");
		printTable(HCB7, 7, "HCB7");
		printTable(HCB8, 8, "HCB8");
		printTable(HCB9, 9, "HCB9");
		printTable(HCB10, 10, "HCB10");
		printTable(HCB11, 11, "HCB11");
		printSFTable(HCB_SF, "HCB_SF");
	}

	private static void printTable(int[][] table, int cb, String name) {
		Arrays.sort(table, new TableComparator());

		System.out.println("int[][] "+name+" = {");
		int[] tmp;
		StringBuilder sb;
		for(int i = 0; i<table.length; i++) {
			sb = new StringBuilder();

			//length, codeword
			sb.append("{"+table[i][1]+", "+table[i][2]);

			//values
			tmp = unpack(table[i][0], cb);
			sb.append(", "+tmp[0]+", "+tmp[1]);
			if(cb<5) sb.append(", "+tmp[2]+", "+tmp[3]);

			sb.append("}");
			if(i<table.length-1) sb.append(",");

			System.out.println(sb.toString());
		}
		System.out.println("};");
	}

	private static void printSFTable(int[][] table, String name) {
		Arrays.sort(table, new TableComparator());

		System.out.println("int[][] "+name+" = {");
		StringBuilder sb;
		for(int i = 0; i<table.length; i++) {
			sb = new StringBuilder();

			//length, codeword, index
			sb.append("{"+table[i][1]+", "+table[i][2]+", "+table[i][0]+"}");
			if(i<table.length-1) sb.append(",");

			System.out.println(sb.toString());
		}
		System.out.println("};");
	}

	private static int[] unpack(int index, int cb) {
		double idx = (double) index;
		final double mod;
		final int off;
		if(UNSIGNED[cb-1]) {
			mod = LAV[cb-1]+1;
			off = 0;
		}
		else {
			mod = 2*LAV[cb-1]+1;
			off = LAV[cb-1];
		}

		final int[] x;
		if(cb<5) {
			x = new int[4];
			x[0] = (int) (idx/(mod*mod*mod))-off;
			idx -= (x[0]+off)*(mod*mod*mod);
			x[1] = (int) (idx/(mod*mod))-off;
			idx -= (x[1]+off)*(mod*mod);
			x[2] = (int) (idx/mod)-off;
			idx -= (x[2]+off)*mod;
			x[3] = (int) idx-off;
		}
		else {
			x = new int[2];
			x[0] = (int) (idx/mod)-off;
			idx -= (x[0]+off)*mod;
			x[1] = (int) idx-off;
		}
		return x;
	}

	private static class TableComparator implements Comparator<int[]> {

		public int compare(int[] i1, int[] i2) {
			if(i1[1]!=i2[1]) return i1[1]-i2[1];
			else return i1[2]-i2[2];
		}
	}
}
