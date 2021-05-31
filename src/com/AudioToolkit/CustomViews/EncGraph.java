package com.AudioToolkit.CustomViews;

import com.AudioToolkit.Objects.Enclosure;
import com.AudioToolkit.Objects.Subwoofer;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class EncGraph {



	double Sd,
			Bl,
			Re,
			Le,
			Fs,
			Mms,
			Cms,
			Qms,
			Qabca,
			Vbca_phys,
			Vas,
			Qlbca,
			Rs_i,
			Pn_i,
			Lco1 = 0.000000001,	
			Rco1 = 0.000001, 
			Cco1 = 0.000000000001,
			Lco2 = 0.000000001,
			Rco2 = 0.000001,
			Cco2 = 0.000000000001,
			Lee2 = 0.000000001,
			Ree2 = 0.000001,
			Ro = 1.18,
			P0 = 0.00002,
			Cair = 345,
			Cco1a = Math.pow(Lco1*(Sd/Bl),2),
			Rco1a =Math.pow(Bl/Sd,2)/Rco1,
			Mco1a = Cco1*Math.pow(Bl/Sd,2), 
			Cco2a = Lco2*Math.pow(Sd/Bl,2), 
			Rco2a = Math.pow(Bl/Sd,2)/Rco2, 
			Mco2a = Cco2*Math.pow(Bl/Sd,2), 
			Rea = Math.pow(Bl/Sd,2)/Re, 
			Cea = Le*Math.pow(Sd/Bl,2), 
			Cee2a = Lee2*Math.pow(Sd/Bl,2), 
			Ree2a = Math.pow(Bl/Sd,2)/Ree2, 
			Mas = Mms/Math.pow(Sd,2), 
			Cas = Cms*Math.pow(Sd,2), 
			Ras = 2*Math.PI*Fs*Mas/Qms, 
			Gammaca = 1+0.9/Math.pow(Qabca,0.9),
			Vbca_i = Vbca_phys*Gammaca,
			l_2_m3 = 0.001,
			Vbca = Vbca_i*l_2_m3,
			Cbaca = Vbca/Math.pow(Ro*Cair,2), 
			Fbca0 = Cair*Sd/(2*Math.PI)*Math.sqrt(Ro*(Vbca+Vas)/(Vbca*Vas*Mms)),
			Raaca = 1/(2*Math.PI*Fbca0*Cbaca*Qabca), 
			Rlaca = Qlbca/(2*Math.PI*Fbca0*Cbaca), 
			Rs = Rs_i+0.000001,
			Rsa = Math.pow(Bl/Sd,2)/Rs, 
			DUcPn = 1,
			Pn = Pn_i*DUcPn,
			Vga = Math.sqrt(Pn*(Re+Rs))*Sd/Bl*Rsa, 
			Re_res = 0, 
			Im_res = 0,
			Zchar = 0, 
			Kshort = 1, 
			Rfuncx = 2;
	double Temp1, Temps;
	double[][] SPL_array = new double[500][2];
	double[] Range_SPL, Range_Phase, Freq = new double[500],V = new double[51];
	int[] P = new int[51],Q = new int[51], T = new int[51];
	int H, Nn, i, j;

	public EncGraph(double Sd, double Bl, double Re, double Le, double Fs, double Mms, double Cms,
			double Qms, double Qabca, double Vbca_phys, double Vas, double Qlbca, double Rs_i, double Pn_i) {
		
		this.Sd = Sd;
		this.Bl = Bl;
		this.Re = Re;
		this.Le = Le;
		this.Fs = Fs;
		this.Mms = Mms;
		this.Cms = Cms;
		this.Qms = Qms;
		this.Qabca = Qabca; //Absorbtion, Qa
		this.Vbca_phys = Vbca_phys; //Physical VB
		this.Vas = Vas;
		this.Qlbca = Qlbca; //Leakage QI
		this.Rs_i = Rs_i;
		this.Pn_i = Pn_i;	//Nominal power
		
	}
	
	public EncGraph(Subwoofer sub, Enclosure enc) {
		
		
		
		
		
		
		
		
	}
	
	
	
	
	public GraphViewData[] genSealed() {
		System.out.println("Start the process");
		//Setup
		i = 1;
		P[i] = 1; Q[i] = 0; V[i] = Cco1a; T[i] = 3;
		i++;
		P[i] = 1; Q[i] = 0; V[i] = Rco1a; T[i] = 0;
		i++;
		P[i] = 1; Q[i] = 2; V[i] = Mco1a; T[i] = 2;
		i++;
		P[i] = 2; Q[i] = 0; V[i] = Cco2a; T[i] = 3;
		i++;
		P[i] = 2; Q[i] = 0; V[i] = Rco2a; T[i] = 0;
		i++;
		P[i] = 2; Q[i] = 3; V[i] = Mco2a; T[i] = 2;
		i++;
		P[i] = 3; Q[i] = 0; V[i] = Rea; T[i] = 0;
		i++;
		P[i] = 3; Q[i] = 0; V[i] = Cea; T[i] = 3;
		i++;
		P[i] = 3; Q[i] = 4; V[i] = Cee2a; T[i] = 3;
		i++;
		P[i] = 4; Q[i] = 0; V[i] = Ree2a; T[i] = 0;
		i++;
		P[i] = 3; Q[i] = 5; V[i] = Mas; T[i] = 2;
		i++;
		P[i] = 5; Q[i] = 6; V[i] = Cas; T[i] = 3;
		i++;
		P[i] = 6; Q[i] = 7; V[i] = Ras; T[i] = 0;    
		i++;
		P[i] = 7; Q[i] = 8; V[i] = Cbaca; T[i] = 3;
		i++;
		P[i] = 8; Q[i] = 0; V[i] = Raaca; T[i] = 0;
		i++;
		P[i] = 7; Q[i] = 0; V[i] = Rlaca; T[i] = 0;      
		i++;
		P[i] = 0; Q[i] = 9; V[i] = 1 / Raaca; P[i] = 1000 * P[i] + 8; Q[i] = 1000 * Q[i] + 0; T[i] = 4;
		i++;
		P[i] = 0; Q[i] = 1; V[i] = Rsa; T[i] = 0;
		i++;
		P[i] = 0; Q[i] = 9; V[i] = 1; T[i] = 0;

		//No of components - 1
		H = i;

		//Highest Node No.
		Nn = 9;

		Freq[0] = 10.0000000000000;

		for (i = 1; i < Freq.length; i++) {
			Freq[i] = Freq[i-1]*1.00925288608;
			//System.out.println(Freq[i]);
		}

		Temp1 = Ro / P0 * Vga;

		for (int i=0; i < Freq.length; i++) {

			calnet(Freq[i]);

			Temps = Re_res;
			Re_res = -Im_res * Freq[i] * Temp1;
			Im_res = Temps * Freq[i] * Temp1;
			SPL_array[i][0] = Freq[i];
			SPL_array[i][1] = 20 * Math.log(Math.sqrt((Math.pow(Re_res,2) + Math.pow(Im_res,2)))) / Math.log(10);
			System.out.println("Freq: " + SPL_array[i][0] + " at " + SPL_array[i][1] + " dBs, Re_res: " + Re_res + ", Im_res: " + Im_res );
		}

		GraphViewData[] graphData = new GraphViewData[SPL_array[0].length];
		for (int i = 0; i < SPL_array[0].length; i++) {
			graphData[i] = new GraphViewData(SPL_array[i][0], SPL_array[i][1]);
		}		
		return graphData;
	}

	public void calnet(double Freq) {

		//Module variables		
		int k, l, Ai, Bi, Ci, Di, Ei, Fi, Pi, Qi;
		int[] D = new int[4];
		double Aval, Bval, Cval, Dval, Pval, Qval, Yval, Vval, W, Ltl, Ptl, Qtl;
		double[][] B = new double[50][51], G = new double[50][51];

		//Clear Error Flag
		//ErrorFlag = 0;

		//Clear Arrays
		
		for (i = 0; i <= Nn; i++) {
			for (j = 0; j <= Nn + 1; j++) {
				G[i][j] = 0;
				B[i][j] = 0;
			}
		}

		W = 6.28318531 * Freq;
		G[P[H]][Nn + 1] = -1 / V[H];
		G[Q[H]][Nn + 1] = 1 / V[H];

		for (i = 1; i <= H + 1; i++) {
			Pi = P[i];
			Qi = Q[i];
			if (T[i] == 0) {
				Vval = 1 / V[i];
				G[Pi][Qi] = G[Pi][Qi] - Vval;
				G[Qi][Pi] = G[Qi][Pi] - Vval;
				G[Pi][Pi] = G[Pi][Pi] + Vval;
				G[Qi][Qi] = G[Qi][Qi] + Vval;
			} else if (T[i] == 1) {
				Vval = 1 / V[i];
				Aval = 1 / (V[Pi] * V[Qi] * Vval);
				Aval = -Aval / (W * (1 - Aval / Vval));
				Bval = Aval / (V[Pi] * Vval);
				Cval = Aval / (V[Qi] * Vval);
				D[1] = P[Pi];
				D[2] = Q[Pi];
				D[3] = P[Qi];
				D[4] = Q[Qi];
				k = 1;
				for (l = 0; l <= 1; l++) {
					for (int m = 0; m <= 1; m++) {
						Pi = D[1 + l];
						Qi = D[1 + m];
						Ei = D[3 + l];
						Fi = D[3 + m];
						B[Pi][Qi] = B[Pi][Qi] + k * Bval;
						B[Pi][Fi] = B[Pi][Fi] - k * Aval;
						B[Ei][Qi] = B[Ei][Qi] - k * Aval;
						B[Ei][Fi] = B[Ei][Fi] + k * Cval;
						k = -k;
					}
					k = -k;
				}
			} else if (T[i] == 2) {
				Vval = 1 / V[i];
				Yval = -Vval / W;
				B[Pi][Qi] = B[Pi][Qi] - Yval;
				B[Qi][Pi] = B[Qi][Pi] - Yval;
				B[Qi][Qi] = B[Qi][Qi] + Yval;
				B[Pi][Pi] = B[Pi][Pi] + Yval;
			} else if (T[i] == 3) {
				Vval = V[i];
				Yval = W * Vval;
				B[Pi][Qi] = B[Pi][Qi] - Yval;
				B[Qi][Pi] = B[Qi][Pi] - Yval;
				B[Qi][Qi] = B[Qi][Qi] + Yval;
				B[Pi][Pi] = B[Pi][Pi] + Yval;
			} else if (T[i] == 4) {
				Vval = V[i];
				Ai = Pi / 1000;
				Bi = Pi - 1000 * Ai;
				Ci = Qi / 1000;
				Di = Qi - 1000 * Ci;
				G[Ai][Bi] = G[Ai][Bi] + Vval;
				G[Ci][Bi] = G[Ci][Bi] - Vval;
				G[Ai][Di] = G[Ai][Di] - Vval;
				G[Ci][Di] = G[Ci][Di] + Vval;
			} else if (T[i] == 5) {
				Vval = V[i];
				Ltl = W * Vval / Cair / Kshort;
				Ptl = -1 / (Zchar * Math.sin(Ltl));
				Qtl = Math.tan(Ltl / 2) / Zchar;
				B[Pi][Pi] = B[Pi][Pi] + Ptl + Qtl;
				B[Pi][Qi] = B[Pi][Qi] - Ptl;
				B[Qi][Pi] = B[Qi][Pi] - Ptl;
				B[Qi][Qi] = B[Qi][Qi] + Ptl + Qtl;
			} else if (T[i] == 6) {
				Vval = 1 / Math.pow(V[i] * Freq,Rfuncx);
				G[Pi][Qi] = G[Pi][Qi] - Vval;
				G[Qi][Pi] = G[Qi][Pi] - Vval;
				G[Pi][Pi] = G[Pi][Pi] + Vval;
				G[Qi][Qi] = G[Qi][Qi] + Vval;
			} else {
				//Dummy = MsgBox["General Circuit Error!", vbOKOnly, "UniBox"]
				//ErrorFlag = True
				//Exit Sub
			}
		}

		//Solving Equations
		for(k = 1; k <= Nn - 1; k++) {
			//System.out.println("Do equation");
			//Pivot Routine
			l = k;
			for (i = k + 1; i <= Nn; i++) {
				Vval = Math.pow(G[i][k],2) + Math.pow(B[i][k],2);
				if (Vval >= 10 * (Math.pow(G[l][k],2) + Math.pow(B[l][k],2)))  l = i;
			}

			if (l != k) {
				for(j = k; j <= Nn + 1; j++) {
					Pval = G[k][j];
					G[k][j] = G[l][j];
					G[l][j] = Pval;
					Pval = B[k][j];
					B[k][j] = B[l][j];
					B[l][j] = Pval;
				}
			}

			Dval = 1 / (Math.pow(G[k][k],2) + Math.pow(B[k][k],2));

			for (i = k + 1; i <= Nn; i++) {
				if (G[i][k] != 0 || B[i][k] != 0) {
					Aval = (G[i][k] * G[k][k] + B[i][k] * B[k][k]) * Dval;
					Bval = (B[i][k] * G[k][k] - G[i][k] * B[k][k]) * Dval;

					for (j = k + 1; j <= Nn + 1; j++) {
						G[i][j] = G[i][j] - Aval * G[k][j] + Bval * B[k][j];
						B[i][j] = B[i][j] - Bval * G[k][j] - Aval * B[k][j];
					}
				}
			}
		}

		G[0][0] = 0;
		B[0][0] = 0;

		for (i = Nn; i >= 1; i--) {
			//System.out.println("Do eqs: " + i);
			Dval = 1 / (Math.pow(G[i][i],2) + Math.pow(B[i][i],2));
			Aval = G[i][i] * Dval;
			Bval = -B[i][i] * Dval;
			Pval = 0; Qval = 0;

			if (i + 1 <= Nn) {
				for (j = i + 1; j <= Nn; j++) {
					Pval = G[i][j] * G[0][j] - B[i][j] * B[0][j] + Pval;
					Qval = G[i][j] * B[0][j] + B[i][j] * G[0][j] + Qval;
				}
			}

			Pval = G[i][Nn + 1] - Pval;
			Qval = B[i][Nn + 1] - Qval;

			G[0][i] = Pval * Aval - Qval * Bval;
			B[0][i] = Pval * Bval + Qval * Aval;
		}

		Re_res = G[0][Q[H + 1]] - G[0][P[H + 1]];
		Im_res = B[0][Q[H + 1]] - B[0][P[H + 1]];
		//System.out.println("done calnet");
		//System.out.println("Re_res: " + Re_res + ", Im_res: " + Im_res );
	}
}
