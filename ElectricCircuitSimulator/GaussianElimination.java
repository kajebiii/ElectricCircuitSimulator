package ElectricCircuitSimulator;

import java.awt.Point;

public class GaussianElimination {
	int NX, NY;

	Equation[] EQs;
	int Size;
	
	double[] Ans;
	
	GaussianElimination(int NX, int NY) {
		this.NX = NX;
		this.NY = NY;
		EQs = new Equation[NX*NY*3];
		
		Ans = new double[NX*NY*3];
		for(int i=0; i<NX*NY*2; i++)
			Ans[i] = 0;
		
		Size = 0;
	}
		
	void DeleteAllEqution() {
		Size = 0;
	}
	
	void AddEqution(Equation eq) {
		EQs[Size] = new Equation(eq);
		Size++;
	}
	
	void Elimination() {
		/*
		for(int i=0; i<Size; i++) {
			for(int ii=1; ii<NX; ii++) for(int jj=1; jj<NY; jj++) for(int k=0; k<2; k++) {
				int j = ii * NY * 2 + jj * 2 + k;
				System.out.print(EQs[i].Coefficient[j] + " ");
			}
			System.out.print(EQs[i].GetAnswer());
			System.out.println("");
		}
		System.out.println("ELIMINATION");
		*/
		
		int rank = 0;
		for(int i=0; i<NX*NY*2+1; i++) {
			int index = -1;
			for(int j=rank; j<Size; j++) {
				if(Math.abs(EQs[j].Coefficient[i]) != 0.0 ) {
					index = j;
					break;
				}
			}
			if(index == -1) continue;
			
			for(int j=0; j<Size; j++) {
				if(index == j) continue;
				
				double ratio = EQs[j].Coefficient[i] / EQs[index].Coefficient[i];
				for(int k=0; k<NX*NY*2+1; k++)
					EQs[j].Coefficient[k] -= EQs[index].Coefficient[k] * ratio;
				for(int k=0; k<NX*NY*2+1; k++)
					if(Math.abs(EQs[j].Coefficient[k]) < 1e-5 )
						EQs[j].Coefficient[k] = 0.0;

			}
			
			Equation temp = new Equation(EQs[rank]);
			for(int k=0; k<NX*NY*2+1; k++)
				EQs[rank ].Coefficient[k] = EQs[index].Coefficient[k];
			for(int k=0; k<NX*NY*2+1; k++)
				EQs[index].Coefficient[k] = 	  temp.Coefficient[k];
			
			rank++;
			
			/*
			for(int o=0; o<Size; o++) {
				for(int ii=1; ii<NX; ii++) for(int jj=1; jj<NY; jj++) for(int k=0; k<2; k++) {
					int j = ii * NY * 2 + jj * 2 + k;
					System.out.print(EQs[o].Coefficient[j] + " ");
				}
				System.out.print(EQs[o].GetAnswer());
				System.out.println("");
			}
			System.out.println("FF");
			*/
			

		}
		
		for(int i=0; i<NX*NY*2; i++)
			Ans[i] = 0;
		for(int i=0; i<rank; i++) {
			for(int j=0; j<NX*NY*2; j++)
				if(EQs[i].Coefficient[j] != 0) {
					Ans[j] = EQs[i].GetAnswer() / EQs[i].Coefficient[j];
					break;
				}
		}
		
		/*
		for(int i=0; i<Size; i++) {
			for(int ii=1; ii<NX; ii++) for(int jj=1; jj<NY; jj++) for(int k=0; k<2; k++) {
				int j = ii * NY * 2 + jj * 2 + k;
				System.out.print(EQs[i].Coefficient[j] + " ");
			}
			System.out.print(EQs[i].GetAnswer());
			System.out.println("");
		}
		System.out.println("FF");
		*/

	}
	
	

