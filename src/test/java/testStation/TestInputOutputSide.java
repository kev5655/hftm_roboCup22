package testStation;

public class TestInputOutputSide {
    /*
    public TestInputOutputSide(){
        String zone = "M_Z22";
        String inputZone = "M_Z12";
        String outputZone = "M_Z32";
        int angle = 0;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        //Station station = new Station("name", "type", "state", "teamColor", zone, "inputCoordinate", "outputCoordinate", angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z12 sein");
        Field field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z32 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
        zone = "M_Z22";
        inputZone = "M_Z32";
        outputZone = "M_Z12";
        angle = 180;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z32 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z12 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
//
//
        zone = "M_Z22";
        inputZone = "M_Z21";
        outputZone = "M_Z23";
        angle = 270;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z21 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z23 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
        zone = "M_Z22";
        inputZone = "M_Z32";
        outputZone = "M_Z12";
        angle = 180;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z32 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z12 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
//
//
        zone = "M_Z22";
        inputZone = "M_Z33";
        outputZone = "M_Z11";
        angle = 135;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z33 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z11 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
        zone = "M_Z22";
        inputZone = "M_Z11";
        outputZone = "M_Z33";
        angle = 315;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z11 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z33 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
//
//
        zone = "M_Z22";
        inputZone = "M_Z13";
        outputZone = "M_Z31";
        angle = 45;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z13 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z31 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET + "\n\n");
//
        zone = "M_Z22";
        inputZone = "M_Z31";
        outputZone = "M_Z13";
        angle = 225;
        System.out.println("TEST STATION ON: " + zone + " Winkel: " + angle);
        station = new Station("CS1", "RS","idle", "CYAN", zone, angle);
        System.out.println("Input von " + zone + " bie " + angle + " sollte: M_Z31 sein");
        field = new Field(station.getInputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(inputZone)) throw new IllegalArgumentException("InputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + inputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET);
        System.out.println("Output von " + zone + " bie " + angle + " sollte: M_Z13 sein");
        field = new Field(station.getOutputCoordinate());
        System.out.println(field);
        if(!field.getZone().equals(outputZone)) throw new IllegalArgumentException("OutputZone ist nicht korrekt ist: " + field.getZone() + " sollte sein: " + outputZone);
        else System.out.println(ConsoleColors.GREEN + "Passed" + ConsoleColors.RESET  + "\n\n");

    }*/



    public static void main(String[] args) {
        //new TestInputOutputSide();
    }
}
