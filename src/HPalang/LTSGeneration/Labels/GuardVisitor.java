/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.LTSGeneration.Labels;

/**
 *
 * @author Iman Jahandideh
 */
public interface GuardVisitor
{
    public void Visit(Guard expr);
}
