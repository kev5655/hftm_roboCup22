package TestGripsProbobufLib;

import com.robotino.communication.refbox.RefboxConnection;

public class TestRefboxConn {


    public TestRefboxConn(){
        RefboxConnection.getInstance();
    }

    public static void main(String[] args) {
        new TestRefboxConn();
    }

}
