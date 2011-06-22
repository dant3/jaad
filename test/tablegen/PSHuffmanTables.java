package tablegen;

import java.util.Arrays;
import java.util.Comparator;

public class PSHuffmanTables implements PSHuffmanTableData {

	public static void main(String[] args) {
		printTable(HUFF_IID_DEFAULT_DF, "HUFF_IID_DEFAULT_DF");
		printTable(HUFF_IID_DEFAULT_DT, "HUFF_IID_DEFAULT_DT");
		printTable(HUFF_IID_FINE_DF, "HUFF_IID_FINE_DF");
		printTable(HUFF_IID_FINE_DT, "HUFF_IID_FINE_DT");
		printTable(HUFF_ICC_DF, "HUFF_ICC_DF");
		printTable(HUFF_ICC_DT, "HUFF_ICC_DT");
		printTable(HUFF_IPD_DF, "HUFF_IPD_DF");
		printTable(HUFF_IPD_DT, "HUFF_IPD_DT");
		printTable(HUFF_OPD_DF, "HUFF_OPD_DF");
		printTable(HUFF_OPD_DT, "HUFF_OPD_DT");
	}

	private static void printTable(int[][] table, String name) {
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

	private static class TableComparator implements Comparator<int[]> {

		public int compare(int[] i1, int[] i2) {
			if(i1[1]!=i2[1]) return i1[1]-i2[1];
			else return i1[2]-i2[2];
		}
	}
}
