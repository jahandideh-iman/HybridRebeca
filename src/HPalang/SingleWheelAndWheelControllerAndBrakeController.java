/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HPalang;

import HPalang.Core.ActorType;
import HPalang.Core.CommunicationType;
import HPalang.Core.SoftwareActor;
import HPalang.Core.DelegationParameter;
import HPalang.Core.ContinuousExpressions.DifferentialEquation;
import HPalang.Core.DiscreteExpressions.BinaryExpression;
import HPalang.Core.DiscreteExpressions.BinaryOperators.*;
import HPalang.Core.DiscreteExpressions.VariableExpression;
import HPalang.Core.InstanceParameter;
import HPalang.Core.MessageHandler;
import HPalang.Core.Mode;
import HPalang.Core.ContinuousExpressions.Invarient;
import HPalang.Core.DiscreteExpressions.FalseConst;
import HPalang.Core.DiscreteExpressions.TrueConst;
import HPalang.Core.MainBlock;
import HPalang.Core.Message;
import HPalang.Core.PhysicalActor;
import HPalang.Core.PhysicalActorType;
import HPalang.Core.ModelDefinition;
import HPalang.Core.SoftwareActorType;
import HPalang.Core.Statement;
import HPalang.Core.Statements.AssignmentStatement;
import HPalang.Core.Statements.IfStatement;
import HPalang.Core.Variable;
import HPalang.Core.Variables.FloatVariable;
import HPalang.Core.Variables.RealVariable;
import static HPalang.Core.ModelCreationUtilities.*;
import HPalang.Core.SingleCommunicationRealVariablePool;
import HPalang.LTSGeneration.Labels.Guard;

/**
 *
 * @author Iman Jahandideh
 */
public class SingleWheelAndWheelControllerAndBrakeController
{
    public static final float arbitrartDelay = 13.0f;
    public static final String Wheel__controller_instance = "controller";
    public static final String Wheel__speed_delegation = "wheel_speed_delegation";
    public static final String Wheel__torque_port = "torque_port";
    public static final String Wheel__speed = "speed";
    public static final String Wheel__torque = "torque";
    public static final String Wheel__timer = "timer";
    public static final String Wheel__brake_mode = "Brake";
    public static final float Wheel__period_const = 0.1f;
    
    public static final String Wheel_Controller__wheel_instance = "wheel";
    public static final String Wheel_Controller__wheel_speed_port = "wheel_speed_port";
    public static final String Wheel_Controller__wheel_speed = "wheel_speed";
    public static final String Wheel_Controller__slip_rate = "slip_rate";
    public static final String Wheel_Controller__apply_torque_handler = "applyTorque";
    public static final String Wheel_Controller__requested_torque = "requested_torque";
    public static final String Wheel_Controller__estimated_speed = "vehicle_speed";
    public static final float Wheel_Controller__wheel_radius_const = 0.3f;

    public static final String Global_Brake_Controller__wheel_rpm_FR_port = "wheel_speed_FR_port";

    
    public static final String Global_Brake_Controller__brake_percent_port = "brake_percent_port";
    public static final String Global_Brake_Controller__control_handler = "control";
    public static final String Global_Brake_Controller__wheel_controller_FR_Instance = "wheel_controller_FR";
    public static final String Global_Brake_Controller__wheel_speed_FR = "wheel_speed_FR";
    public static final String Global_Brake_Controller__brake_percent = "brake_percent";
    public static final String Global_Brake_Controller__estimated_speed = "estimated_speed";
    public static final String Global_Brake_Controller__global_torque = "global_torque";

    
    
    
    public static final float CAN__dealy_const = 0.01f;
    
