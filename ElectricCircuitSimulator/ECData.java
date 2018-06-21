package ElectricCircuitSimulator;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import ElectricCircuitSimulator.ECFrame.Time;

public class ECData {
	
	int ButtonClicked;
	
	int DRAW_WIRE         	= 0;
	int MAKE_RESISTANCE 	= 1;
	int MAKE_BATTERY 		= 2;
	int DRAG_TO_SELECT		= 3;
	int TRANSFORM_PANEL 	= 4;
	int EXTENSION_PANEL		= 5;
	int CONTRACTION_PANEL 	= 6;
	int SAVE_FILE			= 7;
	int LOAD_FILE			= 8;
	int QUIT_FILE			= 9;
	int CLEAN_CIRCUIT		= 10;
	int SET_GROUND			= 11;
	int CALCULATE_CURRENT	= 12;
	int SELECT_WIRE       	= 13;

	Point RecodeTransform;
	
	DPoint RecodeDragStart;
	DPoint RecodeDragEnd;
	
	boolean IsDrag = false;

	
	int NX, NY;
	boolean[][][] IsConnect; // 0 가로, 1 세로
	boolean[][][] IsSelect;
	int    [][][][] Element;
	double [][][]   Current;
	int BLANK 		= 0;
	int RESISTANCE 	= 1; Color Color_Resistance = new Color(204, 000, 102);
	int BATTERY		= 2; Color Color_Battery    = new Color(000, 128, 255);
	// 0 : blank
	// 1 : Resistance
	// 2 : battery
	
	boolean[][]   Check;
	
	Point GroundP;
	
	GaussianElimination GE;
	
	boolean IsCalculate;
	int Percent;
	
	boolean SaveFile = false;
	
	ECData() {
		NX = 25; NY = 25;	
		init();
	}
	ECData(int NX, int NY) {
		this.NX = NX; this.NY = NY;
		init();
	}
	private void init() {
		ButtonClicked = 0;	

		RecodeTransform = new Point(0, 0);
		
		IsConnect = new boolean[NX][NY][2]; //기본값으로 초기화
		IsSelect  = new boolean[NX][NY][2]; //기본값으로 초기화
		Element   = new int [2][NX][NY][2];
		Current   = new double [NX][NY][2];
		
		Check	  = new boolean[NX][NY];
		
		GroundP   = new Point(-100, -100);
		
		for(int i=0; i<NX; i++) for(int j=0; j<NY; j++) 
			for(int k=0; k<2; k++) {
				IsConnect[i][j][k] = false;
				IsSelect [i][j][k] = false;
				Check    [i][j]	   = false;
			}
		
		GE = new GaussianElimination(NX, NY);
		
		IsCalculate = false;
		Percent = 0;
		
		RecodeDragStart	= new DPoint(0, 0);
		RecodeDragEnd	= new DPoint(0, 0);
		
		DraggedThread thread = new DraggedThread();
		thread.start();
	}
	private boolean InPoint(Point X) {
		if(X.x < 0 || X.x >= NX || X.y < 0 || X.y >= NY) return false;
		return true;
	}
	

