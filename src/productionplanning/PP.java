package productionplanning;

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

public class PP {

	public static List<Integer> Items = new LinkedList<>();
	
	public static double[] PP(double epsilon, ConnectR connection) throws IOException, REXPMismatchException, REngineException {
        double OV = 0;
		double[] PCV = {0,0,0,0,0};
        try {
			Items.clear();

			IloCplex cplex = new IloCplex();
			//cplex.setOut(null);

			double P[] = {180,180,250,270,300,320};
			double C[] = {20,25,30,40,50,60};
			double V[] = {2,2,2,2,2,2};
			double U[] = {1500,2000,2200,3000,2700,2500};
			double D[] = {1100,1500,1800,1600,2300,2500};
			double Zero[] = {0,0,0,0,0,0};

			int n = 6;
			double theta = 0.00001;

			//
			//IloNumVar[] x = cplex.boolVarArray(n);
			IloNumVar[] z = cplex.numVarArray(n, Zero, D);
			IloNumVar[] x = cplex.numVarArray(n, Zero, U);
			IloNumVar[] y = cplex.numVarArray(n, 0, Double.MAX_VALUE);
			IloNumVar zz = cplex.numVar(1,1);

			//objective
			IloLinearNumExpr obj = cplex.linearNumExpr();

			for(int i=0; i<n; i++) {
						obj.addTerm(P[i],z[i]);
				//obj.addTerm(-theta*(C[i]+(epsilon)*0.5*C[i]), x[i]);
				//obj.addTerm(-theta*(C[i]+(epsilon)*0.5*C[i]), x[i]);
				//obj.addTerm(-theta*V[i],y[i]);
				//obj.addTerm(theta*400000,zz);

				//obj.addTerm(-theta*(0.25*C[i]*C[i]), x[i]);
			}
			
			cplex.addMaximize(obj);

			//constraints
			//0
			IloLinearNumExpr constraint, constraint1;

			//1
			constraint = cplex.linearNumExpr();

			for(int i=0; i<n; i++)
			{
					constraint.addTerm(C[i], x[i]);
					constraint.addTerm(epsilon*0.5*C[i],x[i]);
					constraint.addTerm(V[i],y[i]);
			}
			//constraint.addTerm(Gamma,z);
			cplex.addLe(constraint, 400000);

			//2
			constraint = cplex.linearNumExpr();

			constraint.addTerm(x[0],1);
			constraint.addTerm(y[0],-1);
			constraint.addTerm(z[0],-1);


			cplex.addEq(constraint, -500);

			//3

			for(int i=1; i<n; i++)
			{
				constraint = cplex.linearNumExpr();
				constraint.addTerm(y[i-1],1);
				constraint.addTerm(x[i],1);
				constraint.addTerm(y[i],-1);
				constraint.addTerm(z[i],-1);
				cplex.addEq(constraint, 0);
			}
			//4

			constraint = cplex.linearNumExpr();
			constraint.addTerm(y[5],1);
			cplex.addEq(constraint, 500);

			//5


			//solve
			int length = 0;
			if(cplex.solve())
			//if(cplex.populate())
			{
				for(int i=0; i<n; i++) {
					System.out.println("x" + i + "=" + cplex.getValue(x[i]));
					System.out.println("y" + i + "=" + cplex.getValue(y[i]));
					System.out.println("z" + i + "=" + cplex.getValue(z[i]));
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
				dis += cplex.getValue(x[i])*C[i] + cplex.getValue(y[i])*V[i];
				//deviations[i] = 0.5*C[i]*cplex.getValue(x[i]);
				//System.out.println(deviations[i]);
			}

			//double[] deviations = new double[length];
			int index = 0;
			for(Integer arc : Items) {
				deviations[index] = 0.5*C[arc]*cplex.getValue(x[arc]);
				//System.out.println(cplex.getValue(x[arc]));
				index++;
			}

			//double nominalDistance = nominalDis(cplex);
			//double[] deviations = KPData.getDeviations(Items, length);
			double[] value = {dis - 400000};

			//System.out.println("value" + value[0]);
			//for(int i=0; i<length; i++)
			//	System.out.println("deviations " + deviations[i]);

			//ConnectR connection = new ConnectR();
			for (int i=0; i<4; i++) {
				PCV[i] = connection.connectToR(deviations,value,i);
			}

			for(int i=0; i<n; i++) {
				OV += cplex.getValue(z[i])*P[i];
				//obj.addTerm(P[i], z[i]);
			}

            //OV = cplex.getObjValue();


			System.out.println("epsilon: " + epsilon);
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
		PP(4.61, connection);
	}
}
