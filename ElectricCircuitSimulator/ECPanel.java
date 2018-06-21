package ElectricCircuitSimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JPanel;

public class ECPanel extends JPanel implements MouseListener, MouseMotionListener{
	
	ECFrame frame;
	ECData data;
	
	double Xmin, Xmax;
	double Ymin, Ymax;
	
	Vector<Vector<Point>> VS = new Vector<Vector<Point>>();
	
	int bgLineWidth = 2;
	int drLineWidth = 5;
	
	Point EXnow, EXnext;
	int EXtype;
	int TYPE_RESISTANCE = 0;
	int TYPE_BATTERY    = 1;

	double size_ratio = 1.5;
	
	double ratio = 0;
	
	ECPanel(ECFrame frame, ECData data) {
		this.frame = frame;
		this.data  = data;
		
		Xmin = -4; Xmax = 6;
		Ymin = -4; Ymax = 6;
		
		EXnow  = new Point();
		EXnext = new Point();
		EXtype = 0;
		
		EXnow  = new Point(-1, -1);
		EXnext = new Point(-2, -1);
		
		addMouseListener(this);
		addMouseMotionListener(this);

		Vector<Point> newV = new Vector<Point>();		
		VS.add(newV);
		
		Time TimeCheck = new Time();
		TimeCheck.start(); 
		
		ReCalulateThread rc = new ReCalulateThread();
		rc.start();
		
		
	}
	

	class Time extends Thread{
		
		public void run() {

			while(true) {
				ratio += 0.01;
				if(ratio > 1)
					ratio -= 1;
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	
	public DPoint DataToPanel(DPoint P) {
		double x = (P.x - Xmin) / (Xmax - Xmin) * (double)getWidth();
		double y = (P.y - Ymin) / (Ymax - Ymin) * (double)getHeight();
		
		return new DPoint(x, y);
	}
	public DPoint PanelToData(DPoint P) {
		double x = (P.x / (double)getWidth () ) * (Xmax - Xmin) + Xmin;
		double y = (P.y / (double)getHeight() ) * (Ymax - Ymin) + Ymin;
		
		return new DPoint(x, y);
	}
	public void drawLineWithPoint(Point P1, Point P2, Graphics2D g) {
		DPoint CP1 = DataToPanel(new DPoint(P1) );
		DPoint CP2 = DataToPanel(new DPoint(P2) );		
		g.drawLine(CP1.GetIX(), CP1.GetIY(), CP2.GetIX(), CP2.GetIY());
	}
	public void drawLineWithPoint(DPoint P1, DPoint P2, Graphics2D g) {
		DPoint CP1 = DataToPanel(P1);
		DPoint CP2 = DataToPanel(P2);		
		g.drawLine(CP1.GetIX(), CP1.GetIY(), CP2.GetIX(), CP2.GetIY());
	}
	public void fillOvalWithPoint( Point P, Graphics2D g, int size) {		
		DPoint CP = DataToPanel(new DPoint(P) );
		g.fillOval(CP.GetIX() - size/2, CP.GetIY() - size/2, size, size);
	}
	public void fillOvalWithPoint(DPoint P, Graphics2D g, int size) {		
		if(size < 0) return;
		DPoint CP = DataToPanel(P);
		g.fillOval(CP.GetIX() - size/2, CP.GetIY() - size/2, size, size);
	}
	public void showAnsWithPoint(DPoint P, Graphics2D g, double ans) {
		if(ans <= 0.0) return;
	    Font font = g.getFont().deriveFont( (float) (9.0f * size_ratio));
	    g.setFont( font );
	    
		String s = String.format("%.2f", ans);

		
				
		double sizeX = 
	            g.getFontMetrics().getStringBounds(s, g).getWidth();
		double sizeY = 
	            g.getFontMetrics().getStringBounds(s, g).getHeight();
		DPoint CP = DataToPanel(P);
		g.drawString(s, (float)(CP.GetIX() - sizeX/2), (float)(CP.GetIY() + sizeY/3) );
	}
	
	public DPoint Internal(Point P1, Point P2) {
		DPoint DP1 = new DPoint(P1);
		DPoint DP2 = new DPoint(P2);
		DPoint DP = new DPoint(0, 0);

		DP.x = DP1.x * ratio + DP2.x * (1-ratio);
		DP.y = DP1.y * ratio + DP2.y * (1-ratio);
		
		return DP;
	}
	

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.BLACK);

		BackgroundLine(g);

//		if(data.ButtonClicked == data.SELECT_WIRE)
			SelectLine(g);

		PastLine(g);				
		DrawElement(g);

		DrawLine(g);
		DrawExpect(g);		
		DrawGround(g);
		
		
//		if(data.ButtonClicked == data.CALCULATE_CURRENT)
		DrawCurrent(g);

			
		DrawProcessing(g);
		
		if(data.IsDrag)
			DrawDrag(g);
	}
	
	
	public void BackgroundLine(Graphics g) {
		Graphics2D g2;
		g.setColor(Color.GRAY);
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(bgLineWidth));

