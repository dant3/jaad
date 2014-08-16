package tablegen;

class Utils {

	//1D float
	public static void printTable(float[] f, String name) {
		final StringBuilder sb = new StringBuilder();
		sb.append("float[] ");
		sb.append(name);
		sb.append(" = {");
		for(int i = 0; i<f.length; i++) {
			sb.append(f[i]);
			sb.append('f');
			if(i<f.length-1) sb.append(", ");
			if(i>0&&(i%5)==0) sb.append("\n");
		}
		sb.append("};");
		System.out.println(sb.toString());
	}

	//2D float
	public static void printTable(float[][] f, String name) {
		System.out.println("float[][] "+name+" = {");
		StringBuilder sb;
		for(int i = 0; i<f.length; i++) {
			sb = new StringBuilder();
			sb.append("{");
			for(int j = 0; j<f[i].length; j++) {
				if(j>0) sb.append(", ");
				sb.append(f[i][j]).append("f");
			}
			sb.append("}");
			if(i<f.length-1) sb.append(",");
			System.out.println(sb.toString());
		}
		System.out.println("};");
	}

	//3D float
	public static void printTable(float[][][] f, String name) {
		System.out.println("float[][][] "+name+" = {");
		StringBuilder sb;
		for(int i = 0; i<f.length; i++) {
			sb = new StringBuilder();
			sb.append("{");
			for(int j = 0; j<f[i].length; j++) {
				sb.append("{");
				for(int k = 0; k<f[i][j].length; k++) {
					if(k>0) sb.append(", ");
					sb.append(f[i][j][k]).append("f");
				}
				sb.append("}");
				if(j<f[i].length-1) sb.append(",\n");
			}
			sb.append("}");
			if(i<f.length-1) sb.append(",\n");
			System.out.println(sb.toString());
		}
		System.out.println("};");
	}

	//4D float
	public static void printTable(float[][][][] f, String name) {
		System.out.println("float[][][][] "+name+" = {");
		StringBuilder sb;
		for(int i = 0; i<f.length; i++) {
			sb = new StringBuilder();
			sb.append("{");
			for(int j = 0; j<f[i].length; j++) {
				sb.append("{");
				for(int k = 0; k<f[i][j].length; k++) {
					sb.append("{");
					for(int l = 0; l<f[i][j][k].length; l++) {
						if(l>0) sb.append(", ");
						sb.append(f[i][j][k][l]).append("f");
					}
					sb.append("}");
					if(k<f[i][j].length-1) sb.append(",\n");
				}
				sb.append("}");
				if(j<f[i].length-1) sb.append(",\n");
			}
			sb.append("}");
			if(i<f.length-1) sb.append(",\n");
			System.out.println(sb.toString());
		}
		System.out.println("};");
	}

	//2D int
	public static void printTable(int[][] table, String name) {
		System.out.println("int[][] "+name+" = {");
		StringBuilder sb;
		for(int i = 0; i<table.length; i++) {
			sb = new StringBuilder();
			sb.append("{");
			for(int j = 0; j<table[i].length; j++) {
				if(j>0) sb.append(", ");
				sb.append(table[i][j]);
			}
			sb.append("}");
			if(i<table.length-1) sb.append(",");
			System.out.println(sb.toString());
		}
		System.out.println("};");
	}
	//1D int

	public static void printTable(int[] table, String name) {
		final StringBuilder sb = new StringBuilder();
		sb.append("int[] ");
		sb.append(name);
		sb.append(" = {");
		for(int i = 0; i<table.length; i++) {
			sb.append(table[i]);
			if(i<table.length-1) sb.append(", ");
		}
		sb.append("};");
		System.out.println(sb.toString());
	}
}
