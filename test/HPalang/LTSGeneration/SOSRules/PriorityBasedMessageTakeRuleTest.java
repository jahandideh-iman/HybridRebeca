/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.SOSRules;

import HPalang.Core.Message;
import HPalang.Core.MessageArguments;
import HPalang.Core.MessagePacket;
import HPalang.Core.Statements.AssignmentStatement;
import HPalang.Core.Statements.MessageTeardownStatement;
import HPalang.Core.VariableArgument;
import HPalang.Core.VariableParameter;
import HPalang.LTSGeneration.Labels.SoftwareLabel;
import HPalang.LTSGeneration.RunTimeStates.GlobalRunTimeState;
import HPalang.LTSGeneration.RunTimeStates.MessageQueueState;
import HPalang.LTSGeneration.RunTimeStates.SoftwareActorState;
import HPalang.Utilities.Queue;
import static TestUtilities.NetworkingUtility.*;
import static TestUtilities.CoreUtility.*;
import Mocks.EmptyMessage;
import static TestUtilities.CoreUtility.SimpleStateInfo;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

/**
 *
 * @author Iman Jahandideh
 */
public class PriorityBasedMessageTakeRuleTest extends SOSRuleTestFixture
{
    
    @Before
    public void Setup()
    {
        rule = new PriorityBasedMessageTakeRule();
    }
    
    @Test @Ignore
    public void TakesTheHighestPriorityMessageIfIsNotSuspendedAndExecutionQueueIsEmpty()
    {
        Message lowPriotiyMessage = new EmptyMessage("lowPriority");
        Message highPriorityMessage = new EmptyMessage("highPriority");
        
        SoftwareActorState actorState = CreateSoftwareActorState("actor");
        actorState.SetSuspended(false);
        actorState.MessageQueueState().Messages().Enqueue(SelfMessagePacket(actorState.Actor(), lowPriotiyMessage, 1));
        actorState.MessageQueueState().Messages().Enqueue(SelfMessagePacket(actorState.Actor(), highPriorityMessage, 2));
        
        globalState.DiscreteState().AddSoftwareActorState(actorState);
        
        generatedLTS = ltsGenerator.Generate(globalState);
        
        GlobalRunTimeState expectedGlobalState = ExpectedGlobalStateWhenMessageIsTaken(globalState, actorState, highPriorityMessage);
        
        rule.TryApply(SimpleStateInfo(globalState), transitionCollectorChecker);
        

        transitionCollectorChecker.ExpectTransition(new SoftwareLabel(), expectedGlobalState);
    }
    
    private GlobalRunTimeState ExpectedGlobalStateWhenMessageIsTaken(GlobalRunTimeState originalState, SoftwareActorState senderState , Message message)
    {
        GlobalRunTimeState expectedGlobalState = originalState.DeepCopy();
        SoftwareActorState expectedActorState = expectedGlobalState.DiscreteState().FindActorState(senderState.SActor());
        MessagePacket relatedPacket =  FindFirstPacketForMessage(expectedActorState.MessageQueueState().Messages(),message);
        expectedActorState.MessageQueueState().Messages().Remove(relatedPacket);
        
        List<VariableParameter> parametersList = message.Parameters().AsList();
        List<VariableArgument> argumentsList = relatedPacket.Arguments().AsList();
        for(int i = 0 ; i < parametersList.size(); i++ )
            expectedActorState.ExecutionQueueState().Statements().Enqueue(
                    new AssignmentStatement(parametersList.get(i).Variable(), argumentsList.get(i).Value()));
        
        
        expectedActorState.ExecutionQueueState().Statements().Enqueue(message.GetMessageBody());
        
        for (VariableParameter parameter : message.Parameters().AsSet()) {
            expectedActorState.ValuationState().Valuation().Add(parameter.Variable());
        }
        expectedActorState.ExecutionQueueState().Statements().Enqueue(
                new MessageTeardownStatement(message.Parameters(), Collections.EMPTY_LIST));
        
        return expectedGlobalState;
    }
    

     
     private MessagePacket FindFirstPacketForMessage(Queue<MessagePacket> packets, Message message)
     {
         for(MessagePacket packet : packets)
             if(packet.Message().equals(message))
                 return packet;
         return null;
     }
}
