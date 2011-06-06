package tablegen;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SBRHuffmanTables implements SBRHuffmanTableData {

	private static final Comparator<int[]> COMP = new Comparator<int[]>() {

		public int compare(int[] i1, int[] i2) {
			return i1[0]-i2[0];
		}
	};

	public static void main(String[] args) {
		printTable(T_HUFFMAN_ENV_1_5, "T_HUFFMAN_ENV_1_5");
		printTable(F_HUFFMAN_ENV_1_5, "F_HUFFMAN_ENV_1_5");
		printTable(T_HUFFMAN_ENV_BAL_1_5, "T_HUFFMAN_ENV_BAL_1_5");
		printTable(F_HUFFMAN_ENV_BAL_1_5, "F_HUFFMAN_ENV_BAL_1_5");
		printTable(T_HUFFMAN_ENV_3_0, "T_HUFFMAN_ENV_3_0");
		printTable(F_HUFFMAN_ENV_3_0, "F_HUFFMAN_ENV_3_0");
		printTable(T_HUFFMAN_ENV_BAL_3_0, "T_HUFFMAN_ENV_BAL_3_0");
		printTable(F_HUFFMAN_ENV_BAL_3_0, "F_HUFFMAN_ENV_BAL_3_0");
		printTable(T_HUFFMAN_NOISE_3_0, "T_HUFFMAN_NOISE_3_0");
		printTable(T_HUFFMAN_NOISE_BAL_3_0, "T_HUFFMAN_NOISE_BAL_3_0");
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