    public static ModelDefinition Create()
    {
        ModelDefinition definition = new ModelDefinition();
        
        PhysicalActorType wheelType = new PhysicalActorType("Wheel");
        SoftwareActorType wheelControllerType = new SoftwareActorType("WheelController");
        SoftwareActorType globalBrakeControllerType = new SoftwareActorType("GlobalBrakeController");
        
        
        FillSkeletonForWheelType(wheelType, wheelControllerType);
        FillSkeletonForWheelControllerType(wheelControllerType, wheelType);
        FillSkeletonForGlobalBrakeControllerType(globalBrakeControllerType, wheelControllerType);
        
        FillFleshForWheelType(wheelType, wheelControllerType.FindMessageHandler(Wheel_Controller__wheel_speed_port));
        FillFleshForWheelControllerType(wheelControllerType, wheelType.FindMode(Wheel__brake_mode), wheelType.FindMessageHandler(Wheel__torque_port));
        FillFleshForGlobalBrakeControllerType(globalBrakeControllerType, wheelControllerType.FindMessageHandler(Wheel_Controller__apply_torque_handler));
        
        SoftwareActor global_brake_controller = new SoftwareActor("global_brake_controller", globalBrakeControllerType, 1);
        
        SoftwareActor wheel_controller_FR = new SoftwareActor("wheel_controller_FR",wheelControllerType, 1);     


        PhysicalActor wheel_FR = new PhysicalActor("wheel_FR", wheelType,1);       

        
        
        FillWheelActor(wheel_FR,wheel_controller_FR,global_brake_controller,Global_Brake_Controller__wheel_rpm_FR_port);
        
  
        FillWheelControllerActor(wheel_controller_FR, wheel_FR);
 
        FillGlobalBrakeController(
                global_brake_controller,
                wheel_controller_FR);
        

        definition.AddType(wheelType);
        definition.AddType(wheelControllerType);
        definition.AddType(globalBrakeControllerType);
        
        definition.AddActor(global_brake_controller);
        
        definition.AddActor(wheel_controller_FR);
        
        definition.AddActor(wheel_FR);
        
        
        definition.SetMainBlock(new MainBlock());
         
        SetNetworkPriority(definition, wheel_controller_FR, Wheel_Controller__apply_torque_handler, 1);
        
        SetNetworkPriority(definition, global_brake_controller, Global_Brake_Controller__wheel_rpm_FR_port, 3);
       
        
        
        SetNetworkDelay(definition, wheel_FR, global_brake_controller, Global_Brake_Controller__wheel_rpm_FR_port, CAN__dealy_const);


        SetNetworkDelay(definition, global_brake_controller, wheel_controller_FR, Wheel_Controller__apply_torque_handler, CAN__dealy_const);

        

        definition.SetEventSystemVariablePoolSize(1);
        
        SingleCommunicationRealVariablePool globalPool = new SingleCommunicationRealVariablePool();
         
        Reserve(globalPool,wheel_FR, Wheel__torque_port,1);
        Reserve(globalPool, wheel_controller_FR, Wheel_Controller__wheel_speed_port, 1);     
        Reserve(globalPool, wheel_controller_FR, Wheel_Controller__apply_torque_handler, 2);
        Reserve(globalPool, global_brake_controller, Global_Brake_Controller__wheel_rpm_FR_port, 1);
        Reserve(globalPool, global_brake_controller, Global_Brake_Controller__brake_percent_port, 1);
        
        definition.SetInitialGlobalVariablePool(globalPool);
        
        
        return definition;
    }
    

    private static void FillSkeletonForWheelType(PhysicalActorType wheelType, ActorType wheelControllerType)
    {
        wheelType.AddInstanceParameter(new InstanceParameter(Wheel__controller_instance, wheelControllerType));
        wheelType.AddDelegationParameter(new DelegationParameter(
                        Wheel__speed_delegation, 
                        DelegationParameter.TypesFrom(Variable.Type.floatingPoint)
                )
        ); 
                
        wheelType.AddVariable(new RealVariable(Wheel__timer));
        wheelType.AddVariable(new RealVariable(Wheel__speed));
        wheelType.AddVariable(new FloatVariable(Wheel__torque));

        wheelType.AddMode(new Mode(Wheel__brake_mode));
        
        wheelType.SetInitialMode(wheelType.FindMode(Wheel__brake_mode));
        
        AddPort(wheelType, Wheel__torque_port, wheelType.FindVariable(Wheel__torque));
    }
    
