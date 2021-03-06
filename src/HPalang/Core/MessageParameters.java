/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Iman Jahandideh
 */
public class MessageParameters extends Equalitable<MessageParameters>
{
    private final Map<String, VariableParameter> parameters = new HashMap<>();
    
    // NOTE: parametersLists is an ad hock solution for keeping the order of the parameters.
    private final List<VariableParameter> parametersLists = new ArrayList<>();
    
    public void Add(VariableParameter variableParameter)
    {
        assert (parameters.containsKey(variableParameter.Name()) == false);
        parameters.put(variableParameter.Name(),variableParameter);
        parametersLists.add(variableParameter);
    }
    
    public void AddAll(MessageParameters other)
    {
        for(VariableParameter param : other.AsSet())
            this.Add(param);
    }

    public VariableParameter Find(String parameterName)
    {
        return parameters.get(parameterName);
    }
    
    public Set<VariableParameter> AsSet()
    {
        return new HashSet<>(parameters.values());
    }
    
    public List<VariableParameter> AsList()
    {
        return parametersLists;
    }
    
    @Override
    protected boolean InternalEquals(MessageParameters other)
    {
       return this.parameters.equals(other.parameters)
               && this.parametersLists.equals(other.parametersLists);
    }

    @Override
    protected int InternalHashCode()
    {
        return parameters.hashCode();
    }
    
    public static MessageParameters From(VariableParameter ... params)
    {
        MessageParameters parameters = new MessageParameters();
        
        for(VariableParameter param : params)
            parameters.Add(param);
        
        return parameters;
    }
}
