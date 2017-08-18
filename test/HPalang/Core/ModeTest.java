/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.Core;

import static HPalang.Core.Statement.StatementsFrom;
import Mocks.EmptyStatement;
import java.util.Queue;
import java.util.Set;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Iman Jahandideh
 */
public class ModeTest
{
    
    @Test
    public void ModesWithEqualDataAreEqual()
    {
        String inv = "inv";
        Set<DifferentialEquation> equations = Mode.EquationsFrom(DifferentialEquation.Empty("eq1"));
        String guard = "guard";
        Queue<Statement> actions = StatementsFrom(new EmptyStatement());
        
        Mode mode1 = new Mode("mode","inv", equations , "guard", actions);
        Mode mode2 = new Mode("mode","inv", equations , "guard", actions);
        
        assertThat(mode1, is(equalTo(mode2)));
    }
    
}
