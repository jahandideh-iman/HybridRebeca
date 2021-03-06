/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core.DiscreteExpressions;

import HPalang.Core.Expression;
import HPalang.Core.ExpressionVisitor;
import HPalang.Core.Expressions.Visitors.ConstantDiscreteExpressionVisitor;
import HPalang.Core.ValuationContainers.SimpleValuationContainer;
import HPalang.Core.ValuationContainer;
import HPalang.Core.Visitor;

/**
 *
 * @author Iman Jahandideh
 */
public class ConstantDiscreteExpression extends DiscreteExpressionT<ConstantDiscreteExpression>
{
    private final int constant;
    
    public ConstantDiscreteExpression(int constant)
    {
        this.constant = constant;
    }
    
    public int Constant()
    {
        return constant;
    }
    
    @Override
    public int Evaluate(ValuationContainer valuations)
    {
        return constant;
    }

    @Override
    protected boolean InternalEquals(ConstantDiscreteExpression other)
    {
        return this.constant == other.constant;
    }

    @Override
    protected int InternalHashCode()
    {
        return Integer.hashCode(constant);
    }

    @Override
    public boolean IsComputable(ValuationContainer valuations)
    {
        return true;
    }

    @Override
    public Expression PartiallyEvaluate(ValuationContainer valuations)
    {
        return this;
    }

    @Override
    public String toString()
    {
        return String.valueOf(constant);
    }
    
    public int Value()
    {
        return constant;
    }

    @Override
    public void Visit(Visitor visitor)
    {
        if(visitor instanceof ConstantDiscreteExpressionVisitor)
            ((ConstantDiscreteExpressionVisitor) visitor).Visit(this);
    }
}
