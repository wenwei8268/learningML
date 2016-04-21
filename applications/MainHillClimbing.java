package applications;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import bayes.BNEvaluator;
import bayes.BNResultWriter;
import bayes.BayesianNetwork;
import bayes.structuresearch.HillClimbingBuilder;
import bayes.structuresearch.score.BIC;
import data.DataSet;
import data.fold.KFoldCreator;
import data.reader.ArffReader;
import pair.Pair;

public class MainHillClimbing 
{
    public static void main(String[] args)
    {  
        try
        {
            PrintWriter out = new PrintWriter(args[1]);
            
            /*
             *  Read the training data from the arff file
             */
            ArffReader reader = new ArffReader();
            DataSet data = reader.readFile(args[0]);
            
            /*
             * Scoring function
             */
            BIC bic = new BIC();
            
            List<Pair<DataSet, DataSet>> folds = KFoldCreator.create(data, 5);
            
            
            out.println("Result on folds:");
            Double scoreSum = 0.0;
            for (int i = 0; i < folds.size(); i++)
            {
                /*
                 * TODO: BAD!
                 */
                BNResultWriter.WRITER = new PrintWriter(args[2]+ "_" + i);
                
                Pair<DataSet, DataSet> fold = folds.get(i);
                
                HillClimbingBuilder hcBuilder = new HillClimbingBuilder();
                BayesianNetwork net = hcBuilder.buildNetwork(fold.getFirst(), 
                                                             1, 
                                                             bic, 
                                                             null);
                
                Double score = BNEvaluator.calculateLogLikelihood(net, 
                                                                  fold.getSecond());
                
                scoreSum += score;
                
                out.println("\n\n-------- Fold " + i + " --------\n");
                
                out.println("Net Structure: ");
                out.print(net);
                
                out.println("Likelihood of test data: ");
                out.println(score);
                
                /*
                 * TODO: BAD!
                 */
                BNResultWriter.WRITER.close();
            }
            out.println("Average likelihood:");
            out.println(scoreSum / folds.size());
            out.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error instantiating output file writer.");
            System.exit(1);
        }
  
    }

}
