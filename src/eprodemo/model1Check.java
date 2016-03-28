package eprodemo;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import util.ConnectR;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class model1Check {

	public static List<Integer> Items = new LinkedList<>();
	
	public static double[] BS(double Gamma, ConnectR connection) throws IOException, REXPMismatchException, REngineException {
        double OV = 0;
		double[] PCV = {0,0,0,0,0};
        try {
			Items.clear();

			IloCplex cplex = new IloCplex();
			//cplex.setOut(null);

			int n = 2;
			double theta = 0.00001;
			double a[] = {2,1};
			double b[] = {1,2};
			double c[] = {1,1.01};
			double d[] = {0.25,0.25};
			//
			//IloNumVar[] x = cplex.boolVarArray(n);
			IloNumVar[] x = cplex.numVarArray(n, 0, Double.MAX_VALUE);
			IloNumVar[] p = cplex.numVarArray(n,0,Double.MAX_VALUE);

			IloNumVar zz = cplex.numVar(0,Double.MAX_VALUE);
			//objective
			IloLinearNumExpr obj = cplex.linearNumExpr();

			for(int i=0; i<n; i++) {
				//obj.addTerm(1,x[i]);

			}
			obj.addTerm(-1,x[0]);
			cplex.addMaximize(obj);



			//constraints
			//0
			IloLinearNumExpr constraint, constraint1;
			constraint = cplex.linearNumExpr();
			for(int i=0; i<n; i++) {
				constraint.addTerm(1,x[i]);


			}
			cplex.addGe(constraint,2.212389380530974);
			//1
			constraint = cplex.linearNumExpr();


			for(int i=0; i<n; i++)
			{
				constraint.addTerm(c[i], x[i]);
				constraint.addTerm(1,p[i]);
			}
			constraint.addTerm(Gamma,zz);
			cplex.addLe(constraint, 2.5);

			for(int i=0; i<n; i++)
			{
				constraint = cplex.linearNumExpr();
				constraint.addTerm(1, p[i]);
				constraint.addTerm(-d[i],x[i]);
				constraint.addTerm(1,zz);
				cplex.addGe(constraint,0);
			}

			//5
			constraint = cplex.linearNumExpr();
			for(int i=0; i<n; i++)
			{
				constraint.addTerm(b[i], x[i]);
			}
			cplex.addLe(constraint, 4);

			constraint = cplex.linearNumExpr();
			for(int i=0; i<n; i++)
			{
				constraint.addTerm(a[i], x[i]);
			}
			cplex.addLe(constraint, 4);


			//5


			//solve
			int length = 0;
			if(cplex.solve())
			//if(cplex.populate())
			{
				for(int i=0; i<n; i++) {
					System.out.println("x" + i + "=" + cplex.getValue(x[i]));
					if (cplex.getValue(x[i]) > 0.0) {
						length++;
						Items.add(i);
					}

				}
				//visualization.repaint();
			}
			//System.out.println("the value of z :" + cplex.getValue(z));

			double dis = 0;
			double[] deviations = new double[length];
			for(int i=0; i<n; i++) {
				dis += cplex.getValue(x[i])*c[i];
				//deviations[i] = 0.5*C[i]*cplex.getValue(x[i]);
				//System.out.println(deviations[i]);
			}

			//double[] deviations = new double[length];
			int index = 0;
			for(Integer arc : Items) {
				deviations[index] = cplex.getValue(x[arc])*d[arc];
				//System.out.println(cplex.getValue(x[arc]));
				index++;
			}

			//double nominalDistance = nominalDis(cplex);
			//double[] deviations = KPData.getDeviations(Items, length);
			double[] value = {dis - 2.5};

			//System.out.println("value" + value[0]);
			//for(int i=0; i<length; i++)
			//	System.out.println("deviations " + deviations[i]);

			//ConnectR connection = new ConnectR();
			for (int i=0; i<1; i++)
				PCV[i] = connection.connectToR(deviations,value,i);

			for(int i=0; i<n; i++) {
				OV += cplex.getValue(x[i]);
				//obj.addTerm(P[i], z[i]);
			}

			//OV = cplex.getObjValue();


			System.out.println("Gamma: " + Gamma);
			System.out.println();
			System.out.println("Objective Value: " + OV);
			System.out.println();
			System.out.println("PCV: " + PCV[0] );
			System.out.println();
			System.out.println("-----------------------------------------------");
			System.out.println();

			//System.out.println("robust distance: " + orienteering.ReadData.robustDis(arcs));
			//end
			cplex.end();

		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		double[] pair = {OV,PCV[0],PCV[1],PCV[2],PCV[3],PCV[4]};
        return pair;
	}

	public static void main(String[] args) throws IOException, REXPMismatchException, REngineException {
		ConnectR connection = new ConnectR();
		BS(1, connection);
	}
}