	private void    Setint     (Point X, Point Y,     int array[][][], int k) {
		if(!InPoint(X) || !InPoint(Y)) return;
		
		if(X.y == Y.y && X.x < Y.x) array[X.x][X.y][0] = k;
		if(X.y == Y.y && X.x > Y.x) array[Y.x][Y.y][0] = k;

		if(X.x == Y.x && X.y < Y.y) array[X.x][X.y][1] = k;
		if(X.x == Y.x && X.y > Y.y) array[Y.x][Y.y][1] = k;

	}
	private int     Getint     (Point X, Point Y,     int array[][][]) {
		if(!InPoint(X) || !InPoint(Y)) return -1;

		if(X.y == Y.y && X.x < Y.x) return array[X.x][X.y][0];
		if(X.y == Y.y && X.x > Y.x) return array[Y.x][Y.y][0];

		if(X.x == Y.x && X.y < Y.y) return array[X.x][X.y][1];
		if(X.x == Y.x && X.y > Y.y) return array[Y.x][Y.y][1];		
		
		System.out.println("ECData : Getint EROOR");
		return -1;
	}
	private void    Setboolean (Point X, Point Y, boolean array[][][], boolean k) {
		if(!InPoint(X) || !InPoint(Y)) return;
		
		if(X.y == Y.y && X.x < Y.x) array[X.x][X.y][0] = k;
		if(X.y == Y.y && X.x > Y.x) array[Y.x][Y.y][0] = k;

		if(X.x == Y.x && X.y < Y.y) array[X.x][X.y][1] = k;
		if(X.x == Y.x && X.y > Y.y) array[Y.x][Y.y][1] = k;

	}
	private boolean Getboolean (Point X, Point Y, boolean array[][][]) {
		if(!InPoint(X) || !InPoint(Y)) return false;

		if(X.y == Y.y && X.x < Y.x) return array[X.x][X.y][0];
		if(X.y == Y.y && X.x > Y.x) return array[Y.x][Y.y][0];

		if(X.x == Y.x && X.y < Y.y) return array[X.x][X.y][1];
		if(X.x == Y.x && X.y > Y.y) return array[Y.x][Y.y][1];		
		
		System.out.println("ECData : Getboolean EROOR" + X.x + "." + X.y + "/" + Y.x + "." + Y.y);
		return false;
	}
	private void    Setdouble  (Point X, Point Y,  double array[][][], double k) {
		if(!InPoint(X) || !InPoint(Y)) return;
		
		if(X.y == Y.y && X.x < Y.x) array[X.x][X.y][0] = k;
		if(X.y == Y.y && X.x > Y.x) array[Y.x][Y.y][0] = k;

		if(X.x == Y.x && X.y < Y.y) array[X.x][X.y][1] = k;
		if(X.x == Y.x && X.y > Y.y) array[Y.x][Y.y][1] = k;

	}
	private double  Getdouble  (Point X, Point Y,  double array[][][]) {
		if(!InPoint(X) || !InPoint(Y)) return 0.1;

		if(X.y == Y.y && X.x < Y.x) return array[X.x][X.y][0];
		if(X.y == Y.y && X.x > Y.x) return array[Y.x][Y.y][0];

		if(X.x == Y.x && X.y < Y.y) return array[X.x][X.y][1];
		if(X.x == Y.x && X.y > Y.y) return array[Y.x][Y.y][1];		
		
		System.out.println("ECData : Getdouble EROOR" + X.x + "." + X.y + "/" + Y.x + "." + Y.y);
		return 0.1;
	}


	public void    SetConnect(Point X, Point Y, boolean k) {
			   Setboolean(X, Y, IsConnect , k);
			   Setint	 (X, Y, Element[0], 0);
			   Setint	 (X, Y, Element[1], 0);
	}
	public boolean GetConnect(Point X, Point Y) {		
		return Getboolean(X, Y, IsConnect);
	}
	public boolean GetFaceConnect(Point X, Point Y) {
		Point XX = new Point(X.x+1, X.y+1);
		Point YY = new Point(Y.x+1, Y.y+1);
		if(X.x + X.y < Y.x + Y.y)
			return !GetConnect(XX, Y);
		else
			return !GetConnect(YY, X);
	}
	
	public void    SetSelect (Point X, Point Y, boolean k) {
			   Setboolean(X, Y, IsSelect, k);
	}
	public boolean GetSelect (Point X, Point Y) {		
		return Getboolean(X, Y, IsSelect);
	}
	public void    SetCurrent(Point X, Point Y,  double k) {
		if(!InPoint(X) || !InPoint(Y)) return;
		
		if(X.y == Y.y && X.x < Y.x) Setdouble(X, Y, Current, +k);
		if(X.y == Y.y && X.x > Y.x) Setdouble(X, Y, Current, -k);

		if(X.x == Y.x && X.y < Y.y) Setdouble(X, Y, Current, +k);
		if(X.x == Y.x && X.y > Y.y) Setdouble(X, Y, Current, -k);
	}
	public double  GetCurrent(Point X, Point Y) {
		if(!InPoint(X) || !InPoint(Y)) return 0.1;
		
		if(X.y == Y.y && X.x < Y.x) return +Getdouble(X, Y, Current);
		if(X.y == Y.y && X.x > Y.x) return -Getdouble(X, Y, Current);

		if(X.x == Y.x && X.y < Y.y) return +Getdouble(X, Y, Current);
		if(X.x == Y.x && X.y > Y.y) return -Getdouble(X, Y, Current);
		
		System.out.println("ECData : GetCurrent EROOR" + X.x + "." + X.y + "/" + Y.x + "." + Y.y);
		return 0.1;
	}
	
	
	public void SetButtonClicked(int k) {
		ButtonClicked = k;
	}
	public  int GetButtonClicked(){
		return ButtonClicked;
	}
	
