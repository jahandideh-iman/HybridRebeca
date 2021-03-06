/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Mocks;

import HPalang.Core.Expression;
import HPalang.Core.ExpressionVisitor;
import HPalang.Core.ValuationContainers.SimpleValuationContainer;
import HPalang.Core.ValuationContainer;
import HPalang.Core.Visitor;

/**
 *
 * @author Iman Jahandideh
 */
public class UncomputableExpression implements Expression
{

    private Expression partialValue;
    
    public UncomputableExpression(Expression partialValue)
    {
        this.partialValue = partialValue;
    }
    
    @Override
    public boolean IsComputable(ValuationContainer valuations)
    {
        return false;
    }

    @Override
    public int Evaluate(ValuationContainer valuations)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Expression PartiallyEvaluate(ValuationContainer valuations)
    {
        return partialValue;
    }

    @Override
    public void Visit(Visitor visitor)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
