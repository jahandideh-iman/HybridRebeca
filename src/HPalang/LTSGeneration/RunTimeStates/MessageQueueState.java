/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.RunTimeStates;

import HPalang.Core.Message;
import HPalang.Core.MessagePacket;
import HPalang.LTSGeneration.SimpleState;
import HPalang.Utilities.Queue;

/**
 *
 * @author Iman Jahandideh
 */
public class MessageQueueState extends SimpleState<MessageQueueState>
{
    private final Queue<MessagePacket> messages = new Queue<>();
    
    public Queue<MessagePacket> Messages()
    {
        return messages;
    }
    
    @Override
    protected MessageQueueState NewInstance()
    {
        return new MessageQueueState();
    }

    @Override
    protected boolean DataEquals(MessageQueueState other)
    {
        return messages.equals(other.messages);
    }

    @Override
    protected void CloneData(MessageQueueState copy)
    {
        copy.messages.Enqueue(messages);
    }

    @Override
    protected int InternalHashCode()
    {
        return messages.hashCode();
    }
    
}
