/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang;

import HPalang.Convertors.HybridAutomatonToSXConvertor;
import HPalang.HybridAutomataGeneration.HybridAutomatonGenerator;
import HPalang.Core.DiscreteExpressions.BinaryExpression;
import HPalang.Core.DiscreteExpressions.BinaryOperators.LogicalAndOperator;
import HPalang.Core.ModelDefinition;
import HPalang.HybridAutomataGeneration.SOSRules.ConversionRule;
import HPalang.LTSGeneration.LTSGenerator;
import HPalang.Core.TransitionSystem.LabeledTransitionSystem;
import HPalang.LTSGeneration.ModeDefinitionToGlobalStateConvertor;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;
import HPalang.LTSGeneration.Labels.SoftwareLabel;
import HPalang.Core.Variable;
import HPalang.HybridAutomataGeneration.HybridAutomaton;
import HPalang.Core.TransitionSystem.Label;
import HPalang.LTSGeneration.Labels.ContinuousLabel;
import HPalang.LTSGeneration.Labels.Guard;
import HPalang.LTSGeneration.Labels.Reset;
import HPalang.LTSGeneration.SOSRules.*;
import HPalang.LTSGeneration.SOSRules.MessageSendRules.CANMessageSendRule;
import HPalang.LTSGeneration.SOSRules.MessageSendRules.WireMessageSendRule;
import HPalang.Parser.Parser;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Iman Jahandideh
 */
public class Main {

    
    // TODO: ------------------------- REFACTOR THIS -------------------------------
    public static void main(String[] args) throws FileNotFoundException, IOException 
    {
        ModelDefinition definition;
        if(args.length ==0)
            definition = BrakeByWireModelTwoWheel.Create();
        else
            definition = new Parser().ParseModel(Read(args[0]));
        
        LTSGenerator tierOneLTSGenerator = CreateTierOneLTSGenrator();
        
        HybridAutomatonGenerator hybridAutomatonGenerator = new HybridAutomatonGenerator();
        hybridAutomatonGenerator.AddSOSRule(new ConversionRule());
        
         
        ModeDefinitionToGlobalStateConvertor convertor = new ModeDefinitionToGlobalStateConvertor();
        LabeledTransitionSystem lts =  tierOneLTSGenerator.Generate(convertor.Convert(definition));
           
        FileWriter writer = new FileWriter("output/");
        
        OutputLTS("FineLTS",lts, writer);
        //OutputLTS("ReducedLTS",new LTSReducer().Reduce(lts), writer);
        

        //LabeledTransitionSystem reduceLTS =  new LTSReducer().Reduce(lts);
        //OutputLTS("ReducedLTS", reduceLTS, writer);
        
        HybridAutomaton automaton = hybridAutomatonGenerator.Generate(lts, definition);
        
        OutputHA("HA", automaton, writer);
//        
//        writer.Write("output_LTS.xml", new LTSToXMLConvertor().Convert(lts));
        //writer.Write("output_HA.xml", new HybridAutomatonToSXConvertor().Convert(automaton));
        
    }

    private static void OutputLTS(String prefix, LabeledTransitionSystem lts, FileWriter writer)
    {
        System.out.println(prefix + " States  : " + lts.States().size());
        System.out.println(prefix+ " Transition : " + lts.Transitions().size());
        
        //writer.Write(prefix +"_aut.aut", new LTSToAUTConvertor().Convert(lts));
        //writer.Write(prefix +"_fsm.fsm", new LTSToFSMConvertor().Convert(lts));
    }
    
    private static void OutputHA(String prefix, HybridAutomaton automaton, FileWriter writer)
    {
        System.out.println(prefix + " Locations  : " + automaton.GetLocations().size());
        System.out.println(prefix+ " Transition : " + automaton.GetTransitions().size());
        
        //writer.Write(prefix +"_aut.aut", new LTSToAUTConvertor().Convert(lts));
        //writer.Write(prefix +"_fsm.fsm", new LTSToFSMConvertor().Convert(lts));
    }
    
    
    private static InputStream Read(String filePath) throws FileNotFoundException
    {
        return new FileInputStream(filePath);
    }
    
    private static LTSGenerator CreateTierOneLTSGenrator()
    {
        LTSGenerator genetator = new LTSGenerator();
        
        // Software
        genetator.AddSOSRule(new SoftwareActorFIFOMessageTakeRule());
        genetator.AddSOSRule(new PhysicalActorFIFOMessageTake());
        genetator.AddSOSRule(new MessageTeardownStatementRule());
        genetator.AddSOSRule(new DelayStatementRule());
        genetator.AddSOSRule(new AssignmentStatementRule());
        genetator.AddSOSRule(new IfStatementRule());
        genetator.AddSOSRule(new CANMessageSendRule());       
        genetator.AddSOSRule(new WireMessageSendRule());
        genetator.AddSOSRule(new ModeChangeStatementRule());

        // Network 
        genetator.AddSOSRule(new CANScheduleRule());
        
        // Phyiscal
        genetator.AddSOSRule(new ContinuousBehaviorExpirationRule());
        genetator.AddSOSRule(new EventExpirationRule());
        
        return genetator;
    }

    static public Label CreateLabelFor(Label firstLabel, Label secondLabel)
    {
        Map<Variable, Reset> resets = new HashMap<>();
        
        Guard newGuard;
        
        for(Reset re : (Set<Reset>)firstLabel.Resets())
                resets.put(re.Variable(), re);
        
        for(Reset re : (Set<Reset>)secondLabel.Resets())
                resets.put(re.Variable(), re);

   
        newGuard = new Guard(new BinaryExpression(firstLabel.Guard().Expression(), new LogicalAndOperator(), secondLabel.Guard().Expression()));
        if(firstLabel instanceof SoftwareLabel)
            return new SoftwareLabel(newGuard ,new LinkedHashSet<>(resets.values()));
        else
          return new ContinuousLabel(newGuard ,new LinkedHashSet<>(resets.values()));  
    }


}

  
