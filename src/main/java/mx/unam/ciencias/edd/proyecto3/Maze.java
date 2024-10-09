package mx.unam.ciencias.edd.proyecto3;

import java.util.Random;
import java.io.*;
import mx.unam.ciencias.edd.*;

public class Maze {

    /*
     * Clase privada para las casillas del laberinto.
     */
    private class Box {
        private class Coord {
            private int fila, columna;

            public Coord(int x, int y) {
                fila = x;
                columna = y;
            }
        }

        private boolean n, s, e, o, seen; //Puertas y seen para saber si ya se visito
        private String bbt = ""; //Auxiliar para ver si se encuentra en alguna frontera y en cual
        private Coord posicion;
        private Box prev;
        private BoxTypes tipo;
        private int score; // Variable para mantener el puntaje de pasar por esta casilla
        
        /*
         * Se crea una nueva casilla en la posicion (a, b) y se le asigna un peso aleatorio
         */
        public Box(int a, int b) {
            posicion = new Coord(a, b);
            n = true;
            s = true;
            o = true;
            e = true;
            
            score = (byte) r.nextInt(16);

            setType();
        }
        
        /*
         * Crea una casilla con base al byte b y le asigna la posicion (fila, columna). Este metodo no esta de la forma simila al de
         * toByte() pq no quizo funcionar con & en el byte. 
         */
        public Box(byte b, int fila, int columna) {
            int doors = b & 0x0F;
            score = (b & 0xF0) >> 4;

            switch (doors) {
                case 0:
                    n = s = e = o = false;
                    break;

                case 1:
                    n = s = o = false;
                    e = true;
                    break;

                case 2:
                    s = e = o = false;
                    n = true;
                    break;

                case 3:
                    s = o = false;
                    e = n = true;
                    break;

                case 4:
                    n = s = e = false;
                    o = true;
                    break;

                case 5:
                    e = o = true;
                    s = n = false;
                    break;

                case 6:
                    o = n = true;
                    s = e = false;
                    break;

                case 7:
                    n = o = e = true;
                    s = false;
                    break;
                
                case 8:
                    s = true;
                    o = n = e = false;
                    break;

                case 9:
                    s = e = true;
                    n = o = false;
                    break;

                case 10: 
                    s = n = true;
                    o = e = false;
                    break;

                case 11:
                    s = e = n = true;
                    o = false;
                    break;

                case 12:
                    s = o = true;
                    n = e = false;
                    break;

                case 13:
                    s = o = e = true;
                    n = false;
                    break;

                case 14:
                    s = n = o = true;
                    e = false;
                    break;
            }

            if (n && s && e && o) {
                System.out.println("Archivo invalido");
                System.exit(-1);
            }
            
            posicion = new Coord(fila, columna);
            setType();
        }

        //Metodo auxiliar que determina cual tipo de casilla es esta, y le asigna un tipo
        private void setType() {
            if (posicion.fila == 0) {
                if (posicion.columna == 0)
                    tipo = BoxTypes.ESQ_NO;

                else if (posicion.columna == maze[0].length-1) 
                    tipo = BoxTypes.ESQ_NE;

                else 
                    tipo = BoxTypes.N_BORD;
            }

            else if (posicion.fila == maze.length-1) {
                if (posicion.columna == 0)
                    tipo = BoxTypes.ESQ_SO;

                else if (posicion.columna == maze[0].length-1)
                    tipo = BoxTypes.ESQ_SE;

                else
                    tipo = BoxTypes.S_BORD;
            }

            else if (posicion.columna == 0) 
                tipo = BoxTypes.O_BORD;

            else if (posicion.columna == maze[0].length - 1)
                tipo = BoxTypes.E_BORD;

            else
                tipo = BoxTypes.FREE_BOX;

        }

        /* Metodos toString auxliares, el toString() no se ocupa porque primero se tienen que poner todas las partes
         * de arriba de la casilla y despues las de abajo, sino queda mal el String
         */
        @Override
        public String toString() {
            return topString() + "\n" + bottomString();
        }
        
        public boolean equals(Box b) {
            return b.posicion.columna == posicion.columna && b.posicion.fila == posicion.fila;
        }

        private String topString() {
            StringBuilder sb = new StringBuilder();

            if (o && !bbt.equals("o")) { //------------------------------------------------------
                sb.append("|");
            } else {
                sb.append(" ");
            }

            if (n && !bbt.equals("n")) {
                sb.append("¯¯");
            } else {
                sb.append("  ");
            }

            if (e && !bbt.equals("e")) { //---------------------------------------------------------
                sb.append("|");
            } else {
                sb.append(" ");
            }

            return sb.toString();
        }