    private static void FillFleshForWheelType(PhysicalActorType wheelType, MessageHandler wheel_speed_port)
    {
        InstanceParameter controllerInstance = wheelType.FindInstanceParameter(Wheel__controller_instance); 
        
        DelegationParameter wheel_rpm_delegation = wheelType.FindDelegationParameter(Wheel__speed_delegation);
        
        RealVariable timer = (RealVariable) wheelType.FindVariable(Wheel__timer); 
        RealVariable speed = (RealVariable) wheelType.FindVariable(Wheel__speed);
        FloatVariable torque = (FloatVariable) wheelType.FindVariable(Wheel__torque);

        
        Mode brakeMode = wheelType.FindMode(Wheel__brake_mode);
        
 
        
        brakeMode.SetInvarient(new Invarient(CreateBinaryExpression(timer, "<=", Const(Wheel__period_const))));
        brakeMode.SetGuard(CreateGuard(timer, "==", Wheel__period_const));
        brakeMode.AddDifferentialEquation(new DifferentialEquation(timer, Const(1f)));
        brakeMode.AddDifferentialEquation(new DifferentialEquation(speed, CreateBinaryExpression(Const(-0.1f),"-", ExpressionFrom(torque))));
        //brakeMode.AddDifferentialEquation(new DifferentialEquation(speed, Const(-0.1f)));
        
        brakeMode.AddAction(CreateResetFor(timer));
        brakeMode.AddAction(CreateSendStatement(controllerInstance, wheel_speed_port, VariableExpression(speed)));
        brakeMode.AddAction(CreateSendStatement(wheel_rpm_delegation, VariableExpression(speed)));
        //brakeMode.AddAction(CreateModeChangeStatement(brakeMode));
        brakeMode.AddAction(new IfStatement(
            CreateBinaryExpression(speed, ">=", Const(0f)),
            Statement.StatementsFrom(CreateModeChangeStatement(brakeMode)), 
            Statement.EmptyStatements()));
    }
    
    private static void FillSkeletonForWheelControllerType(SoftwareActorType wheelControllerType, ActorType wheelType)
    {       
        wheelControllerType.AddInstanceParameter(new InstanceParameter(Wheel_Controller__wheel_instance, wheelType));

        wheelControllerType.AddVariable(new FloatVariable(Wheel_Controller__wheel_speed));
        wheelControllerType.AddVariable(new FloatVariable(Wheel_Controller__slip_rate));
        
        MessageHandler applyTorque = new MessageHandler(Message.MessageType.Control);
        
        AddParameter(applyTorque, Wheel_Controller__requested_torque, FloatVariable.class);
        AddParameter(applyTorque, Wheel_Controller__estimated_speed, FloatVariable.class);
        wheelControllerType.AddMessageHandler(Wheel_Controller__apply_torque_handler, applyTorque);
        
        AddPort(wheelControllerType, Wheel_Controller__wheel_speed_port, Wheel_Controller__wheel_speed);        
    }
    
    private static void FillFleshForWheelControllerType(SoftwareActorType wheelControllerType, Mode brakeMode, MessageHandler wheel_torque_port)
    {
        InstanceParameter wheel = wheelControllerType.FindInstanceParameter(Wheel_Controller__wheel_instance);
        FloatVariable wheel_speed = (FloatVariable) wheelControllerType.FindVariable(Wheel_Controller__wheel_speed);
        FloatVariable slip_rate = (FloatVariable) wheelControllerType.FindVariable(Wheel_Controller__slip_rate);
        
        MessageHandler applyTorque = wheelControllerType.FindMessageHandler(Wheel_Controller__apply_torque_handler);
        FloatVariable requested_torque = (FloatVariable) applyTorque.Parameters().Find(Wheel_Controller__requested_torque).Variable();
        FloatVariable vehicle_speed = (FloatVariable) applyTorque.Parameters().Find(Wheel_Controller__estimated_speed).Variable();
        
//        applyTorque.AddStatement(
//                new IfStatement(
//                        CreateBinaryExpression(vehicle_speed, ">", Const(0f)), 
//                        Statement.StatementsFrom(new AssignmentStatement(slip_rate, 
//                            CreateBinaryExpression(
//                                    CreateBinaryExpression(
//                                            vehicle_speed, 
//                                            "-", 
//                                            CreateBinaryExpression(
//                                                    wheel_speed,
//                                                    "*", 
//                                                    Const(Wheel_Controller__wheel_radius_const))),
//                                    "/", 
//                                        CreateBinaryExpression(vehicle_speed, Wheel__torque, e2) VariableExpression(vehicle_speed)))), 
//                        Statement.StatementsFrom(new AssignmentStatement(slip_rate, Const(0f)))));
        
        applyTorque.AddStatement(new AssignmentStatement(slip_rate, 
                CreateBinaryExpression(
                        CreateBinaryExpression(
                                vehicle_speed, 
                                "-", 
                                CreateBinaryExpression(
                                        wheel_speed,
                                        "*", 
                                        Const(Wheel_Controller__wheel_radius_const))),
                        "/", 
                        Const(1f))));
        
//        applyTorque.AddStatement(new AssignmentStatement(slip_rate, 
//                CreateBinaryExpression(
//                        CreateBinaryExpression(
//                                vehicle_speed, 
//                                "-", 
//                                CreateBinaryExpression(
//                                        wheel_speed,
//                                        "*", 
//                                        Const(Wheel_Controller__wheel_radius_const))),
//                        "/", 
//                        VariableExpression(vehicle_speed))));
        
        
        //applyTorque.AddStatement(CreateSendStatement(wheel, wheel_torque_port , VariableExpression(requested_torque)));
        applyTorque.AddStatement(new IfStatement(
                  new BinaryExpression(
                                new VariableExpression(slip_rate),
                                new GreaterOperator(),
                                Const(0.2f)),
                Statement.StatementsFrom(CreateSendStatement(wheel, wheel_torque_port , Const(0.0f))), 
                Statement.StatementsFrom(
                        CreateSendStatement(wheel, wheel_torque_port , VariableExpression(requested_torque)))
        ));
        
        
    }

