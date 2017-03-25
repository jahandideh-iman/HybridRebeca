/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang;

import HPalang.Core.Actor;
import HPalang.Core.ContinuousVariable;
import HPalang.Core.MainBlock;
import HPalang.Core.MessageHandler;
import HPalang.Core.Messages.NormalMessage;
import HPalang.Core.ProgramDefinition;
import HPalang.Core.Statement;
import HPalang.Core.Statements.ContinuousBehaviorStatement;
import HPalang.Core.Statements.DelayStatement;
import HPalang.Core.Statements.SendStatement;
import HPalang.LTSGeneration.RunTimeStates.ContinuousBehavior;

/**
 *
 * @author Iman Jahandideh
 */
public class SimpleModel
{
     public ProgramDefinition Create()
    {
        ProgramDefinition definition = new ProgramDefinition();
        
        Actor actorA = new Actor("A",1);        
        Actor actorB = new Actor("B",1);
        
        actorA.AddContinuousVariable(new ContinuousVariable("timer"), 0);
        
        MessageHandler handler_a1 = new MessageHandler();  
        MessageHandler handler_a2 = new MessageHandler();        
        
        MessageHandler handler_b1 = new MessageHandler();

        
        actorA.AddMessageHandler("a1",handler_a1);   
        actorA.AddMessageHandler("a2",handler_a2);

        actorB.AddMessageHandler("b1",handler_b1);
        
        handler_a1.AddStatement(new DelayStatement(1.0f));
        handler_a1.AddStatement(
                new ContinuousBehaviorStatement(
                        new ContinuousBehavior(
                                "timer<=2", 
                                "timer' = 1", 
                                "timer == 2", 
                                Statement.StatementsFrom(new SendStatement(actorA,new NormalMessage(handler_a1))))));
        handler_a1.AddStatement(new SendStatement(actorA,new NormalMessage(handler_a1)));

        handler_b1.AddStatement(new DelayStatement(1.0f));
        handler_b1.AddStatement(new SendStatement(actorA,new NormalMessage(handler_a1))); 
        handler_b1.AddStatement(new SendStatement(actorA,new NormalMessage(handler_a2)));

        
     
        MainBlock mainBlock = new MainBlock();     
                
        definition.AddActor(actorA);
        definition.AddActor(actorB);
        definition.SetMainBlock(mainBlock);
        
        return definition;
    }
}
