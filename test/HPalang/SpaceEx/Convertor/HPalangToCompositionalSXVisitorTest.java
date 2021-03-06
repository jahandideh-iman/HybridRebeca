/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang.SpaceEx.Convertor;

import Builders.SoftwareActorBuilder;
import Builders.ProgramDefinitionBuilder;
import Builders.SpaceExModelBuilder;
import HPalang.Core.SoftwareActor;
import HPalang.Core.ModelDefinition;
import HPalang.SpaceEx.Core.SpaceExModel;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Iman Jahandideh
 */
public class HPalangToCompositionalSXVisitorTest
{
    
    private HPalangToCompositionalSXConvertor convertor;
    
    @Before
    public void Setup()
    {
        convertor = new HPalangToCompositionalSXConvertor();
    }

    @Test
    public void ConvertsEmptyHPalangModelToEmptySXModel()
    {
        ModelDefinition hpalangModel = new ProgramDefinitionBuilder().Build();
        
        convertor.Convert(hpalangModel);
        
        
        assertTrue(convertor.GetConvertedModel().equals(new SpaceExModel()));
    }
    
//    @Test
//    public void ConvertsEmptyActorCorrectly()
//    {
//        SoftwareActor actor = new SoftwareActorBuilder().WithID("actor").Build();
//        ModelDefinition hpalangModel = new ProgramDefinitionBuilder()
//                .With(actor)
//                .Build();
//        
//        convertor.Visit(hpalangModel);
//        
//        SpaceExModel expectedModel = new SpaceExModelBuilder()
//                .With(ne)
//        
//        
//        assertTrue(convertor.GetConvertedModel().equals());
//    }
    
}