	public void Select(Point X) {
		for(int i=0; i<NX; i++) for(int j=0; j<NY; j++) {
			for(int k=0; k<2; k++)
				IsSelect [i][j][k] = false;
			Check[i][j] = false;
		}
		
		int[] CX = {0, 1, 0, -1};
		int[] CY = {1, 0, -1, 0};
		
		Vector<Point> Q = new Vector<Point>();
		
		Q.add(X); Check[X.x][X.y] = true;
		int r = 0, f = -1;
		while(f < r) {
			Point now = Q.get(++f);
			for(int i=0; i<4; i++) {
				Point next = new Point(now.x + CX[i], now.y + CY[i]);
				if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
				if(GetConnect(now, next) == false) continue;
				SetSelect(now, next, true);
				if(Check[next.x][next.y] == true) continue;
				
				Check[next.x][next.y] = true;
				Q.add(next);
				r++;
			}
		}
	}

	public void  SetRecodeTransform(Point X) {
		RecodeTransform = X;
	}
	public Point GetRecodeTransform() {
		return RecodeTransform;
	}
	
	public void SetElementValue(Point X, Point Y, int element, int value) {
		SetConnect(X, Y, true);
		
		Setint(X, Y, Element[0], element);
		if(GetElement(X, Y) == this.RESISTANCE) 
			Setint(X, Y, Element[1], value  );

		if(GetElement(X, Y) == this.BATTERY   ) {
			if(X.y == Y.y && X.x < Y.x) Setint(X, Y, Element[1], -value  );
			if(X.y == Y.y && X.x > Y.x) Setint(X, Y, Element[1], +value  );
	
			if(X.x == Y.x && X.y < Y.y) Setint(X, Y, Element[1], -value  );
			if(X.x == Y.x && X.y > Y.y) Setint(X, Y, Element[1], +value  );
		}

	}
	public int  GetElement(Point X, Point Y) {
		return Getint(X, Y, Element[0]);
	}
	public int  GetValue  (Point X, Point Y) {
		if(GetElement(X, Y) == this.RESISTANCE) return Getint(X, Y, Element[1]);
		if(GetElement(X, Y) == this.BATTERY   ) {
			if(X.y == Y.y && X.x < Y.x) return +Getint(X, Y, Element[1]);
			if(X.y == Y.y && X.x > Y.x) return -Getint(X, Y, Element[1]);
	
			if(X.x == Y.x && X.y < Y.y) return +Getint(X, Y, Element[1]);
			if(X.x == Y.x && X.y > Y.y) return -Getint(X, Y, Element[1]);
		}
		return -1;
	}
	
	public void CleanCurcuit() {
		int[] CX = {0, 1, 0, -1};
		int[] CY = {1, 0, -1, 0};

		Vector<Point> Q = new Vector<Point>();
		int r = -1, f = -1;

		Point now = new Point();
		for(int i=0; i<NX; i++) for(int j=0; j<NY; j++) {
			now.x = i; now.y = j;
			
			int cnt = 0;
			for(int k=0; k<4; k++) {
				Point next = new Point(now.x + CX[k], now.y + CY[k]);
				if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
				if(GetConnect(now, next) == true)
					cnt++;
			}
			
			if(cnt == 1) {
				Q.add(new Point(now.x, now.y));
				r++;
			}
		}
						
		while(f < r) {
			now = Q.get(++f);
			for(int i=0; i<4; i++) {
				Point next = new Point(now.x + CX[i], now.y + CY[i]);
				System.out.println("(" + now.x + "/" + now.y + "/" + next.x + "/" + next.y + ")");
				if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
				if(GetConnect(now, next) == false) continue;
				SetConnect(now, next, false);

				int cnt = 0;
				for(int k=0; k<4; k++) {
					Point nnext = new Point(next.x + CX[k], next.y + CY[k]);
					if(!InPoint(nnext)) continue;
					if(GetConnect(next, nnext) == true)
						cnt++;
				}

				if(cnt == 1) {
					Q.add(next);
					r++;
				}
				
			}
		}
	}

