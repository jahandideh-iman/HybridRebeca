/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Builders;

import HPalang.Core.SoftwareActor;
import HPalang.Core.MessageHandler;
import HPalang.Core.SoftwareActorType;
import HPalang.Core.Variables.IntegerVariable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Iman Jahandideh
 */
public class SoftwareActorBuilder
{
    
    private String name;
    private int capacity = 1;
    private Map<String,MessageHandler> messageHandlers = new HashMap<>();
    private List<IntegerVariable> discreteVariables = new LinkedList<>();
    
    public SoftwareActorBuilder WithID(String id)
    {
        this.name = id;
        return this;
    }
    
    public SoftwareActorBuilder WithDiscreteVariable(IntegerVariable dVariable)
    {
        discreteVariables.add(dVariable);
        return this;
    }
    
    public SoftwareActorBuilder WithCapacity(int capacity)
    {
        this.capacity = capacity;
        return this;
    }
    
    public SoftwareActorBuilder WithHandler(String handlerId, MessageHandler handler)
    {
        messageHandlers.put(handlerId, handler);
        return this;
    }
    
    public SoftwareActor Build()
    {
        SoftwareActorType type = new SoftwareActorType(name + "Type");
        SoftwareActor actor = new SoftwareActor(name,type,capacity);
        for(Map.Entry<String,MessageHandler> entry :messageHandlers.entrySet())
            type.AddMessageHandler(entry.getKey(), entry.getValue());
        
        return actor;
    }
}
