package ElectricCircuitSimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

class ButtonPanel extends JPanel implements MouseListener{
	ECFrame frame;
	ECData data;

	int rt;
	
	ButtonPanel(ECFrame frame, ECData data) {
		this.frame = frame;
		this.data  = data;
		rt = 7;
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);
		
		for(int j=0; j<1; j++) 
		for(int i=0; i<10; i++) {
			g.setColor(Color.LIGHT_GRAY);
			
			if(data.GetButtonClicked() == (j*10 + i) ) {
				g.setColor(Color.GREEN);
			}
			
			int baseX = getWidth() / 2 * j;
			int X = getWidth() / 1;
			int Y = getHeight();
							
			g.fillRect(baseX + X/rt, X/rt + X*i, X - X/rt*2, X - X/rt*2);
			
			drawIcon(baseX + X/rt, X/rt + X*i, X - X/rt*2, X - X/rt*2, j*10 + i, g);
		}
	}
	
	public void drawIcon(int bx, int by, int sx, int sy, int type, Graphics g) {
		Graphics2D g2;
		if(type == data.DRAW_WIRE) {
			g2 = (Graphics2D) g;
	        g2.setStroke(new BasicStroke(5));
	        g2.setColor(Color.WHITE);
	        g2.drawLine(bx + sx/5, by+sy/2, bx + sx*4/5, by+sy/2);
		}
		if(type == data.SELECT_WIRE) {
			ImageIcon ic = new ImageIcon("[Type]Select.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.TRANSFORM_PANEL) {
			ImageIcon ic = new ImageIcon("[Type]Transform.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);			
		}
		if(type == data.EXTENSION_PANEL) {
			ImageIcon ic = new ImageIcon("[Type]ZoomIn.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.CONTRACTION_PANEL) {
			ImageIcon ic = new ImageIcon("[Type]ZoomOut.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.MAKE_RESISTANCE) {
			g2 = (Graphics2D) g;
	        g2.setStroke(new BasicStroke(5));
			g2.setColor(data.GetColor(data.RESISTANCE));
	        g2.drawLine(bx + sx/5, by+sy/2, bx + sx*4/5, by+sy/2);
		}
		if(type == data.MAKE_BATTERY) {
			g2 = (Graphics2D) g;
	        g2.setStroke(new BasicStroke(5));
			g2.setColor(data.GetColor(data.BATTERY   ));
	        g2.drawLine(bx + sx/5, by+sy/2, bx + sx*4/5, by+sy/2);
		}
		if(type == data.CLEAN_CIRCUIT) {
			ImageIcon ic = new ImageIcon("[Type]Clean.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);			
		}
		if(type == data.SET_GROUND) {
			ImageIcon ic = new ImageIcon("[Type]Ground.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.CALCULATE_CURRENT) {
			ImageIcon ic = new ImageIcon("[Type]Simulation.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.SAVE_FILE) {
			ImageIcon ic = new ImageIcon("[Type]Save.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.LOAD_FILE) {
			ImageIcon ic = new ImageIcon("[Type]Load.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);
		}
		if(type == data.QUIT_FILE) {
			ImageIcon ic = new ImageIcon("[Type]Quit.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);			
		}
		if(type == data.DRAG_TO_SELECT) {
			ImageIcon ic = new ImageIcon("[Type]Select.png");
			Image image = ic.getImage();
			g.drawImage(image, bx, by, sx, sy, this);			
		}
	}

	int PressClicked = -1;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

		Point get = e.getPoint();
		
		for(int j=0; j<1; j++) 
		for(int i=0; i<10; i++) {
			int baseX = getWidth() / 2 * j;
			int X = getWidth() / 1;
			int Y = getHeight();

			int x = get.x - baseX - X/rt;
			int y = get.y - X/rt - X*i;
			
			if(x < X - X/rt*2 && x > 0 && y < X - X/rt*2 && y > 0) {
				PressClicked = j*10 + i;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		Point get = e.getPoint();
		
		for(int j=0; j<1; j++) 
		for(int i=0; i<10; i++) {
			int baseX = getWidth() / 2 * j;
			int X = getWidth() / 1;
			int Y = getHeight();

			int x = get.x - baseX - X/rt;
			int y = get.y - X/rt - X*i;
			
			if(x < X - X/rt*2 && x > 0 && y < X - X/rt*2 && y > 0) {
				if(PressClicked == (j*10 + i) )
					data.SetButtonClicked(j*10 + i);
			}
		}
		

		if(data.ButtonClicked == data.SAVE_FILE)
			ClickedSaveFile(e);
		if(data.ButtonClicked == data.LOAD_FILE)
			ClickedLoadFile(e);
		if(data.ButtonClicked == data.QUIT_FILE)
			ClickedQuitFile(e);
	}
	public void ClickedSaveFile(MouseEvent e) {


	    // a jframe here isn't strictly necessary, but it makes the example a little more real
	    JFrame frame = new JFrame();

	    // prompt the user to enter their name
	    String name = JOptionPane.showInputDialog(frame, "File Name");

	    // get the user's input. note that if they press Cancel, 'name' will be null
	    
	    if(name == null) {
	    	System.out.println("NULL");
	    	return;
	    }else{
	    	if(name.equals("")) {
	    		JOptionPane.showMessageDialog(frame,
	    			    "File Name Required",
	    			    "File Name Warning",
	    			    JOptionPane.WARNING_MESSAGE);
	    		return;
	    	}
	    }
	    

		JFileChooser chooser;
		String choosertitle = "Save";
		  
		chooser = new JFileChooser(); 
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		chooser.setDialogTitle(choosertitle);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
			try {
			
			BufferedWriter out = new BufferedWriter(new FileWriter(chooser.getSelectedFile() + "/" + name + ".ecp") );
			out.write(data.NX + " " +data.NY); out.newLine();
			for(int i=0; i<data.NX; i++)
				for(int j=0; j<data.NY; j++)
					for(int k=0; k<2; k++) {
						int record = (data.IsConnect[i][j][k] == true ? 1 : 0);
						out.write(record+" ");
					}
			for(int i=0; i<data.NX; i++) 
				for(int j=0; j<data.NY; j++)
					for(int k=0; k<2; k++) {
						int record = (data.IsSelect[i][j][k] == true ? 1 : 0);
						out.write(record+" ");
					}
			for(int h=0; h<2; h++)
				for(int i=0; i<data.NX; i++) 
					for(int j=0; j<data.NY; j++)
						for(int k=0; k<2; k++) {
							int record = data.Element[h][i][j][k];
							out.write(record+" ");
						}
	
		    out.close();
				      
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
		}else {
			System.out.println("No Selection ");
		}
		
		data.SaveFile = true;
	}
	public void ClickedLoadFile(MouseEvent e) {
		
		JFileChooser fileChooser;
		fileChooser = new JFileChooser(".");
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		fileChooser.setFileFilter(new FileFilter() {
		      
			public String getDescription() {
			    return "Electric_Circuit Files (*.ecp)";
			}
		   
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".ecp");
		    }
		}
		});
		
		if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			System.out.println(fileChooser.getSelectedFile());
			try {
				BufferedReader in = new BufferedReader(new FileReader(fileChooser.getSelectedFile() ));

				
				int X = ReadInt(in);
				int Y = ReadInt(in);
				
				for(int i=0; i<X; i++)
					for(int j=0; j<Y; j++)
						for(int k=0; k<2; k++)
							data.IsConnect[i][j][k] = (ReadInt(in) == 1 ? true : false);

				for(int i=0; i<X; i++)
					for(int j=0; j<Y; j++)
						for(int k=0; k<2; k++)
							data.IsSelect [i][j][k] = (ReadInt(in) == 1 ? true : false);

				for(int h=0; h<2; h++)
					for(int i=0; i<X; i++) 
						for(int j=0; j<Y; j++)
							for(int k=0; k<2; k++) {
								int l = ReadInt(in);
								if(l >= 0) l = -l; 
								else if(l > -30  ) l += 30;
								else if(l > -300 ) l += 300;
								else if(l > -3000) l += 3000;
								
								data.Element[h][i][j][k] = -l;
//								System.out.println(data.Element[h][i][j][k]);
							}
				
			  	in.close();
			  	
			} catch (IOException e1) {
				System.err.println(e1); // 에러가 있다면 메시지 출력
			    System.exit(1);
			}
		}
	
		data.SaveFile = true;
	}
	public int ReadInt(BufferedReader in){
		
		try {
			int result = 0;
			while(true) {
				int k;
				k = (in.read());
				
//				System.out.print(k + " ");
				
				if(k == -1) return -10000;
				if(k == 32 || k == 10)
					return result;
				
				result *= 10;
				result += (k-48);
				
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -10000;
	}
	public void ClickedQuitFile(MouseEvent e) {
		if(data.SaveFile == true)
			System.exit(0);
		
		
		int save = JOptionPane.showConfirmDialog(frame, "Would you like to save?", "Save",
	            JOptionPane.YES_NO_OPTION);

		if (save == JOptionPane.YES_OPTION)
			ClickedSaveFile(e);
	    System.exit(0);
        	
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}