	public Color GetColor(int type) {
		if(type == this.RESISTANCE)
			return this.Color_Resistance;
		if(type == this.BATTERY)
			return this.Color_Battery;
		
		System.out.println("ECData : GetColor ERROR");
		return new Color(0, 0, 0);
	}

	public void  SetGroundPoint(Point now) {
		int[] CX = {0, 1, 0, -1};
		int[] CY = {1, 0, -1, 0};
		
		int cnt = 0;
		for(int k=0; k<4; k++) {
			Point next = new Point(now.x + CX[k], now.y + CY[k]);
			if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
			if(GetConnect(now, next) == true)
				cnt++;
		}
		
		if(cnt >= 2) {
			GroundP.x = now.x;
			GroundP.y = now.y;
		}
		
//		GroundP = (Point)X.clone();
	}
	
	public void Delete() {

		int[] CX = {1, 0};
		int[] CY = {0, 1};
		for(int i=0; i<NX; i++)
			for(int j=0; j<NY; j++) {
				for(int k=0; k<2; k++) {
					Point now  = new Point(i		, j			);
					Point next = new Point(i + CX[k], j + CY[k] );

					if(GetSelect (now, next) == false) continue;

					SetElementValue(now, next, 0, 0);
					SetConnect(now, next, false);
					SetSelect (now, next, false);
					System.out.println(i + ":" + j);
				}
			}
	}
	
	
	public void ConservationLaw() {
		for(int i=0; i<NX; i++) {
			for(int j=0; j<NY; j++) {
				int[] CX = {0, 1, 0, -1};
				int[] CY = {1, 0, -1, 0};
				
				Equation eq = new Equation(NX, NY);
				
				Point now = new Point(i, j);
				for(int k=0; k<4; k++) {
					Point next = new Point(now.x + CX[k], now.y + CY[k]);
					if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
					if(GetConnect(now, next) == true)
						eq.SetCoefficient(now, next, 1);
				}
				
				eq.SetAnswer(0);
				
				
				GE.AddEqution(eq);
				
			}
		}
	}
	
	public void FindCycle() {
		for(int i=0; i<NX; i++) for(int j=0; j<NY; j++)
			Check[i][j] = false;

		int[] CX = {0, 1, 0, -1};
		int[] CY = {1, 0, -1, 0};

		
		for(int i=0; i<NX; i++) for(int j=0; j<NY; j++) {
			if(Check[i][j] == true) continue;
			
			Vector<Point> Q = new Vector<Point>();
			int r = -1, f = -1;
			
			Q.add(new Point(i, j));
			Check[i][j] = true;
			r++;
			
			while(f < r) {
				Point now = Q.get(++f);
				
				for(int k=0; k<4; k++) {
					Point next = new Point(now.x + CX[k], now.y + CY[k]);
					if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
					if(Check[next.x][next.y] == true) continue;
					if(GetFaceConnect(now, next) == true) {
						Q.add(next);
						Check[next.x][next.y] = true;
						r++;
					}
					
				}
			}
			
			int[] ClockX = {0, 1, 1, 0, 0};
			int[] ClockY = {0, 0, 1, 1, 0};

			Equation eq = new Equation(NX, NY);
			for(int q=0; q<Q.size(); q++) {
				Point leftup = Q.get(q);
				
				for(int k=0; k<4; k++) {
					Point now  = new Point(leftup.x + ClockX[k  ], leftup.y + ClockY[k  ]);
					Point next = new Point(leftup.x + ClockX[k+1], leftup.y + ClockY[k+1]);
					

					if(GetElement(now, next) == this.RESISTANCE)
						eq.SetCoefficient(now, next, 1);
					if(GetElement(now, next) == this.BATTERY)
						eq.SetAnswer(eq.GetAnswer() + GetValue(now, next));
				}
			}
			GE.AddEqution(eq);

		}
	}
	
