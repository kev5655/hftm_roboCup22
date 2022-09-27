package com.robotino.game;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.eventBus.intern.OrderBus;
import com.robotino.eventBus.intern.OrderEvent;
import org.robocup_logistics.llsf_msgs.ProductColorProtos;

import java.util.ArrayList;
import java.util.List;

/**
 * Order Daten Klasse
 */
public class Order {

    public static final List<Order> orderList = new ArrayList<>();

    private final int id;
    private final MachineClientUtils.BaseColor baseColor;
    private final CapColor capColor;
    private final List<MachineClientUtils.RingColor> ringColors = new ArrayList<>();
    private final Type type;
    private final int howManySameOrder;
    private final int deliveryGate;
    private final int deliveryPeriodBegin;
    private final int deliveryPeriodEnd;

    public Order(int id,
                 String baseColor,
                 String capColor,
                 List<ProductColorProtos.RingColor> ringColorsList,
                 String type,
                 int quantityRequested,
                 int deliveryGate,
                 int deliveryPeriodBegin,
                 int deliveryPeriodEnd) {


        this.id = id;
        switch (baseColor) {
            case "BASE_RED" -> this.baseColor = MachineClientUtils.BaseColor.BASE_RED;
            case "BASE_SILVER" -> this.baseColor = MachineClientUtils.BaseColor.BASE_SILVER;
            case "BASE_BLACK" -> this.baseColor = MachineClientUtils.BaseColor.BASE_BLACK;
            default -> throw new IllegalArgumentException("Es gibt keine BaseColor mit dem String: " + baseColor);
        }
        switch (capColor) {
            case "CAP_BLACK" -> this.capColor = CapColor.Black;
            case "CAP_GREY" -> this.capColor = CapColor.Grey;
            default -> throw new IllegalArgumentException("Es gibt keine CapColor mit dem String: " + capColor);
        }

        for(ProductColorProtos.RingColor color : ringColorsList){
            switch (color.toString()){
                case "RING_ORANGE" -> this.ringColors.add(MachineClientUtils.RingColor.RING_ORANGE);
                case "RING_BLUE" -> this.ringColors.add(MachineClientUtils.RingColor.RING_BLUE);
                case "RING_YELLOW" -> this.ringColors.add(MachineClientUtils.RingColor.RING_YELLOW);
                case "RING_GREEN" -> this.ringColors.add(MachineClientUtils.RingColor.RING_GREEN);
                default -> throw new IllegalArgumentException("Es gibt keine RingColor mit dem String: " + color);
            }
        }

        switch (type) {
            case "C0" -> this.type = Type.C0;
            case "C1" -> this.type = Type.C1;
            case "C2" -> this.type = Type.C2;
            case "C3" -> this.type = Type.C3;
            default -> throw new IllegalArgumentException("Es gibt kein type mit dem String: " + type);
        }

        this.howManySameOrder = quantityRequested;
        this.deliveryGate = deliveryGate;
        this.deliveryPeriodBegin = deliveryPeriodBegin;
        this.deliveryPeriodEnd = deliveryPeriodEnd;

        orderList.add(this);

        OrderBus.getInstance().publish(new OrderEvent((this)));
    }

    public enum Type {
        C0,
        C1,
        C2,
        C3
    }


    public enum CapColor{
        Black,
        Grey
    }


    public int getId() { return id; }
    public MachineClientUtils.BaseColor getBaseColor() { return baseColor; }
    public CapColor getCapColor() { return capColor; }
    public List<MachineClientUtils.RingColor> getRingColors() { return ringColors; }
    public Type getType() { return type; }
    public int getHowManySameOrder() { return howManySameOrder; }
    public int getDeliveryGate() { return deliveryGate; }
    public int getDeliveryPeriodBegin() { return deliveryPeriodBegin; }
    public int getDeliveryPeriodEnd() { return deliveryPeriodEnd; }
}
