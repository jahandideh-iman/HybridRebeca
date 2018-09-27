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
import java.util.Queue;

/**
 *
 * @author Iman Jahandideh
 */
public class BrakeByWireModelSingleWheelSimplified
{
    public static final boolean ADD_TIME_PROPERTY_MONITOR = true;
    public static final boolean ADD_SAFETY_PROPERTY_MONITOR = false;
    
    public static final float arbitrartDelay = 13.0f;
    public static final String Wheel__controller_instance = "controller";
    public static final String Wheel__speed_delegation = "wheel_speed_delegation";
    public static final String Wheel__torque_port = "torque_port";
    public static final String Wheel__speed = "speed";
    public static final String Wheel__torque = "torque";
    public static final String Wheel__timer = "timer";
    public static final String Wheel__brake_mode = "Brake";
    public static final float Wheel__period_const = 0.05f;
    
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

    
    public static final String Brake__controller_instance = "controller";
    public static final String Brake__brake_percent = "brake_percent";
    public static final String Brake__max_brake_percent = "max_brake_percent";
    public static final String Brake__timer = "timer";
    public static final String Brake__increasing_brake = "IncreasingBrake";
    public static final String Brake__constant_brake = "ConstantBrake";
    public static final float Brake__period_const = 0.05f;
    public static final float Brake__brake_rate_const = 1f;
    
    public static final String Clock__callback = "callback";
    public static final float Clock__period_const = 0.05f;
    
    public static final float CAN__dealy_const = 0.01f;
    
    public static final String Time_Monitor__time = "time";
    public static final String Time_Monitor__rate = "rate";
    public static final String Time_Monitor__stop = "stop";    
    public static final String Time_Monitor__start = "start";
    public static final String Time_Monitor__monitoring = "Monitoring";


