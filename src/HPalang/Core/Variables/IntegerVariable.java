/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core.Variables;

import HPalang.Core.Variable;

/**
 *
 * @author Iman Jahandideh
 */
// TODO: Make this class final.
public /*final*/ class IntegerVariable extends Variable
{
    public IntegerVariable(String name)
    {
        super(name, Type.integer);
    } 
}
