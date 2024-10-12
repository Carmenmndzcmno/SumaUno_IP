import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Carmen Méndez Camino
 * @see Tablero
 */

public class Main {

	public static void main(String[] args) {
			
		JFrame app = new JFrame("SumaUno");
		
		String entrada= JOptionPane.showInputDialog("Filas:");
		int filas=Integer.parseInt(entrada);

		String salida=String.format("Has elegido %d filas", filas);
        JOptionPane.showMessageDialog(null, salida);

		Tablero t = new Tablero(filas);
		t.setApp(app);
		
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setBounds(100, 100, (filas+1)*40, (filas+1)*60);
		app.add(t);
		app.setLocationRelativeTo(null);
		app.setVisible(true);

	}
}