		for(int i=0; i<data.NX; i++) {
			for(int j=0; j<data.NY; j++) {
				Point now = new Point(i  , j  );
				Point dn  = new Point(i  , j+1);
				Point rt  = new Point(i+1, j  );
				
//				g.drawLine(i*10, j*10, i*10, j*10+10);
				drawLineWithPoint(now, dn, g2);
				drawLineWithPoint(now, rt, g2);

			}
		}
	}
	public void PastLine(Graphics g) {
		Graphics2D g2;
		g.setColor(Color.WHITE);
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(drLineWidth));

        for(int i=0; i<data.NX; i++)
        	for(int j=0; j<data.NY; j++) {
        		Point now = new Point(i  , j  );
        		Point rt  = new Point(i+1, j  );
        		Point dn  = new Point(i  , j+1);
        		
        		g2.setColor(Color.WHITE);
        		if(data.GetConnect(now, rt))
        			drawLineWithPoint(now, rt, g2);

        		g2.setColor(Color.WHITE);
        		if(data.GetConnect(now, dn))
        			drawLineWithPoint(now, dn, g2);
        	}

	}
	public void DrawElement(Graphics g) {
		Graphics2D g2;
		g.setColor(Color.WHITE);
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(drLineWidth));

        for(int i=0; i<data.NX; i++)
        	for(int j=0; j<data.NY; j++) {
        		Point now = new Point(i  , j  );
        		Point rt  = new Point(i+1, j  );
        		Point dn  = new Point(i  , j+1);
        		
        		double di = (double)i;
        		double dj = (double)j;
        		
        		if(data.GetElement(now, rt) == data.RESISTANCE) {
        			double ratio = 1./12.;

        			g2.setColor(Color.BLACK);	
        	        g2.setStroke(new BasicStroke(drLineWidth));
        	        drawLineWithPoint(new DPoint(di + ratio*2, dj - 0.00), new DPoint(di + ratio*10, dj + 0.00), g2);

        	        g2.setColor(data.GetColor(data.RESISTANCE));
        			g2.setStroke(new BasicStroke(drLineWidth*2/3));

        			
        			di += ratio*2;
        			drawLineWithPoint(new DPoint(di + ratio*0, dj + 0.00), new DPoint(di + ratio*1, dj + 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*2, dj + 0.00), new DPoint(di + ratio*1, dj + 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*2, dj + 0.00), new DPoint(di + ratio*3, dj - 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*4, dj + 0.00), new DPoint(di + ratio*3, dj - 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*4, dj + 0.00), new DPoint(di + ratio*5, dj + 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*6, dj + 0.00), new DPoint(di + ratio*5, dj + 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*6, dj + 0.00), new DPoint(di + ratio*7, dj - 0.15), g2);
        			drawLineWithPoint(new DPoint(di + ratio*8, dj + 0.00), new DPoint(di + ratio*7, dj - 0.15), g2);
        			di -= ratio*2;
        		}
        		if(data.GetElement(now, rt) == data.BATTERY   ) {
        			g2.setColor(Color.BLACK);	
        	        g2.setStroke(new BasicStroke(drLineWidth*2));
        	        drawLineWithPoint(new DPoint(di + 0.45, dj - 0.00), new DPoint(di + 0.55, dj + 0.00), g2);

        	        g2.setColor(data.GetColor(data.BATTERY   ));        			
        			if(data.GetValue(now, rt) < 0) {
	        	        g2.setStroke(new BasicStroke(drLineWidth/2));
	        	        drawLineWithPoint(new DPoint(di + 0.55, dj - 0.30), new DPoint(di + 0.55, dj + 0.30), g2);
	        			g2.setStroke(new BasicStroke(drLineWidth));
	        			drawLineWithPoint(new DPoint(di + 0.45, dj - 0.15), new DPoint(di + 0.45, dj + 0.15), g2);
        			}else{
	        	        g2.setStroke(new BasicStroke(drLineWidth/2));
	        			drawLineWithPoint(new DPoint(di + 0.45, dj - 0.30), new DPoint(di + 0.45, dj + 0.30), g2);        				
	        			g2.setStroke(new BasicStroke(drLineWidth));
	        	        drawLineWithPoint(new DPoint(di + 0.55, dj - 0.15), new DPoint(di + 0.55, dj + 0.15), g2);
        			}
        		}

        		if(data.GetElement(now, dn) == data.RESISTANCE) {
        			double ratio = 1./12.;
        			g2.setColor(Color.BLACK);	
        	        g2.setStroke(new BasicStroke(drLineWidth));
        	        drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*2), new DPoint(di + 0.00, dj + ratio*10), g2);

        	        g2.setColor(data.GetColor(data.RESISTANCE));
        	        g2.setStroke(new BasicStroke(drLineWidth*2/3));
        	        
        			dj += ratio*2;
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*0), new DPoint(di + 0.15, dj + ratio*1), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*2), new DPoint(di + 0.15, dj + ratio*1), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*2), new DPoint(di - 0.15, dj + ratio*3), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*4), new DPoint(di - 0.15, dj + ratio*3), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*4), new DPoint(di + 0.15, dj + ratio*5), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*6), new DPoint(di + 0.15, dj + ratio*5), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*6), new DPoint(di - 0.15, dj + ratio*7), g2);
        			drawLineWithPoint(new DPoint(di + 0.00, dj + ratio*8), new DPoint(di - 0.15, dj + ratio*7), g2);
        			dj -= ratio*2;
        		}
        		if(data.GetElement(now, dn) == data.BATTERY   ) {
        			g2.setColor(Color.BLACK);	
        	        g2.setStroke(new BasicStroke(drLineWidth*2));
        	        drawLineWithPoint(new DPoint(di - 0.00, dj + 0.45), new DPoint(di + 0.00, dj + 0.55), g2);
        			
        			g2.setColor(data.GetColor(data.BATTERY   ));
        			if(data.GetValue(now, dn) < 0) {
	        	        g2.setStroke(new BasicStroke(drLineWidth/2));
	        			drawLineWithPoint(new DPoint(di - 0.30, dj + 0.55), new DPoint(di + 0.30, dj + 0.55), g2);
	        	        g2.setStroke(new BasicStroke(drLineWidth));
	        			drawLineWithPoint(new DPoint(di - 0.15, dj + 0.45), new DPoint(di + 0.15, dj + 0.45), g2);
        			}else{
	        	        g2.setStroke(new BasicStroke(drLineWidth/2));
	        			drawLineWithPoint(new DPoint(di - 0.30, dj + 0.45), new DPoint(di + 0.30, dj + 0.45), g2);
	        	        g2.setStroke(new BasicStroke(drLineWidth));
	        			drawLineWithPoint(new DPoint(di - 0.15, dj + 0.55), new DPoint(di + 0.15, dj + 0.55), g2);
        			}
        		}
        	}

		
	}
	public void SelectLine(Graphics g) {
		Graphics2D g2;
		g.setColor(Color.CYAN.darker().darker());
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(drLineWidth*5/2));

        for(int i=0; i<data.NX; i++)
        	for(int j=0; j<data.NY; j++) {
        		Point now = new Point(i  , j  );
        		Point rt  = new Point(i+1, j  );
        		Point dn  = new Point(i  , j+1);

        		if(data.GetSelect (now, rt))
        			drawLineWithPoint(now, rt, g2);
        		if(data.GetSelect (now, dn))
        			drawLineWithPoint(now, dn, g2);
        	}

	}
	public void DrawLine(Graphics g) {
		Graphics2D g2;
		if(data.ButtonClicked == data.DRAW_WIRE)
			g.setColor(Color.ORANGE);
		if(data.ButtonClicked == data.MAKE_RESISTANCE)
			g.setColor(data.GetColor(data.RESISTANCE).brighter() );
		if(data.ButtonClicked == data.MAKE_BATTERY)
			g.setColor(data.GetColor(data.BATTERY   ).brighter() );
		
		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(drLineWidth));

		for(int k=VS.size()-1; k<VS.size(); k++) {
			if(k < 0) break;
			Vector<Point> v = VS.get(k);
			
			for(int i=1; i<v.size(); i++) {
				Point now1 = v.get(i-1);
				Point now2 = v.get(i  );
				drawLineWithPoint(now1, now2, g2);
				
	//			g.drawOval((int)now0.x-5, (int)now0.y-5, 10, 10);
			}
		}
	}
	public void DrawExpect(Graphics g) {
		return;
		/*
		if(data.ButtonClicked != data.MAKE_RESISTANCE 
	&&	   data.ButtonClicked != data.MAKE_BATTERY) return;

		
		Graphics2D g2;
		if(data.ButtonClicked == data.MAKE_RESISTANCE)
			g.setColor(data.GetColor(data.RESISTANCE));
		if(data.ButtonClicked == data.MAKE_BATTERY)
			g.setColor(data.GetColor(data.BATTERY   ));

		g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(drLineWidth));

//        System.out.println(EXnow.x + "/" + EXnow.y);
        
		if(data.GetConnect(EXnow, EXnext))
			drawLineWithPoint(EXnow, EXnext, g2);
		*/
	}
	public void DrawGround(Graphics g) {
		Graphics2D g2;
		g2 = (Graphics2D) g;
		
		g2.setColor(Color.MAGENTA);
		this.fillOvalWithPoint(data.GroundP, g2, 20);
	}
	public void DrawCurrent(Graphics g) {
//		if(data.IsCalculate) return;
		
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(10.0f));
		
		double maxAns = 0.001;
		
		for(int i=0; i<data.NX; i++)
			for(int j=0; j<data.NY; j++) {
				Point now = new Point(i  , j  );
				Point dn  = new Point(i  , j+1);
				Point rt  = new Point(i+1, j  );
				if(maxAns < Math.abs(data.GE.GetAns(now, rt)) )
					maxAns = Math.abs(data.GE.GetAns(now, rt));
				if(maxAns < Math.abs(data.GE.GetAns(now, dn)) )
					maxAns = Math.abs(data.GE.GetAns(now, dn));
			}
		
		for(int i=0; i<data.NX; i++) {
			for(int j=0; j<data.NY; j++) {
				Point now = new Point(i  , j  );
				Point dn  = new Point(i  , j+1);
				Point rt  = new Point(i+1, j  );

				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(new Color(125, 205, 00));
				
				fillOvalWithPoint(Internal(now, rt), g2, (int)(Math.sqrt(+data.GE.GetAns(now, rt) / maxAns) * size_ratio * 20) );
				fillOvalWithPoint(Internal(rt, now), g2, (int)(Math.sqrt(+data.GE.GetAns(rt, now) / maxAns) * size_ratio * 20) );
					
				fillOvalWithPoint(Internal(now, dn), g2, (int)(Math.sqrt(+data.GE.GetAns(now, dn) / maxAns) * size_ratio * 20) );
				fillOvalWithPoint(Internal(dn, now), g2, (int)(Math.sqrt(+data.GE.GetAns(dn, now) / maxAns) * size_ratio * 20) );

				g2.setColor(new Color(255, 255, 255));
				showAnsWithPoint(Internal(now, rt), g2, +data.GE.GetAns(now, rt) );
				showAnsWithPoint(Internal(rt, now), g2, +data.GE.GetAns(rt, now) );
					
				showAnsWithPoint(Internal(now, dn), g2, +data.GE.GetAns(now, dn) );
				showAnsWithPoint(Internal(dn, now), g2, +data.GE.GetAns(dn, now) );
				
				
//				System.out.println("[" + now.x + ":" + now.y + "]" + "[" + rt.x + ":" + rt.y + "]" + data.GE.GetAns(now, rt));
			}
		}

	}
	public void DrawProcessing(Graphics g) {
		return;
		
		/*
		int X = getWidth();
		int Y = getHeight();
		
//		if(!data.IsCalculate) return;
		g.setColor(Color.WHITE);
		g.fillRect(X*1/20, Y*19/20, X*18/20, Y*1/40);
		g.setColor(Color.BLUE);
		g.fillRect(X*1/20, Y*19/20, (X*18/20) * data.Percent / 100, Y*1/40);
		*/
	}
	public void DrawDrag(Graphics g) {
		DPoint P0 = DataToPanel(data.RecodeDragStart);
		DPoint P1 = DataToPanel(data.RecodeDragEnd);
		
		int max_X = Math.max(P0.GetIX(), P1.GetIX());
		int min_X = Math.min(P0.GetIX(), P1.GetIX());
		int max_Y = Math.max(P0.GetIY(), P1.GetIY());
		int min_Y = Math.min(P0.GetIY(), P1.GetIY());
		

		g.setColor(Color.CYAN);
		
		g.drawLine(max_X, min_Y, max_X, max_Y);
		g.drawLine(min_X, min_Y, min_X, max_Y);
		g.drawLine(min_X, min_Y, max_X, min_Y);
		g.drawLine(min_X, max_Y, max_X, max_Y);

	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(data.ButtonClicked == data.SELECT_WIRE)
			ClickedSelect(e);

		if(data.ButtonClicked == data.EXTENSION_PANEL)
			ClickedExtension(e);
		if(data.ButtonClicked == data.CONTRACTION_PANEL)
			ClickedContraction(e);
		
		/*
		if(data.ButtonClicked == data.MAKE_RESISTANCE 
	||	   data.ButtonClicked == data.MAKE_BATTERY) 
			ClickedMakeElement(e);
		*/
		
		
		if(data.ButtonClicked == data.CLEAN_CIRCUIT)
			data.CleanCurcuit();
		
		if(data.ButtonClicked == data.SET_GROUND)
			ClickedSetGroundPoint(e);
		
		if(data.ButtonClicked == data.CALCULATE_CURRENT)
			data.CalculateCurrent();
		
		
	}
	public void ClickedSelect(MouseEvent e) {
		DPoint now = new DPoint(e.getPoint());
		DPoint  ch = PanelToData(now);
		 Point get = new Point( (int)(ch.x + 0.5), (int)(ch.y + 0.5) );

		 data.Select(get);
	}	
	public void ClickedExtension(MouseEvent e) {
		DPoint now = new DPoint(e.getPoint());
		DPoint  ch = PanelToData(now);
		
		if(Xmax - Xmin < 3) return;
		
		double changediff = (Xmax - Xmin)/2 / 1.5;

		Xmin = ch.x - changediff; Xmax = ch.x + changediff;
		Ymin = ch.y - changediff; Ymax = ch.y + changediff;
		
		size_ratio *= 1.5;
	}	
	public void ClickedContraction(MouseEvent e) {
		DPoint now = new DPoint(e.getPoint());
		DPoint  ch = PanelToData(now);

		if(Xmax - Xmin > 25) return;

		
		double changediff = (Xmax - Xmin)/2 * 1.5;

		Xmin = ch.x - changediff; Xmax = ch.x + changediff;
		Ymin = ch.y - changediff; Ymax = ch.y + changediff;
		
		size_ratio *= 1/1.5;

	}
	public void ClickedMakeElement(MouseEvent e) {
		if(!data.GetConnect(EXnow, EXnext)) return;

		if(data.ButtonClicked == data.MAKE_RESISTANCE)
			data.SetElementValue(EXnow, EXnext, data.RESISTANCE, 1);
		if(data.ButtonClicked == data.MAKE_BATTERY)
			data.SetElementValue(EXnow, EXnext, data.BATTERY   , 1);
	}
	public void ClickedSetGroundPoint(MouseEvent e) {
		DPoint now = new DPoint(e.getPoint());
		DPoint  ch = PanelToData(now);
		 Point get = new Point( (int)(ch.x + 0.5), (int)(ch.y + 0.5) );
		 
		 data.SetGroundPoint(get);
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		data.SaveFile = false;
		// TODO Auto-generated method stub
		if(data.ButtonClicked == data.TRANSFORM_PANEL)
			PressedTransform(e);

		if(data.ButtonClicked == data.DRAG_TO_SELECT)
			PressedSelect(e);
		
	}
	public void PressedTransform(MouseEvent e) {
		Point get = e.getPoint();
		data.SetRecodeTransform(get);
	}
	public void PressedSelect(MouseEvent e) {
//		DPoint get = PanelToData( new DPoint(e.getPoint()) );
		DPoint get = PanelToData( new DPoint(e.getPoint() ) );
		data.RecodeDragStart = get;
		data.RecodeDragEnd	 = get;
		
		data.IsDrag = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(data.ButtonClicked == data.DRAW_WIRE
		|| data.ButtonClicked == data.MAKE_BATTERY
		|| data.ButtonClicked == data.MAKE_RESISTANCE)
			ReleasedAddVector(e);


		if(data.ButtonClicked == data.TRANSFORM_PANEL)
			ReleasedTransform(e);


		if(data.ButtonClicked == data.DRAG_TO_SELECT)
			ReleasedSelect(e);
	}
	public void ReleasedAddVector(MouseEvent e) {
		Vector<Point> v = VS.get(VS.size()-1);
		
		for(int i=1; i<v.size(); i++) {
			Point now1 = v.get(i-1);
			Point now2 = v.get(i  );
			
			if(data.ButtonClicked == data.DRAW_WIRE)
				data.SetConnect(now1, now2, true);
			if(data.ButtonClicked == data.MAKE_RESISTANCE)
				data.SetElementValue(now1, now2, data.RESISTANCE, 1);
			if(data.ButtonClicked == data.MAKE_BATTERY)
				data.SetElementValue(now1, now2, data.BATTERY   , 1);
		}

		
		Vector<Point> newV = new Vector<Point>();		
		VS.add(newV);
	}
	public void ReleasedTransform(MouseEvent e) {
		Point get = e.getPoint();
		Point recode = data.GetRecodeTransform();
		
		DPoint  dataG = PanelToData(new DPoint(get   ));
		DPoint  dataR = PanelToData(new DPoint(recode));
		
		Xmin += dataR.x - dataG.x; Xmax += dataR.x - dataG.x;
		Ymin += dataR.y - dataG.y; Ymax += dataR.y - dataG.y;
		
	}
	public void ReleasedSelect(MouseEvent e) {
		DPoint get = PanelToData( new DPoint(e.getPoint() ) );
		data.RecodeDragEnd = get;
		data.IsDrag = false;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(data.ButtonClicked == data.DRAW_WIRE
		|| data.ButtonClicked == data.MAKE_BATTERY
		|| data.ButtonClicked == data.MAKE_RESISTANCE)
			DraggedDraw(e);
		if(data.ButtonClicked == data.TRANSFORM_PANEL)
			DraggedTransform(e);
		
		if(data.ButtonClicked == data.DRAG_TO_SELECT)
			DraggedSelect(e);

	}
	public void DraggedTransform(MouseEvent e) {
		Point get = e.getPoint();
		Point recode = data.GetRecodeTransform();
		
		DPoint  dataG = PanelToData(new DPoint(get   ));
		DPoint  dataR = PanelToData(new DPoint(recode));
		
		Xmin += dataR.x - dataG.x; Xmax += dataR.x - dataG.x;
		Ymin += dataR.y - dataG.y; Ymax += dataR.y - dataG.y;
		
		data.SetRecodeTransform(get);
	}
	public void DraggedDraw(MouseEvent e) {
		DPoint now = new DPoint(e.getPoint());
		DPoint  ch = PanelToData(now);
		 Point get = new Point( (int)(ch.x + 0.5), (int)(ch.y + 0.5) );
		
		if(get.x < 1 || get.x >= data.NX || get.y < 1 || get.y >= data.NY) return;

		Vector<Point> V = VS.get(VS.size() - 1);
		if(V.size() == 0) {
			V.add(get);
		}else{
			if(V.size() >= 2) {
				if(V.get(V.size() - 2).x == get.x && V.get(V.size() - 2).y == get.y) {
					V.removeElementAt(V.size() - 1);
				}
				if(V.lastElement().x != get.x || V.lastElement().y != get.y) {
					VectorAdd(V, V.lastElement(), get);
				}
			}else{
				if(V.lastElement().x != get.x || V.lastElement().y != get.y) {
					VectorAdd(V, V.lastElement(), get);
				}
			}
		}
	}
	public void VectorAdd(Vector<Point> VP, Point A, Point B) {
		Point now = new Point(); now.x = A.x; now.y = A.y;
		Point copy;

		for(; now.x < B.x; now.x++, copy = new Point(now.x, now.y), VP.add(copy));
		for(; now.x > B.x; now.x--, copy = new Point(now.x, now.y), VP.add(copy));
		
		for(; now.y < B.y; now.y++, copy = new Point(now.x, now.y), VP.add(copy));
		for(; now.y > B.y; now.y--, copy = new Point(now.x, now.y), VP.add(copy));
		
	}
	public void DraggedSelect(MouseEvent e) {
		DPoint get = PanelToData( new DPoint(e.getPoint() ) );
		data.RecodeDragEnd = get;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

		if(data.ButtonClicked != data.MAKE_RESISTANCE 
	&&	   data.ButtonClicked != data.MAKE_BATTERY) return;

		DPoint  Dnow = new DPoint(e.getPoint());
		DPoint    ch = PanelToData(Dnow);
		 Point   now = new  Point( (int)(ch.x + 0.5), (int)(ch.y + 0.5) );
		DPoint  diff = new DPoint( ch.x - (double)now.x, ch.y - (double)now.y);
		 Point  next = new  Point(now.x, now.y);
		 
		if(diff.y >= 0 && Math.abs(diff.y) >= Math.abs(diff.x) ) next.y += 1;
		if(diff.y  < 0 && Math.abs(diff.y) >= Math.abs(diff.x) ) next.y -= 1;
		if(diff.x >= 0 && Math.abs(diff.x) >  Math.abs(diff.y) ) next.x += 1;
		if(diff.x  < 0 && Math.abs(diff.x) >  Math.abs(diff.y) ) next.x -= 1;

		EXnow .x = now .x; EXnow .y = now .y;
		EXnext.x = next.x; EXnext.y = next.y;
		
		if(data.ButtonClicked == data.MAKE_RESISTANCE)
			EXtype = TYPE_RESISTANCE;
		if(data.ButtonClicked == data.MAKE_BATTERY)
			EXtype = TYPE_BATTERY;

		
	}
	

	class ReCalulateThread extends Thread{
		public void run() {
			while(true) {
				if(!data.IsCalculate)
					data.CalculateCurrent();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
}


class DPoint{
	double x;
	double y;
	DPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	DPoint(int x, int y) {
		this.x = (double)x;
		this.y = (double)y;
	}
	DPoint(Point P) {
		this.x = (double)P.x;
		this.y = (double)P.y;
	}
	int GetIX() {return (int)x;}
	int GetIY() {return (int)y;}
}






