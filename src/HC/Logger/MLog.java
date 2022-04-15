package HC.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import HC.Entities.TPatient;

public class MLog implements ILog_ClientHandler, ILog_Patient {
    private ReentrantLock rl;
    private final BufferedWriter logFile;
    private final String fileName = "log.txt";
    private final List<String> headers;

    public MLog() throws IOException {
        new File(this.fileName);
        this.logFile = new BufferedWriter(new FileWriter(fileName));
        this.rl = new ReentrantLock();
        this.headers = new ArrayList<>(Arrays.asList("STT", "ETH", "ET1", "ET2", "EVR1", "EVR2",
            "EVR3", "EVR4", "WTH", "WTR1", "WTR2", "MDH", "MDR1", "MDR2", "MDR3", "MDR4", "PYH", "OUT"));
    }

    public void writeHeaders() {
        write(" STT | ETH ET1 ET2 | EVR1 EVR2 EVR3 EVR4 | WTH  WTR1 WTR2 | MDH  MDR1 MDR2 MDR3 MDR4 | PYH | OUT");
    }

    public void writeState(String message) {
        write(String.format(" %-4s|%-13s|%-21s|%-16s|%-26s|%-4s", message, "", "", "", "", ""));
    }
    
    public void writePatient(TPatient patient, String room) {
        var args = new String[headers.size()];
        Arrays.fill(args, "");
        int index = headers.indexOf(room);
        if (index == -1) {
            throw new IllegalArgumentException("Room not recognized.");
        }
        args[index] = patient.toString();
        write(String.format(" %-4s| %-4s%-4s%-4s| %-5s%-5s%-5s%-5s| %-5s%-5s%-5s| %-5s%-5s%-5s%-5s%-5s| %-4s | %-4s",
                (Object[]) args));
    }

    public void write(String message){
        rl.lock();

        System.out.println(message);

        try {
            this.logFile.write(message);
            this.logFile.newLine();
            this.logFile.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rl.unlock();
    }
}
