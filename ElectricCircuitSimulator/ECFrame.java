package ElectricCircuitSimulator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class ECFrame extends JFrame implements KeyListener{
	
	ECData data;
	
	ECPanel panel;
	ButtonPanel left;
	
	
	int ECX = 600;
	int ECY = 600;
	
	double ratio = 1. / 10.;
	int BTX = (int)(ECY * ratio);
	
	int titleH;
	
	ECFrame() {
		setTitle("ECFrame");
		
		data = new ECData();
		
		panel = new ECPanel(this, data);
		left  = new ButtonPanel(this, data);
        left.setPreferredSize(new Dimension(BTX, 0));

		
		add(left, BorderLayout.WEST);
		
		add(panel);
        
		
		setSize(ECX + BTX, ECY);
		
		this.setLocationRelativeTo(null);
		
		setVisible(true);

		titleH = (getHeight()-getContentPane().getHeight());
		setSize(ECX + BTX, ECY + titleH);


		Time TimeCheck = new Time();
		TimeCheck.start(); 
		
		addKeyListener(this);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
//                System.out.println(getWidth() + " : " + (getHeight() - titleH) );
                
                int x = (int)(getWidth() / (1. + ratio));
                int y = getHeight() - titleH;

                setSize( (int)(Math.max(x, y) * (1. + ratio)), Math.max(x, y) + titleH);

                left.setPreferredSize(new Dimension( (int)(Math.max(x, y) * ratio) , 0));
            }
            
        });

	}
	
	

	class Time extends Thread{
		
		public void run() {

			while(true) {
				panel.repaint();
				left.repaint();
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getKeyCode());
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
//		System.out.println(e.getKeyCode());
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		

	    if(e.getKeyCode() == 8) // DELETE
	    {
	    	if(data.ButtonClicked == data.SELECT_WIRE)
	    		data.Delete();
			if(data.ButtonClicked == data.DRAG_TO_SELECT)
	    		data.Delete();
	    }
	}
	



	

}