        private String bottomString() {
            StringBuilder sb = new StringBuilder();

            if (o && !bbt.equals("o")) { //--------------------------------------------------------------
                sb.append("|");
            } else {
                sb.append(" ");
            }

            if (s && !bbt.equals("s")) {
                sb.append("__");
            } else {
                sb.append("  ");
            }

            if (e && !bbt.equals("e")) { //-------------------------------------------------------------------------
                sb.append("|");
            } else {
                sb.append(" ");
            }

            return sb.toString();
        }
        
        //Metodo para cuando se va recontruyendo la salida del laberinto, dibuja una linea entre esta casilla y la de la derecha
        private String conectRight() {
            StringBuilder sb = new StringBuilder();
            sb.append("<line x1=\"" + (50 + posicion.columna*20 + 10) + "\" y1=\"" + (50 + posicion.fila*20 + 10) + "\" x2=\"" + (50 + posicion.columna*20 + 10 + 20) + "\" y2=\"" + (50 + posicion.fila*20 + 10) + "\" stroke= \"red\" stroke-with=\"3\"/>\n");
            return sb.toString();
        }
        
        //Metodo para cuando se va recontruyendo la salida del laberinto, dibuja una linea entre esta casilla y la de abajo
        private String conectSouth() {
            StringBuilder sb = new StringBuilder();
            sb.append("<line x1=\"" + (50 + posicion.columna*20 + 10) + "\" y1=\"" + (50 + posicion.fila*20 + 10) + "\" x2=\"" + (50 + posicion.columna*20 + 10) + "\" y2=\"" + (50 + posicion.fila*20 + 10 + 20) + "\" stroke= \"red\" stroke-with=\"3\"/>\n");
            return sb.toString();
        }
        
        /*
         * Escribe las casillas en forma de SVG. Como no queremos poner dobles lineas, se ponen solo las paredes en la derecha 
         * y este (a menos que sea casilla de frontera, ahi se pueden poner todas)
         */
        private String toSVG() {
            StringBuilder sb = new StringBuilder();
            if (n && posicion.fila == 0) { //Frontera Norte
                sb.append("<line x1=\"" + (50 + posicion.columna*20) + "\" y1=\"" + (50 + posicion.fila*20) + "\" x2=\"" + (50 + posicion.columna*20 + 20) + "\" y2=\"" + (50 + posicion.fila*20) + "\" stroke= \"black\" stroke-with=\"3\"/>\n");
            }
            
            if (s) { //Frontera Sur
                sb.append("<line x1=\"" + (50 + posicion.columna*20) + "\" y1=\"" + (50 + posicion.fila*20 + 20) + "\" x2=\"" + (50 + posicion.columna*20 + 20) + "\" y2=\"" + (50 + posicion.fila*20 + 20) + "\" stroke= \"black\" stroke-with=\"3\"/>\n");
            }
            
            if (o && posicion.columna == 0) { //Frontera Oeste
                sb.append("<line x1=\"" + (50 + posicion.columna*20) + "\" y1=\"" + (50 + posicion.fila*20) + "\" x2=\"" + (50 + posicion.columna*20) + "\" y2=\"" + (50 + posicion.fila*20 + 20) + "\" stroke= \"black\" stroke-with=\"3\"/>\n");
            }
            
            if (e) { //Frontera Este
                sb.append("<line x1=\"" + (50 + posicion.columna*20 + 20) + "\" y1=\"" + (50 + posicion.fila*20) + "\" x2=\"" + (50 + posicion.columna*20 + 20) + "\" y2=\"" + (50 + posicion.fila*20 + 20) + "\" stroke= \"black\" stroke-with=\"3\"/>\n");
            }
            
            return sb.toString();
        }
        
        //Se escribe la casilla como byte
        private byte toByte() {
            byte res = 0;
            if (n) res |= 0x02;
            
            if (s) res |= 0x08;
            
            if (e) res |= 0x01;
            
            if (o) res |= 0x04;
            
            res |= score << 4;
            
            return (byte) (res & 0xFF);
        }

    }

    private Box [][] maze; //Matriz que representa al Laberinto
    private Box begining, end; //Las casillas donde se empieza y termina respectivamente
    private Random r = new Random();
    private Grafica<Box> mazeG;
    private String solucion = "";