	public void Kirchhoff(Point now, Equation eq, Point past) {
		System.out.println(now.x + "/" +now.y);
		
		int[] CX = {0, 1, 0, -1};
		int[] CY = {1, 0, -1, 0};

		Check[now.x][now.y] = true;

		for(int k=0; k<4; k++) {
			Point next = new Point(now.x + CX[k], now.y + CY[k]);
			if(next.x < 0 || next.x >= NX || next.y < 0 || next.y >= NY) continue;
			if(GetConnect(now, next) == false) continue;
			
			if(past.x != GroundP.x || past.y != GroundP.y)
				if(next.x == GroundP.x && next.y == GroundP.y) {
					if(GetElement(now, next) == this.RESISTANCE)
						eq.SetCoefficient(now, next, 1);
					if(GetElement(now, next) == this.BATTERY)
						eq.SetAnswer(eq.GetAnswer() + GetValue(now, next));

					for(int i=0; i<NX*NY*2+1; i++)
						System.out.print(eq.Coefficient[i]);
					System.out.println("");
					GE.AddEqution(eq);
					

					if(GetElement(now, next) == this.RESISTANCE)
						eq.SetCoefficient(now, next, 0);
					if(GetElement(now, next) == this.BATTERY)
						eq.SetAnswer(eq.GetAnswer() - GetValue(now, next));
				}
			
			if(Check[next.x][next.y] == true) continue;
			
			if(GetElement(now, next) == this.RESISTANCE)
				eq.SetCoefficient(now, next, 1);
			if(GetElement(now, next) == this.BATTERY)
				eq.SetAnswer(eq.GetAnswer() + GetValue(now, next));
			
			
			Kirchhoff(next, eq, now);

			if(GetElement(now, next) == this.RESISTANCE)
				eq.SetCoefficient(now, next, 0);
			if(GetElement(now, next) == this.BATTERY)
				eq.SetAnswer(eq.GetAnswer() - GetValue(now, next));
		}
		
		Check[now.x][now.y] = false;
	}
	
	public void  CalculateCurrent() {
//		if(GroundP.x == -1 && GroundP.y == -1) return;
		CalculateThread TimeCheck = new CalculateThread();
		
		IsCalculate = true;
		TimeCheck.start();
		try {
			TimeCheck.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IsCalculate = false;
	}
	

	class CalculateThread extends Thread{
		
		public void run() {
			Percent		= 0;

			PercentThread Inc1 = new PercentThread(25);
			Inc1.start(); 

			GE.DeleteAllEqution();
			
			PercentThread Inc2 = new PercentThread(50);
			Inc2.start(); 

			
			ConservationLaw();

			PercentThread Inc3 = new PercentThread(75);
			Inc3.start(); 

			FindCycle();
			
			PercentThread Inc4 = new PercentThread(100);
			Inc4.start(); 

			GE.Elimination();
			
			while(Inc4.isAlive()) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			IsCalculate = false;
		}
		
	}
	
	class PercentThread extends Thread{
		int max;
		PercentThread(int max) {
			this.max = max;
		}
		public void run() {
			while(true) {
				Percent++;
				if(Percent >= max) break;
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	class DraggedThread extends Thread{
		public void run() {
			while(true) {
				if(IsDrag == true) {
					int[] CX = {1, 0};
					int[] CY = {0, 1};
					for(int i=0; i<NX; i++)
						for(int j=0; j<NY; j++) {
							for(int k=0; k<2; k++) {
								Point now  = new Point(i		, j			);
								Point next = new Point(i + CX[k], j + CY[k] );
								
								if(GetConnect(now, next) == false) continue;
								
								double i0 = (double)i;
								double j0 = (double)j;
								double i1 = (double)i + (double)CX[k];
								double j1 = (double)j + (double)CY[k];
								
								double max_X = Math.max(RecodeDragStart.x, RecodeDragEnd.x);
								double min_X = Math.min(RecodeDragStart.x, RecodeDragEnd.x);
								double max_Y = Math.max(RecodeDragStart.y, RecodeDragEnd.y);
								double min_Y = Math.min(RecodeDragStart.y, RecodeDragEnd.y);
								
								if(min_X <= i0 && i0 <= max_X && min_Y <= j0 && j0 <= max_Y) {
									SetSelect(now, next, true);
									continue;
								}
								if(min_X <= i1 && i1 <= max_X && min_Y <= j1 && j1 <= max_Y) {
									SetSelect(now, next, true);
									continue;
								}
								if(k == 0) {
									if(min_X >= i0 && max_X <= i1 && min_Y <= j0 && max_Y >= j0) {
										SetSelect(now, next, true);
										continue;
									}										
								}
								if(k == 1) {
									if(min_X <= i0 && max_X >= i0 && min_Y >= j0 && max_Y <= j1) {
										SetSelect(now, next, true);
										continue;
									}																			
								}
								SetSelect(now, next, false);
								continue;
							}
						}
				}
				
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
	
	
	
}
