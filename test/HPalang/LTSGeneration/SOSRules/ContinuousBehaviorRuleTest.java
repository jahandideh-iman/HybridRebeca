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
import HPalang.LTSGeneration.ConditionalLabel;
import HPalang.LTSGeneration.LTSGenerator;
import HPalang.LTSGeneration.LabeledTransitionSystem;
import HPalang.LTSGeneration.MessageWithBody;
import HPalang.LTSGeneration.RunTimeStates.ActorRunTimeState;
import HPalang.LTSGeneration.RunTimeStates.ContinuousBehavior;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;
import HPalang.LTSGeneration.TauLabel;
import HPalang.Statements.ContinuousBehaviorStatement;
import HPalang.Statements.Statement;
import static HPalang.Statements.Statement.StatementsFrom;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Iman Jahandideh
 */
public class ContinuousBehaviorRuleTest
{
    LTSGenerator ltsGenerator = new LTSGenerator();
    LabeledTransitionSystem generatedLTS;
    GlobalRunTimeStateBuilder globalState = new GlobalRunTimeStateBuilder();
    
    @Before
    public void Setup()
    {
        ltsGenerator.AddSOSRule(new ContinuousBehaviorRule());
    }
    
    @Test
    public void ForEachActorStateIfNextStatementIsContinuousBehaviorThenAddsTheBehavior()
    {
        Actor actor1 = new ActorBuilder().WithID("actor1").WithCapacity(1).Build();
       
        ActorRunTimeStateBuilder actor1State = new ActorRunTimeStateBuilder()
                .WithActor(actor1)
                .EnqueueStatement(new ContinuousBehaviorStatement(new ContinuousBehavior("inv","ode","guard",StatementsFrom(new Statement()))));
        
        
        globalState
                .AddActorRunTimeState(actor1State);
                
                 
        generatedLTS = ltsGenerator.Generate(globalState.Build());
        
        GlobalRunTimeState nextGlobalState1 = globalState.Build();
        ActorRunTimeState nextActorState1 = nextGlobalState1.FindActorState(actor1);
        nextActorState1.DequeueNextStatement();
        nextActorState1.GetContinuousState().AddBehavior(new ContinuousBehavior("inv","ode","guard",StatementsFrom(new Statement())));
        
        assertTrue(generatedLTS.HasTransition(globalState.Build(), new TauLabel(), nextGlobalState1));
    }
}