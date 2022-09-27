package com.robotino.communication.refbox.send;

import com.grips.model.teamserver.MachineClientUtils;
import com.grips.refbox.RefboxClient;
import com.robotino.communication.refbox.RefboxConnection;

/**
 * Mit dieser Klasse können die Machine zurückgesetzt werden könnte höchsten bei einem neustart verwendet werden,
 * oder zum Testen.
 */
public class ResetMachineSignal {

    private final RefboxClient client;

    private static final ResetMachineSignal RESET_MACHINE_SIGNAL_INSTANCE = new ResetMachineSignal();
    public static ResetMachineSignal getInstance() { return RESET_MACHINE_SIGNAL_INSTANCE; }
    private ResetMachineSignal(){
        client = RefboxConnection.getInstance().getClient();
    }

    public void startSendingResetMachineSignal(MachineClientUtils.Machine machine){
        client.sendResetMachine(machine);
    }



}