    public static ModelDefinition Create()
    {
        ModelDefinition definition = new ModelDefinition();
        
        PhysicalActorType wheelType = new PhysicalActorType("Wheel");
        SoftwareActorType wheelControllerType = new SoftwareActorType("WheelController");
        PhysicalActorType brakeType = new PhysicalActorType("Brake");
        SoftwareActorType globalBrakeControllerType = new SoftwareActorType("GlobalBrakeController");
        PhysicalActorType clockType = new PhysicalActorType("Clock");
        
        FillSkeletonForWheelType(wheelType, wheelControllerType);
        FillSkeletonForWheelControllerType(wheelControllerType, wheelType);
        FillSkeletonForBrakeType(brakeType, globalBrakeControllerType);
        FillSkeletonForGlobalBrakeControllerType(globalBrakeControllerType, wheelControllerType);
        FillClockType(clockType);
        
        FillFleshForWheelType(wheelType, wheelControllerType.FindMessageHandler(Wheel_Controller__wheel_speed_port));
        FillFleshForWheelControllerType(wheelControllerType, wheelType.FindMode(Wheel__brake_mode), wheelType.FindMessageHandler(Wheel__torque_port));
        FillFleshForBrakeType(brakeType, globalBrakeControllerType.FindMessageHandler(Global_Brake_Controller__brake_percent_port));
        FillFleshForGlobalBrakeControllerType(globalBrakeControllerType, wheelControllerType.FindMessageHandler(Wheel_Controller__apply_torque_handler));
        
        PhysicalActor brake_pedal = new PhysicalActor("brake", brakeType, 1);
        SoftwareActor global_brake_controller = new SoftwareActor("global_brake_controller", globalBrakeControllerType, 1);
        
        SoftwareActor wheel_controller_FR = new SoftwareActor("wheel_controller_FR",wheelControllerType, 1);     


        PhysicalActor wheel_FR = new PhysicalActor("wheel_FR", wheelType,1);       

        
        PhysicalActor clock = new PhysicalActor("clock", clockType,1);
        
        FillWheelActor(wheel_FR,wheel_controller_FR,global_brake_controller,Global_Brake_Controller__wheel_rpm_FR_port);
        
  
        FillWheelControllerActor(wheel_controller_FR, wheel_FR);
 
        FillGlobalBrakeController(
                global_brake_controller,
                wheel_controller_FR);
        
        FillBrake(brake_pedal, global_brake_controller);
        
        FillClock(clock, global_brake_controller, Global_Brake_Controller__control_handler);

        definition.AddType(wheelType);
        definition.AddType(wheelControllerType);
        definition.AddType(brakeType);
        definition.AddType(globalBrakeControllerType);
        definition.AddType(clockType);
        
        definition.AddActor(brake_pedal);
        definition.AddActor(global_brake_controller);
        
        definition.AddActor(wheel_controller_FR);
        
        definition.AddActor(wheel_FR);
        
        definition.AddActor(clock);
        
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
        
        
        PhysicalActorType monitorType = new PhysicalActorType("Monitor");
        FillMonitorType(monitorType);
        
        if(ADD_TIME_PROPERTY_MONITOR)
        {
            PhysicalActor timeMonitor = new PhysicalActor("timeMonitor", monitorType, 1);
            
            InstanceParameter brakeParam = new InstanceParameter("timeMonitor", monitorType);
            brakeType.AddInstanceParameter(brakeParam);
            Mode brakeIncreasingMode = brakeType.FindMode(Brake__increasing_brake);
            Mode brakeConstantMode = brakeType.FindMode(Brake__constant_brake);
            
            brakeIncreasingMode.AddAction(
                    CreateSendStatement(brakeParam, monitorType.FindMessageHandler(Time_Monitor__start)));
            brakeConstantMode.AddAction(
                    CreateSendStatement(brakeParam, monitorType.FindMessageHandler(Time_Monitor__start)));
            
            InstanceParameter wheelParam = new InstanceParameter("timeMonitor", monitorType);
            wheelType.AddInstanceParameter(wheelParam);
            MessageHandler wheelSetTorqueHandler = wheelType.FindMessageHandler(Wheel__torque_port);
            wheelSetTorqueHandler.AddStatement(CreateSendStatement(wheelParam, monitorType.FindMessageHandler(Time_Monitor__stop)));
            
            brake_pedal.BindInstance(brakeParam, timeMonitor, CommunicationType.Wire);
            wheel_FR.BindInstance(wheelParam, timeMonitor, CommunicationType.Wire);
            
            definition.AddActor(timeMonitor); 
        }
        
        if(ADD_SAFETY_PROPERTY_MONITOR)
        {

            PhysicalActor timeMonitor = new PhysicalActor("safetyMonitor", monitorType, 2);
            
            InstanceParameter wheelControllerParam = new InstanceParameter("safetyMonitor", monitorType);
            
            MessageHandler wheelControllerApplyHandler = wheelControllerType.FindMessageHandler(Wheel_Controller__apply_torque_handler);
            
            wheelControllerType.AddInstanceParameter(wheelControllerParam);
  
            wheelControllerApplyHandler.AddStatement(CreateSendStatement(wheelControllerParam, monitorType.FindMessageHandler(Time_Monitor__start)));
//            for(Statement statement : wheelControllerApplyHandler.GetBody())
//            {
//                if(statement instanceof IfStatement)
//                {
//                    IfStatement ifStatement = (IfStatement) statement;
//                    BinaryExpression expression = (BinaryExpression) ifStatement.Expression();
//                    VariableExpression op1 = expression != null? (VariableExpression) expression.Operand1():null;
//                    Variable var = op1 != null? op1.Variable() : null;
//                    if(var != null && var.Name().equals(Wheel_Controller__slip_rate))
//                        ifStatement.TrueStatements().add(CreateSendStatement(wheelControllerParam, monitorType.FindMessageHandler(Time_Monitor__start)));
//                    
//                }
//            }

            InstanceParameter wheelParam = new InstanceParameter("timeMonitor", monitorType);
            wheelType.AddInstanceParameter(wheelParam);
            MessageHandler wheelSetTorqueHandler = wheelType.FindMessageHandler(Wheel__torque_port);
            wheelSetTorqueHandler.AddStatement(CreateSendStatement(wheelParam, monitorType.FindMessageHandler(Time_Monitor__stop)));
            
            wheel_controller_FR.BindInstance(wheelControllerParam, timeMonitor, CommunicationType.Wire);
            wheel_FR.BindInstance(wheelParam, timeMonitor, CommunicationType.Wire);
            
            definition.AddActor(timeMonitor);
        }
        
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
        
        Queue<Statement> truePath  = Statement.StatementsFrom();
        
        
        truePath.add(new AssignmentStatement(slip_rate, 
                    CreateBinaryExpression(
                                    vehicle_speed, 
                                    "-", 
                                    CreateBinaryExpression(
                                            wheel_speed,
                                            "*", 
                                            Const(Wheel_Controller__wheel_radius_const)))));
        
        //truePath.add(new AssignmentStatement(vehicle_speed, C)));
        //truePath.add(new AssignmentStatement(slip_rate, CreateBinaryExpression(slip_rate, "/",  CreateBinaryExpression(vehicle_speed, "+", Const(10f)))));
        
        truePath.add(new AssignmentStatement(slip_rate, CreateBinaryExpression(slip_rate, "/",  Const(2f))));
        truePath.add(new IfStatement(
                  new BinaryExpression(
                                new VariableExpression(slip_rate),
                                new GreaterOperator(),
                                Const(0.2f)),
                Statement.StatementsFrom(CreateSendStatement(wheel, wheel_torque_port , Const(0.0f))), 
                Statement.StatementsFrom(
                        CreateSendStatement(wheel, wheel_torque_port , VariableExpression(requested_torque)))
        ));
   
        applyTorque.AddStatement(new IfStatement(
                CreateBinaryExpression(vehicle_speed, ">", Const(0)), 
                truePath, 
                Statement.StatementsFrom(CreateSendStatement(wheel, wheel_torque_port , VariableExpression(requested_torque)))
                
        ));

        
        
    }

