/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration;

/**
 *
 * @author Iman Jahandideh
 */
public class TauLabel implements Label
{
    @Override
    public boolean equals(Object other)
    {
         if(other == null)
            return false;
        
        if (!this.getClass().isAssignableFrom(other.getClass()))
            return false;
        return true;
    }
    
    @Override
    public String toString()
    {
        return "t";
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        return hash;
    }
}
