/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core;

/**
 *
 * @author Iman Jahandideh
 */
@Deprecated // Use variable instead
public class ContinuousVariable extends Variable
{
    
    public ContinuousVariable(String name)
    {
        super(name, Type.real);
    }
    
}
