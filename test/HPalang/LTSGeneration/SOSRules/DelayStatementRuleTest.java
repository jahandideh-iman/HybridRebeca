/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.SOSRules;

import Builders.ActorBuilder;
import Builders.ActorRunTimeStateBuilder;
import Builders.GlobalRunTimeStateBuilder;
import HPalang.Core.Actor;
import HPalang.LTSGeneration.RunTimeStates.ContinuousBehavior;
import HPalang.LTSGeneration.LTSGenerator;
import HPalang.LTSGeneration.LabeledTransitionSystem;
import HPalang.LTSGeneration.RunTimeStates.ActorRunTimeState;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;
import HPalang.LTSGeneration.TauLabel;
import HPalang.Core.Statements.DelayStatement;
import HPalang.Core.Statements.ResumeStatement;
import static HPalang.Core.Statement.StatementsFrom;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;


/**
 *
 * @author Iman Jahandideh
 */

public class DelayStatementRuleTest
{ 
    LTSGenerator ltsGenerator = new LTSGenerator();
    LabeledTransitionSystem generatedLTS;
    GlobalRunTimeStateBuilder globalState = new GlobalRunTimeStateBuilder();
    
    @Before
    public void Setup()
    {
        ltsGenerator.AddSOSRule(new DelayStatementRule());
    }
    
    @Test
    public void ForEachActorStateIfNextStatementIsDelayThenDelaysTheActorStateAndAddsDelayBehavior()
    {
        Actor actor1 = new ActorBuilder().WithID("actor1").WithCapacity(1).Build();
       
        ActorRunTimeStateBuilder actor1State = new ActorRunTimeStateBuilder()
                .WithActor(actor1)
                .EnqueueStatement(new DelayStatement(1.0f));
        
        globalState
                .AddActorRunTimeState(actor1State);
                
                 
        generatedLTS = ltsGenerator.Generate(globalState.Build());
        
        GlobalRunTimeState stateAfterActor1Delay = globalState.Build();
        ActorRunTimeState stateAfterActor1Delay_Actor1 = stateAfterActor1Delay.FindActorState(actor1);
        stateAfterActor1Delay_Actor1.StatementQueue().Dequeue();
        stateAfterActor1Delay_Actor1.SuspendedStatements().Enqueue(stateAfterActor1Delay_Actor1.StatementQueue());
        stateAfterActor1Delay_Actor1.StatementQueue().Clear();
        stateAfterActor1Delay_Actor1.SetSuspended(true);
        String actor1DelayVar = actor1.GetDelayVariableName();
        stateAfterActor1Delay_Actor1.ContinuousBehaviors().Add(new ContinuousBehavior(
                actor1DelayVar+"<="+1.0f 
                , actor1DelayVar+"'=1"
                , actor1DelayVar+"=="+1.0f
                , StatementsFrom(new ResumeStatement())));

        assertTrue(generatedLTS.HasTransition(globalState.Build(), new TauLabel(), stateAfterActor1Delay));
    }
    
}
