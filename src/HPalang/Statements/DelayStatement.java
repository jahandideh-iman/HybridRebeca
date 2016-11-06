/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Statements;


/**
 *
 * @author Iman Jahandideh
 */
public class DelayStatement implements Statement
{
    private float delay;
    
    public DelayStatement(float delay)
    {
        this.delay = delay;
    }
    
    public float GetDelay()
    {
        return delay;
    }
    
        
    @Override
    public boolean equals(Object other)
    {
        if(other == null)
            return false;
        
        if (!this.getClass().isAssignableFrom(other.getClass()))
            return false;
            
        DelayStatement otherD = (DelayStatement) other;
       
        return this.delay == otherD.delay;
    }
    
    @Override
    public String toString()
    {
        return "delay(" + delay +")";
    }
}
