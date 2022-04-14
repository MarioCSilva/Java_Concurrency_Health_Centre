package HC.WaitingHall;

import HC.CallCentreHall.ICallCentreHall_WaitingHall;
import HC.Entities.TPatient;
import HC.Enumerates.DoS;
import HC.FIFO.MFIFO;
import HC.Logger.ILog_WaitingHall;

import java.util.concurrent.locks.ReentrantLock;

public class MWaitingHall implements IWaitingHall_CallCenter, IWaitingHall_Patient {

    private final ReentrantLock entranceLock;
    private int WTN = 0;
    private final int NUM_FIFOS = 3;
    private final ILog_WaitingHall logger;
    private final ICallCentreHall_WaitingHall ccw;
    // 3 FIFOS for each WTR, each index corresponds to an index of Severity (Higher to lower)
    private final MFIFO WTR1_FIFOs[];
    private  final MFIFO WTR2_FIFOs[];


    private  final int size;
    public MWaitingHall(int nos, ILog_WaitingHall logger, ICallCentreHall_WaitingHall ccw){
        this.logger = logger;
        this.entranceLock = new ReentrantLock();
        this.ccw = ccw;
        this.size = nos;
        this.WTR1_FIFOs = new MFIFO[NUM_FIFOS];
        this.WTR2_FIFOs = new MFIFO[NUM_FIFOS];
        for(int i =0; i< NUM_FIFOS; i++){
            WTR1_FIFOs[i] = new MFIFO(nos);
            WTR2_FIFOs[i] = new MFIFO(nos);
        }
    }

    public  void enterHall(TPatient patient){
        try {
            entranceLock.lock();
            patient.setWTN(WTN);
            WTN++;
        } finally {
            entranceLock.unlock();
        }

        logger.writePatient(patient, "WTH");
        //TODO: Notify Call Center

        MFIFO[] patientFIFOs;

        if (patient.getIsAdult()) {
            patientFIFOs = WTR2_FIFOs;
            logger.writePatient(patient,"WTR2");
        } else {
            patientFIFOs = WTR1_FIFOs;
            logger.writePatient(patient,"WTR1");
        }

        if(patient.getDoS() == DoS.RED) {
            patientFIFOs[0].put(patient);
        } else if(patient.getDoS() == DoS.YELLOW) {
            patientFIFOs[1].put(patient);
        } else if(patient.getDoS() == DoS.BLUE) {
            patientFIFOs[2].put(patient);
        }
    }




}