	private boolean InPoint(Point X) {
		if(X.x < 0 || X.x >= NX || X.y < 0 || X.y >= NY) return false;
		return true;
	}
	int ChangeInt(int X, int Y, int Z) {
		return X * NY * 2 + Y * 2 + Z;
	}
	int ChangeXYZ(Point X, Point Y) {
		if(!InPoint(X) || !InPoint(Y)) return -1;
		
		if(X.y == Y.y && X.x < Y.x) return ChangeInt(X.x, X.y, 0);
		if(X.y == Y.y && X.x > Y.x) return ChangeInt(Y.x, Y.y, 0);

		if(X.x == Y.x && X.y < Y.y) return ChangeInt(X.x, X.y, 1);
		if(X.x == Y.x && X.y > Y.y) return ChangeInt(Y.x, Y.y, 1);
		
		return -1;
	}
	double GetAns(Point X, Point Y) {
		if(!InPoint(X) || !InPoint(Y)) return 0;
		
		if(X.y == Y.y && X.x < Y.x) return +Ans[ChangeXYZ(X, Y)];
		if(X.y == Y.y && X.x > Y.x) return -Ans[ChangeXYZ(X, Y)];

		if(X.x == Y.x && X.y < Y.y) return +Ans[ChangeXYZ(X, Y)];
		if(X.x == Y.x && X.y > Y.y) return -Ans[ChangeXYZ(X, Y)];
		
		return 0;
	}
	
}

class Equation{
	double[] Coefficient;
	int NX, NY;
	
	
	Equation(int NX, int NY) {
		this.NX = NX;
		this.NY = NY;
		Coefficient = new double[NX*NY*2+1];
		for(int i=0; i<NX*NY*2; i++)
			Coefficient[i] = 0;
	}

	Equation(Equation eq) {
		this.NX = eq.NX;
		this.NY = eq.NY;
		Coefficient = new double[NX*NY*2+1];
		for(int i=0; i<NX*NY*2+1; i++)
			Coefficient[i] = eq.Coefficient[i];
	}
	

	private boolean InPoint(Point X) {
		if(X.x < 0 || X.x >= NX || X.y < 0 || X.y >= NY) return false;
		return true;
	}
	
	int ChangeInt(int X, int Y, int Z) {
		return X * NY * 2 + Y * 2 + Z;
	}
	
	int ChangeXYZ(Point X, Point Y) {
		if(!InPoint(X) || !InPoint(Y)) return -1;
		
		if(X.y == Y.y && X.x < Y.x) return ChangeInt(X.x, X.y, 0);
		if(X.y == Y.y && X.x > Y.x) return ChangeInt(Y.x, Y.y, 0);

		if(X.x == Y.x && X.y < Y.y) return ChangeInt(X.x, X.y, 1);
		if(X.x == Y.x && X.y > Y.y) return ChangeInt(Y.x, Y.y, 1);
		
		return -1;
	}
	
	void SetCoefficient(Point X, Point Y, double k) {
		if(!InPoint(X) || !InPoint(Y)) return;
		
		if(X.y == Y.y && X.x < Y.x) Coefficient[ChangeXYZ(X, Y)] = +k;
		if(X.y == Y.y && X.x > Y.x) Coefficient[ChangeXYZ(X, Y)] = -k;

		if(X.x == Y.x && X.y < Y.y) Coefficient[ChangeXYZ(X, Y)] = +k;
		if(X.x == Y.x && X.y > Y.y) Coefficient[ChangeXYZ(X, Y)] = -k;

	}
	
	void SetAnswer(double k) {
		Coefficient[NX*NY*2] = k;
	}
	double GetAnswer() {
		return Coefficient[NX*NY*2];
	}
	
	double GetCoefficient(Point X, Point Y) {
		if(!InPoint(X) || !InPoint(Y)) return -1;
		
		if(X.y == Y.y && X.x < Y.x) return +Coefficient[ChangeXYZ(X, Y)];
		if(X.y == Y.y && X.x > Y.x) return -Coefficient[ChangeXYZ(X, Y)];

		if(X.x == Y.x && X.y < Y.y) return +Coefficient[ChangeXYZ(X, Y)];
		if(X.x == Y.x && X.y > Y.y) return -Coefficient[ChangeXYZ(X, Y)];
		
		return -1;
	}
}