    private static void FillSkeletonForBrakeType(PhysicalActorType brakeType, SoftwareActorType globalBrakeControllerType)
    {
        brakeType.AddInstanceParameter(new InstanceParameter(Brake__controller_instance, globalBrakeControllerType));
        
        
        brakeType.AddVariable(new RealVariable(Brake__brake_percent));    
        brakeType.AddVariable(new FloatVariable(Brake__max_brake_percent));
        brakeType.AddVariable(new RealVariable(Brake__timer));
        
        brakeType.AddMode(new Mode(Brake__increasing_brake));    
        brakeType.AddMode(new Mode(Brake__constant_brake));

        brakeType.SetInitialMode(brakeType.FindMode(Brake__increasing_brake));
    }
    
    private static void FillFleshForBrakeType(PhysicalActorType brakeType, MessageHandler brakePercentPort)
    {
        InstanceParameter controller = brakeType.FindInstanceParameter(Brake__controller_instance);
        
        RealVariable brake_percent = (RealVariable) brakeType.FindVariable(Brake__brake_percent);
        FloatVariable max_brake_percent = (FloatVariable) brakeType.FindVariable(Brake__max_brake_percent);
        RealVariable timer = (RealVariable) brakeType.FindVariable(Brake__timer);
        
        Mode increasingBrakeMode = brakeType.FindMode(Brake__increasing_brake);
        Mode constantBrakeMode = brakeType.FindMode(Brake__constant_brake);
                
        increasingBrakeMode.SetInvarient(new Invarient(
                CreateBinaryExpression(timer, "<=", Const(Brake__period_const))));
        
        increasingBrakeMode.SetGuard(new Guard(CreateBinaryExpression(timer, "==", Const(Brake__period_const))));
        
        increasingBrakeMode.AddDifferentialEquation(new DifferentialEquation(timer, Const(1f)));
        increasingBrakeMode.AddDifferentialEquation(new DifferentialEquation(brake_percent, Const(Brake__brake_rate_const)));
        increasingBrakeMode.AddAction(new IfStatement(
                CreateBinaryExpression(brake_percent,"<",VariableExpression(max_brake_percent)),
                Statement.StatementsFrom(CreateModeChangeStatement(increasingBrakeMode)), 
                Statement.StatementsFrom(CreateModeChangeStatement(constantBrakeMode))
        ));
        increasingBrakeMode.AddAction(CreateResetFor(timer)); 
        increasingBrakeMode.AddAction(CreateSendStatement(controller, brakePercentPort, VariableExpression(brake_percent)));
        
        

        constantBrakeMode.SetInvarient(new Invarient(CreateBinaryExpression(timer, "<=", Const(Brake__period_const))));
        constantBrakeMode.SetGuard(CreateGuard(timer, "==", Brake__period_const));
        
        constantBrakeMode.AddDifferentialEquation(new DifferentialEquation(timer, Const(1f)));
        constantBrakeMode.AddDifferentialEquation(new DifferentialEquation(brake_percent, Const(0)));
        constantBrakeMode.AddAction(CreateResetFor(timer)); 
        constantBrakeMode.AddAction(CreateSendStatement(controller, brakePercentPort, VariableExpression(brake_percent)));
        constantBrakeMode.AddAction(CreateModeChangeStatement(constantBrakeMode));
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
    
    private static void FillClockType(PhysicalActorType clockType)
    {
        DelegationParameter callback = new DelegationParameter(Clock__callback);
        clockType.AddDelegationParameter(callback);
        
        RealVariable timer = new RealVariable("timer");
        
        clockType.AddVariable(timer);
        
        Mode runningMode = new Mode("Running");
        
        runningMode.SetInvarient(new Invarient(CreateBinaryExpression(timer, "<=", Const(Clock__period_const))));
        runningMode.SetGuard(CreateGuard(timer, "==", Clock__period_const));
                
        runningMode.AddDifferentialEquation(new DifferentialEquation(timer, Const(1f)));
        
        runningMode.AddAction(CreateModeChangeStatement(runningMode));
        runningMode.AddAction(CreateResetFor(timer));
        runningMode.AddAction(CreateSendStatement(callback));
        
        clockType.AddMode(runningMode);
        clockType.SetInitialMode(runningMode);
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

    private static void FillBrake(PhysicalActor brake, SoftwareActor global_brake_controller)
    {
        BindInstance(brake, Brake__controller_instance, global_brake_controller, CommunicationType.Wire);
    }

    private static void FillClock(PhysicalActor clock, SoftwareActor global_brake_controller, String controlHandler)
    {
        BindDelagation(clock, Clock__callback, global_brake_controller, controlHandler, CommunicationType.Wire);
    }

    private static void FillMonitorType(PhysicalActorType timeMonitorType)
    {
        RealVariable time = new RealVariable(Time_Monitor__time);
        FloatVariable rate = new FloatVariable(Time_Monitor__rate);
        
        timeMonitorType.AddVariable(time);
        timeMonitorType.AddVariable(rate);
        
        MessageHandler startHandler =  new MessageHandler(Message.MessageType.Control);
        MessageHandler stopHandler = new MessageHandler(Message.MessageType.Control);
        
        startHandler.AddStatement(new AssignmentStatement(rate, Const(1f)));
        startHandler.AddStatement(new AssignmentStatement(time, Const(0f)));
        
        stopHandler.AddStatement(new AssignmentStatement(rate, Const(0f)));
        
        timeMonitorType.AddMessageHandler(Time_Monitor__start, startHandler);
        timeMonitorType.AddMessageHandler(Time_Monitor__stop, stopHandler);
        
        Mode monitoringMode = new Mode(Time_Monitor__monitoring);
        
        monitoringMode.SetGuard(new Guard(new FalseConst()));
        monitoringMode.SetInvarient(new Invarient(new TrueConst()));
        monitoringMode.AddDifferentialEquation(new DifferentialEquation(time, VariableExpression(rate)));
        monitoringMode.AddAction(CreateModeChangeStatement(monitoringMode));
        
        timeMonitorType.AddMode(monitoringMode);
        timeMonitorType.SetInitialMode(monitoringMode);
        
    }
}
