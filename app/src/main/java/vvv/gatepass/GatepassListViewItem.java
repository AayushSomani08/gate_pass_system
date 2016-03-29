package vvv.gatepass;

/**
 * Created by Pradumn K Mahanta on 28-03-2016.
 */
public class GatepassListViewItem {

    public final String gp_Number;
    public final String gp_Status;
    public final String gp_inTime;
    public final String gp_outTime;



        public GatepassListViewItem(String gp_Number, String gp_Status, String gp_inTime, String gp_outTime) {
            this.gp_Number = gp_Number;
            this.gp_Status = gp_Status;
            this.gp_inTime = gp_inTime;
            this.gp_outTime = gp_outTime;
        }
}