    /*
     * Constructor cuando se quiere generar un laberinto. Se usa la altura y anchura especificadas en los argumentos del programa
     */
    public Maze(int h, int w) { //h, w la altura y anchura respectivamente
        if (h <= 1 || w <= 1) {
            System.out.println("Archivo invalido");
            System.exit(-1);
        }
    
        maze = new Box[h][w];

        fillMaze(h, w);

        begining = maze[r.nextInt(maze.length)][0];
        begining.o = false;
        end = maze[r.nextInt(maze.length)][maze[0].length-1];
        end.e = false;

        lookForEntrances();

        buildMaze();
    }
    
    /*
     * Constructor de un laberinto, se llama cuando se quiere dar la solucion a un archivo .mze.
     * 
     * El BufferedInputStream isr se obtiene en la clase Main. En caso de que los 4 primeros caracteres no sean MAZE, la altura
     * o la anchura del laberinto sean menores que 2 se escribira que el archivo no es valido.
     * 
     * Tenemos que asumir que los primeros 4 caracteres serán MAZE y los siguientes 2 la altura y anchura respectivamente.
     * Por eso se usan numeros específicos en los casos
     */
    public Maze(BufferedInputStream isr) {
        int he = 0, wi = 0, al = 0, an = 0;
        try {
            int i = 0, divisiones = 0;
            while ((i = isr.read()) != -1) {
                if (divisiones > 5) {
                    maze[al][an] = new Box((byte) (i & 0xFF), al, an);
                    if (an == maze[0].length - 1) {
                        an = 0;
                        al++;
                    } else {
                        an++;
                    }
                }
                
                else if ((divisiones == 0 && i != 0x4D) || (divisiones == 1 && i != 0x41) || (divisiones == 2 && i != 0x5A) || (divisiones == 3 && i != 0x45)) {
                    System.out.println("Archivo invalidoo");
                    System.exit(-1);
                    
                }
                
                else if (divisiones == 4) {
                    if (i < 1) {
                        System.out.println("Archivo invalido");
                        System.exit(-1);
                    }
                    
                    he = i;
                }
                
                else if (divisiones == 5) {
                    if (i < 1) {
                        System.out.println("Archivo invalido");
                        System.exit(-1);
                    }
                
                    wi = i;
                    maze = new Box[he][wi];
                }
                
                divisiones++;
                
            }
            
            isr.close();
            
        } catch (Exception ioe) {}
        
        lookForEntrances();
        plot();
        solve();
    }
    
    /*
     * Este metodo construye un laberinto sobre la matriz.
     * 
     * Se va haciendo dfs sobre la matriz y se van agregando los posibles vecinos de la casilla actual a una lista. De esta se saca
     * un vecino al azar y así se van abriendo las puertas.
     */
    private void buildMaze() {
        Pila<Box> pila = new Pila<Box>();
        pila.mete(begining);

        while (!pila.esVacia()) {
            Box temp = pila.mira();
            temp.seen = true;

            Lista<Box> prob = new Lista<Box>();
            switch (temp.tipo) {
                case ESQ_NE:
                    if (!maze[0][maze[0].length-2].seen) prob.agrega(maze[0][maze[0].length-2]);
                    if (!maze[1][maze[0].length-1].seen) prob.agrega(maze[1][maze[0].length-1]);
                    break;

                case ESQ_NO:
                    if (!maze[0][1].seen) prob.agrega(maze[0][1]);
                    if (!maze[1][0].seen) prob.agrega(maze[1][0]);
                    break;

                case ESQ_SE:
                    if (!maze[maze.length-2][maze[0].length-1].seen) prob.agrega(maze[maze.length-2][maze[0].length-1]);
                    if (!maze[maze.length-1][maze[0].length-2].seen) prob.agrega(maze[maze.length-1][maze[0].length-2]);
                    break;

                case ESQ_SO:
                    if (!maze[maze.length-2][0].seen) prob.agrega(maze[maze.length-2][0]);
                    if (!maze[maze.length-1][1].seen) prob.agrega(maze[maze.length-1][1]);
                    break;

                case S_BORD:
                    if (!maze[temp.posicion.fila][temp.posicion.columna - 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna-1]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna + 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna + 1]);
                    if (!maze[temp.posicion.fila - 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila - 1][temp.posicion.columna]);
                    break;

                case N_BORD:
                    if (!maze[temp.posicion.fila][temp.posicion.columna - 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna-1]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna + 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna + 1]);
                    if (!maze[temp.posicion.fila + 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila + 1][temp.posicion.columna]);
                    break;

                case O_BORD:
                    if (!maze[temp.posicion.fila - 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila - 1][temp.posicion.columna]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna + 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna + 1]);
                    if (!maze[temp.posicion.fila + 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila + 1][temp.posicion.columna]);
                    break;

