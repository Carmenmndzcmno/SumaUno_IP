import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class Tablero extends JPanel{ //Tablero es una subclase de JPanel que es una superclase predefinida
	
	/**
	 * @param filas: valor entero de las filas
	 * @param valores: matriz de enteros de la que partimos
	 * @param puntuacion: puntuacion del juego
	 * @param hayAlgunoIgual: boolean para comprobar si hay alguna casilla igual contigua a la que se ha pulsado para más tarde hacer los movimientos necesarios del juego
	 * @param vida: vida del jugador, parte del 3 (valor máximo)
	 * @param estadoPartida: para la ampliación de Fin de Partida. Este parámetro comprueba el estado de la Partida. Si es true la partida sigue funcionando
	 * @param exito: valor para comprobar los exitos/combos para obtener una vida más en el juego. Ampliación de recuperar vida.
	 */

	private int filas;
	int [][] valores;
	Random r = new Random();
	int puntuacion = 0;
	boolean hayAlgunoIgual;
	int vida = 3;
	boolean estadoPartida = true;
	JFrame ventana;
	int exito=0;

	//Constructores
	Tablero(int filas) {
		//El constructor tiene los parámetros oportunos 
		//para inicializar el tablero y el juego
		this.filas = filas;
		valores = new int [this.filas][this.filas];
		llenarMatriz();
		// Añadimos el 'escuchador' de ratón
		addMouseListener(new MouseHandler());
	}
	
	private void llenarMatriz(){
        for(int i=0;i<valores.length;i++){
            for(int j=0;j<valores[i].length;j++){
                valores[i][j] = r.nextInt(6)+1;//Se le añade un +1 porque si no nunca llegaría al seis y partir del 0
            }
        }
    }
	private void valorAleatorio(int j, int i){
		hayAlgunoIgual = true;
		int valorBase = valores[j][i];
		valores[j][i]=0;
		for (int a = j; a-1>0 && valorBase==valores[a-1][i] && valores[a][i]==valores[a-1][i];a--){ //Marcamos como "nulos" (en realidad 0) las casillas que no nos interesan
			valores[a][i] = 0;
		}
		while (valores[j][i]==0){ //Vamos bajando casillas hasta que la casilla de número de fila más alto tenga un valor
			for (int a = j; a>=0;a--){
				if (a!=0){
					valores[a][i]=valores[a-1][i];
				}else{
					int valorOriginal = valores[a][i];
					valores[a][i] = r.nextInt(6)+1;
					while (valorOriginal ==valores[a][i]){
						valores[a][i] = r.nextInt(6)+1;
					}
				}
			}
		}
	}
	private void valorAleatorioAbajo(int j, int i){  //Usamos este método para quitar de la forma más óptima los valores SUR
		hayAlgunoIgual = true;
		int valorBase = valores[j][i];
		valores[j][i]=0; //i constante
		int filaPrimera=j;
		for (int a = j; a+1<filas && valorBase==valores[a+1][i] && valores[a][i]==valores[a+1][i];a++){ //Marcamos como nulos los valores necesarios
			valores[a][i] = 0;
			filaPrimera=a;
		}
		while (valores[filaPrimera][i]==0){
			for (int a = filaPrimera; a>=0;a--){
				if (a!=0){
					valores[a][i]=valores[a-1][i];
				}else{
					int valorOriginal = valores[a][i];
					valores[a][i] = r.nextInt(6)+1;
					while (valorOriginal ==valores[a][i]){
						valores[a][i] = r.nextInt(6)+1;
					}
				}
			}
		}
	}
	
	//Método para que según el valor de la matriz haya unos colores u otros
	private Color color(int valor) { //El objeto color es una clase implementada
        //Define colores según los valores de la matriz
		//Vamos a hacerlo de forma gradiente. De colores fríos a cálidos
		if (valor==1){
			return Color.blue;
		} else if (valor==2){
			return Color.cyan;
		}else if ( valor==3){
			return Color.green;
		}else if (valor==4){
			return Color.yellow;
		}else if (valor==5){
			return Color.orange;
		}else if (valor==6){
			return Color.red;
		}else{
			return Color.lightGray;
		}
	}

	private void dibujarMatriz(Graphics g){

		int x;
		int y;

		//Dibujamos los rectangulos del juego
		for(int i=0;i<valores.length;i++){
			y = 10 + (i*40);
			for(int j=0;j<valores[i].length;j++){
				x = 10 + (j*40);
				g.setColor(color(valores[i][j]));
				g.fillRect(x, y, 40, 40);
			}
		}

		//Dibujamos los numeros del juego
		Font fuente = new Font("Arial", Font.PLAIN, 20);
		g.setFont(fuente);
		g.setColor(Color.black);
		for(int i=0;i<valores.length;i++){
			y = 35 + (i*40);
			for(int j=0;j<valores[i].length;j++){
				x = 25 + (j*40);
				g.drawString(String.valueOf(valores[i][j]), x, y);
			}
		}
	}
	//Textos de la "screen"
	private void puntuacionVidas(Graphics g){
		Font fuente = new Font("Arial", Font.PLAIN, 15);
		g.setFont(fuente);
		g.drawString("Puntuacion: "+puntuacion, 10, filas*40+40);
		g.drawString("Vidas: "+vida, 10, filas*40+70);
	}

	//Método paint
	public void paintComponent(Graphics g) {
		//Método estandar para dibujar cosas en java con JPanel
	    //Graphics es una clase que tiene distintas funciones para dibujar cosas en la "screen", y la denominaremos como g
	    //Digamos que Graphics, es como el pincel que usas para pintar
		super.paintComponent(g);//el super hace referencia a que su clase padre es JPanel

		//Aquí iría el código para pintar el estado del tablero
		dibujarMatriz(g);
		puntuacionVidas(g);
	}	
	
	private void esIgual(int j, int i){ //Comprobamos si hay en las distintas direcciones casillas que cumplan las condiciones del juego

		int contador = 0; //con este contador multiplicamos luego el valorBase para calcular la puntuación
		int valorBase = valores[j][i]; //guardamos el valor base de la casilla de la que partimos para no perderlo 
		int fila = j; //guardamos el valor base de la j de la que partimos para no perderlo 
		int columna = i; //guardamos el valor base de la i de la que partimos para no perderlo 
		
		while (j-1>=0 && i-1>=0 && valorBase==valores[j-1][i-1]){ //NO (diagonal superior izquierda)
			contador++;
			j--;
			i--;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (i-1>=0  && valorBase==valores[j][i-1]){ //O (filas izquierda)
			contador++;
			i--;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (j+1<filas && i-1>=0 && valorBase==valores[j+1][i-1]){ //SO (diagonal inferior izquierda)
			contador++;
			j++;
			i--;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (j-1>=0 && valorBase==valores[j-1][i]){ //N (columna arriba)
		contador++;
		j--;
		valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (j-1>=0 && i+1<filas && valorBase==valores[j-1][i+1]){ //NE (diagonal superior derecha)
			contador++;
			j--;
			i++;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (i+1<filas && valorBase==valores[j][i+1]){ //E (filas hacia la derecha)
			contador++;
			i++;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (j+1<filas && i+1<filas && valorBase==valores[j+1][i+1]){ //SE (diagonal inferior derecha)
			contador++;
			j++;
			i++;
			valorAleatorio(j, i);
		}
		j=fila;
		i=columna;
		valores[j][i] = valorBase;
		while (j+1<filas && valorBase==valores[j+1][i]){ //S (columna abajo)
			contador++;
			j++;
			valorAleatorioAbajo(j, i);
		}
		puntuacion=contador*valorBase;
	}

	// AMPLIACIÓN 2: Detección de fin de partida
	/**
	* @return movimientosPosibles(): devuelve un boolean que dice si siguen habiendo movimientos posibles en la partida 
	*/
	private boolean movimientosPosibles(){
		for (int i = 0; i < filas; i++) {
            for (int j = 0; j < filas; j++) {
                int valorActual = valores[i][j]+1;
				//Por tema de no salirnos de la matriz tendremos que mirar las direcciones una por una para saber si siguen habiendo movimientos posibles
				if (i+1<filas&&valorActual==valores[i+1][j]){
					return estadoPartida = true;
				} else if (i-1>=0 && valorActual==valores[i-1][j]){
					return estadoPartida = true;
				} else if (j+1<filas&&valorActual==valores[i][j+1]){
					return estadoPartida = true;
				}else if (j-1>=0&&valorActual==valores[i][j-1]){
					return estadoPartida = true;
				}else if (j+1<filas && i+1<filas && valorActual==valores[i+1][j+1]){
					return estadoPartida = true;
				} else if (j-1>=0&&i-1>=0&&valorActual==valores[i-1][j-1]){
					return estadoPartida = true;
				}else if (j-1>=0&&i+1<filas&&valorActual==valores[i+1][j-1]){
					return estadoPartida = true;
				}else if(i-1>=0&&j+1<filas&&valorActual==valores[i-1][j+1]){
					return estadoPartida = true;
				}else {
					estadoPartida = false;
				}

            }
        }
		return estadoPartida;
	}
	
	public void setApp(JFrame ventana){	//Relacionamos el "app" del main con "ventana" para más tarde poder cerrar la misma al fin de la partida
		this.ventana=ventana;
	}

	//Ampliación número 1: Recuperación de vida
	//Cuando una jugada es valida y no tenemos el máximo de vidas, incrementamos el valor 
	private void jugadaValida(){
		if(vida<3){
			exito++;
		}
	}
	//Ponemos a cero el valor cuando hemos recuperado una vida o cuando hemos cometido un fallo.
	private void resetearJugadasValidas(){
		exito = 0;
	}
	//Recuperamos una vida cuando hacemos 5 jugadas sin tener ningún fallo.
	private void recuperarVidas(){
		if(exito==5){
			vida++;
			resetearJugadasValidas();
		}
	}

	private void imprimirMatriz(){ // Este bucle para imprimir la matriz por teclado sólo lo hacemos ya que en la página 12 del enunciado pone que se requiere imprimirla
		for (int a = 0; a < filas; a++) {
			for (int b = 0; b < filas; b++) {
				System.out.print(valores[a][b] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	//Clase  privada para las acciones del ratón:
	//El ratón al final va a desencadenar todo el juego
	private class  MouseHandler extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			// Este bucle para imprimir la matriz por teclado sólo lo hacemos ya que en la página 12 del enunciado pone que se requiere imprimirla
			imprimirMatriz();

			//JOptionPane.showMessageDialog(null, String.format("Raton %d, %d \n",e.getX(),e.getY()));
			// Esta string lo que hace mostrar un diálogo para mostrar qué es de las coordenadas del ratón, es decir, dónde está

			//Si queremos que al clickar una casilla cambie de color, vamos a necesitar primero saber ¿Dónde está?
			// Dependemos de una x y una y dependientes del click. Por tanto:
			int x = e.getX();
			int y = e.getY(); //recordamos que el "evento de ratón e" es el click

			//Ahora necesitamos saber en qué i y j está. Por tanto, si en líneas anteriores dijimos que:
			//y = 10 + (i*40);
			//x = 10 + (i*40);
			int j = (y - 10)/40; //esto será las filas
			int i = (x - 10)/40; //esto será las columnas

			//Ahora tenemos los índices únicos de cada casilla
			// Sumamos 1 a ese valor
			if (i<=filas && i>=0 && j>=0 && j<=filas){ //aseguramos de estar dentro del tablero (si no, salta una excepción)
				valores[j][i]++; 
			}
			hayAlgunoIgual = false;
			esIgual(j, i);
			if (hayAlgunoIgual==false){ //si el jugador comete un fallo "se le castiga" quitándole una vida
				valores[j][i]--; 
				vida--;
			} else { //si el jugador hizo un movimiento certero, premiarle para a lo largo que pueda obtener una vcida (máximo 3)
				jugadaValida();
			}

			if (vida==1){ //AMPLIACIÓN 2 DETECCIÓN FIN DE PARTIDA
				movimientosPosibles();	//En el caso de quedar una vida, comprobamos si existen movimientos posibles
			} else if (vida==0){	//Si quedan 0 vidas, ¡se terminó la partida!
				estadoPartida = false;
			}

			if (estadoPartida==false){ //Fin de la partida
				ventana.dispose();
				String fin=String.format("¡Perdiste! Has obtenido %d puntos",puntuacion);
				JOptionPane.showMessageDialog(null, fin);
			}
			recuperarVidas();
			repaint();
		}
	}
}

