
package mx.unam.ciencias.edd.proyecto3;

import mx.unam.ciencias.edd.*;
import java.io.*;

public class Main {

	private static String[] a;

	private static boolean argsContains(String s) {
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals(s))
				return true;
		}

		return false;
	}

	private static int getIndexArgs(String s) {
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals(s))
				return i;
		}

		return -1;
	}

	public static void main(String [] args) {
		a = args;

		if (args.length == 0) {
			BufferedInputStream bis = new BufferedInputStream(System.in);
			Maze m = new Maze(bis);

			System.out.println(m.toSVG());
		}

		else {
			if (argsContains("-g")) {
				if (!argsContains("-h") || !argsContains("-w")) {
					System.out.println("No encontre la altura o el ancho del laberinto, intente de nuevo");
					System.exit(-1);
				}

				try {
					int filas = Integer.parseInt(a[getIndexArgs("-h") + 1]);
					int columnas = Integer.parseInt(a[getIndexArgs("-w") + 1]);

					if (filas < 2 || filas > 255 || columnas < 2 || columnas > 255) throw new NumberFormatException();

					Maze m = new Maze(filas, columnas);
					m.writeMaze();

				} catch (NumberFormatException nfe) {
					System.out.println("La altura o la anchura son invalidas, intente de nuevo");
				}

			} else {
				System.out.println("No encontre ninguna indicación. Para generar un laberinto nuevo utilize -g y para asignar altura y anchura use -h y -w");
				System.exit(-1);
			}
		}
	}
}