    private static void FillSkeletonForGlobalBrakeControllerType(SoftwareActorType globalBrakeControllerType, SoftwareActorType wheelControllerType)
    {
        globalBrakeControllerType.AddInstanceParameter(new InstanceParameter(Global_Brake_Controller__wheel_controller_FR_Instance, wheelControllerType));
        
        globalBrakeControllerType.AddVariable(new FloatVariable(Global_Brake_Controller__wheel_speed_FR));
        globalBrakeControllerType.AddVariable(new FloatVariable(Global_Brake_Controller__brake_percent));
        globalBrakeControllerType.AddVariable(new FloatVariable(Global_Brake_Controller__estimated_speed));
        globalBrakeControllerType.AddVariable(new FloatVariable(Global_Brake_Controller__global_torque));
        
        AddPort(globalBrakeControllerType, Global_Brake_Controller__wheel_rpm_FR_port, globalBrakeControllerType.FindVariable(Global_Brake_Controller__wheel_speed_FR));
        AddPort(globalBrakeControllerType, Global_Brake_Controller__brake_percent_port, globalBrakeControllerType.FindVariable(Global_Brake_Controller__brake_percent));
        
        globalBrakeControllerType.AddMessageHandler(Global_Brake_Controller__control_handler, new MessageHandler(Message.MessageType.Control));
    }

    private static void FillFleshForGlobalBrakeControllerType(SoftwareActorType globalBrakeControllerType, MessageHandler wheelControllerApply)
    {
        InstanceParameter wheel_controller_FR = globalBrakeControllerType.FindInstanceParameter(Global_Brake_Controller__wheel_controller_FR_Instance);

        
        FloatVariable global_torque = (FloatVariable) globalBrakeControllerType.FindVariable(Global_Brake_Controller__global_torque);
        FloatVariable wheel_speed_FR =  (FloatVariable) globalBrakeControllerType.FindVariable(Global_Brake_Controller__wheel_speed_FR);

        FloatVariable brake_percent = (FloatVariable) globalBrakeControllerType.FindVariable(Global_Brake_Controller__brake_percent);
        FloatVariable estimated_speed = (FloatVariable) globalBrakeControllerType.FindVariable(Global_Brake_Controller__estimated_speed);
        
        MessageHandler control = globalBrakeControllerType.FindMessageHandler(Global_Brake_Controller__control_handler);
        
        control.AddStatement(new AssignmentStatement(
                estimated_speed, VariableExpression(wheel_speed_FR))); 
        
        control.AddStatement(new AssignmentStatement(global_torque, CreateBinaryExpression(brake_percent, "/", Const(100f)) ));
        
        
        control.AddStatement(CreateSendStatement(wheel_controller_FR, wheelControllerApply, VariableExpression(global_torque), VariableExpression(estimated_speed)));
    }
    
    private static void FillWheelActor(
            PhysicalActor wheel,
            SoftwareActor wheelController,
            SoftwareActor global_brake_controller,
            String delegationHandlerName)
    {
        
        BindInstance(wheel,Wheel__controller_instance, wheelController, CommunicationType.Wire );
        
        BindDelagation(wheel, Wheel__speed_delegation, global_brake_controller, delegationHandlerName, CommunicationType.CAN);
    }
    
    private static void FillWheelControllerActor(SoftwareActor wheel_controller, PhysicalActor wheel)
    {
        BindInstance(wheel_controller, Wheel_Controller__wheel_instance, wheel, CommunicationType.Wire);        
    }

    private static void FillGlobalBrakeController(
            SoftwareActor global_brake_controller,
            SoftwareActor wheel_controller_FR)
    {
        SoftwareActorType type = global_brake_controller.Type();
        
        BindInstance(
                global_brake_controller,
                Global_Brake_Controller__wheel_controller_FR_Instance,
                wheel_controller_FR,
                CommunicationType.CAN);    
    }


}