                case E_BORD:
                    if (!maze[temp.posicion.fila - 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila - 1][temp.posicion.columna]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna - 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna - 1]);
                    if (!maze[temp.posicion.fila + 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila + 1][temp.posicion.columna]);
                    break;

                case FREE_BOX:
                    if (!maze[temp.posicion.fila - 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila - 1][temp.posicion.columna]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna + 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna + 1]);
                    if (!maze[temp.posicion.fila][temp.posicion.columna - 1].seen) prob.agrega(maze[temp.posicion.fila][temp.posicion.columna - 1]);
                    if (!maze[temp.posicion.fila + 1][temp.posicion.columna].seen) prob.agrega(maze[temp.posicion.fila + 1][temp.posicion.columna]);
                    break;
            }

            if (!prob.esVacia()) {
                if (prob.getLongitud() == 1) {
                    openPath(temp, prob.getPrimero());
                    pila.mete(prob.getPrimero());
                } else {
                    int rand = r.nextInt(prob.getLongitud());
                    Box sig = prob.get(rand);
                    openPath(temp, sig);
                    pila.mete(sig);
                }
            }

            else {
                pila.saca();
            }
        }
    }

    //Metodo para abrir puertas en el laberinto
    private void openPath(Box a, Box b) {
        if (a.posicion.fila == b.posicion.fila) {
            if (a.posicion.columna == (b.posicion.columna-1)){
                a.e = b.o = false;
            }

            else {
                a.o = b.e = false;
            }
        }

        else {
            if (a.posicion.fila == (b.posicion.fila - 1)) {
                a.s = b.n = false;
            }

            else {
                a.n = b.s = false;
            }
        }
    }
    
    /*
     * Metodo que ayuda a encontrar las salidas del laberinto
     * 
     * Recorremos las cuatro fronteras buscando salidas, y cuando se encuentra una se le atribuye una etiqueta dependiendo
     * donde se encontro, ya sea "n", "s", "e", "o" y se agrega la casilla a una lista. En caso de que la lista tenga 1 o
     * mas de 2 elementos, o que estos dos elementos sean iguales, mandaremos que es archivo es invalido y saldremos. Sino, establecemos el inicio como la primer casilla
     * encontrada y el final como la ultima (que tambien es la segunda encontrada y la unica restante)
     */
    private void lookForEntrances() {
        Lista<Box> entrances = new Lista<Box>();
        
        for (int i = 0; i < maze[0].length; i++) {
            if (maze[0][i].n == false) {
                entrances.agrega(maze[0][i]);
                maze[0][i].bbt = "n";
            }
            
            if (maze[maze.length-1][i].s == false) {
                entrances.agrega(maze[maze[0].length-1][i]);
                maze[maze.length-1][i].bbt = "s";
            }
        }
        
        for (int i = 0; i < maze.length; i++) {
            if (maze[i][0].o == false) {
                entrances.agrega(maze[i][0]);
                maze[i][0].bbt = "o";
            }
            
            if (maze[i][maze[0].length-1].e == false) {
                entrances.agrega(maze[i][maze[0].length-1]);
                maze[i][maze[0].length-1].bbt = "e";
            }
        }
        
        if (entrances.getElementos() != 2) {
            System.out.println(toString());
            System.out.println("Archivo invalido");
            System.out.println(entrances.getLongitud());
            System.exit(-1);
        }
        
        begining = entrances.getPrimero();
        end = entrances.getUltimo();
        
        if (begining.equals(end)) {
            System.out.println("Archivo invalido");
            System.exit(-1);
        }
    }
    
    /*
     * Metodo que grafica la matriz del laberinto. Para evitar que se trate de conectar doble una casilla, solo vamos
     * conectando casilla al este y sur.
     * 
     * Primero se agregan todas las casillas (primer for) y despues se conectan las casillas (segundo y terecer for)
     */
    private void plot() {
        mazeG = new Grafica<Box>();
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                mazeG.agrega(maze[i][j]);
            }
        }
        
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                if (!maze[i][j].e && !maze[i][j].bbt.equals("e")) {
                    mazeG.conecta(maze[i][j], maze[i][j+1], 1 + maze[i][j].score + maze[i][j+1].score);
                }
                
                if (!maze[i][j].s && !maze[i][j].bbt.equals("s")) {
                    mazeG.conecta(maze[i][j], maze[i+1][j]);
                }
            }
        }
    }
    
    /*
     * Metodo que resuelve el laberinto. Aqui se llama al algoritmo de Dijkstra sobre la grafica ya creada con el método
     * plot() y se resuelve. Despues, se recrea la trayectoría con la lista resultante de dijkstra() y se agregan en un
     * String que se regresa. En este String (el string es solucion y es un atributo de clase) se va agregando una linea ya
     * sea al sur o norte de la casilla y se va reconstrullendo la salida al laberinto
     */
    private void solve() {
        Lista<VerticeGrafica<Box>> list = mazeG.dijkstra(begining, end);
        Lista<Box> tray = new Lista<Box>();
        for (VerticeGrafica<Box> b : list) {
            tray.agrega(b.get());
        }
        
        if (tray.getElementos() == 0) {
            System.out.println("Archivo invalido");
            System.exit(-1);
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tray.getElementos()-1; i++) { //Checar condiciones con las entradas y salidas (posible error)
            Box temp = tray.get(i);
            temp.prev = tray.get(i+1);
            
            if (!temp.n && !temp.bbt.equals("n") && temp.prev.equals(maze[temp.posicion.fila - 1][temp.posicion.columna])) {
                sb.append(maze[temp.posicion.fila - 1][temp.posicion.columna].conectSouth());
            }
            
            else if (!temp.s && !temp.bbt.equals("s") && temp.prev.equals(maze[temp.posicion.fila + 1][temp.posicion.columna])) {
                sb.append(temp.conectSouth());
            }
            
            else if (!temp.e && !temp.bbt.equals("e") && temp.prev.equals(maze[temp.posicion.fila][temp.posicion.columna + 1])) {
                sb.append(temp.conectRight());
            }
            
            else if (!temp.o && !temp.bbt.equals("o") && temp.prev.equals(maze[temp.posicion.fila][temp.posicion.columna - 1])) {
                sb.append(maze[temp.posicion.fila][temp.posicion.columna - 1].conectRight());
            }
        }
        
        solucion = sb.toString();
    }

    //Una representación en String del laberinto, aquí no se muestra las soluciones. Se uso para ver que el SVG concordara con el String
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maze.length; i++) {
            StringBuilder top = new StringBuilder();
            StringBuilder bottom = new StringBuilder();
            for (int j = 0; j < maze[0].length; j++) {
                top.append(maze[i][j].topString());
                bottom.append(maze[i][j].bottomString());
            }
            sb.append(top).append("\n").append(bottom).append("\n");
        }
    
        return sb.toString();
    }
    
    /*
     * Metodo que escribe el laberinto en SVG
     * 
     * Utiliza el metodo auxiliar de la clase Box, por lo que basicamente solo agrega las etiquetas del inicio y el final
     * y recorre toda la matriz del laberinto dando .toSVG a las casillas.
     */
    public String toSVG() {
        StringBuilder sb = new StringBuilder();
        sb.append("<svg width=\"" + (maze[0].length * 20 + 100) + "\" height=\"" + (maze.length * 20 + 100) + "\" version=\"1.1\" xmlns=\"https://www.w3.org/2000/svg\">\n");
    
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                sb.append(maze[i][j].toSVG());
            }
        }
        
        sb.append(solucion);
    
        sb.append("</svg>");
    
        return sb.toString();
    }
    
    //Metodo que escribe el archivo .mze
    public void writeMaze() {
        byte prim = (byte) (0x4D);
        byte seg = (byte) (0x41);
        byte ter = (byte) (0x5A);
        byte cuart = (byte) (0x45);
        
        byte height = (byte) (maze.length & 0xFF);
        byte width = (byte) (maze[0].length  & 0xFF);
        
        try {
            BufferedOutputStream osw = new BufferedOutputStream(System.out);
            osw.write(prim);
            osw.write(seg);
            osw.write(ter);
            osw.write(cuart);
            osw.write(height);
            osw.write(width);
            
            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[0].length; j++) {
                    osw.write(maze[i][j].toByte());
                }
            }
            
            osw.close();
        } catch (Exception ioe) {}
    }

    //Metodo auxiliar para llenar el laberinto de casillas cerradas. 
    private void fillMaze(int h, int w) {
        for (int i = 0; i < h; i++)
            for (int j = 0; j < w; j++)
                maze[i][j] = new Box(i, j);
    }
}
