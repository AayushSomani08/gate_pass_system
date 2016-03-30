package vvv.gatepass;

/**
 * Created by Pradumn K Mahanta on 28-03-2016.
 */
public class GatepassListViewItem {

    public final String gp_GatepassNumber, gp_StudentName, gp_UserName, gp_RequestStatus, gp_RequestTo,
    gp_EnrollmentNo, gp_OutDate, gp_OutTime, gp_InDate, gp_InTime, gp_VisitPlace, gp_VisitType, gp_ContactNo, gp_Purpose, gp_Reason;

    public GatepassListViewItem(String gp_GatepassNumber, String gp_StudentName, String gp_UserName, String gp_RequestStatus,
                                String gp_RequestTo, String gp_EnrollmentNo, String gp_OutDate, String gp_OutTime,
                                String gp_InDate, String gp_InTime, String gp_VisitPlace, String gp_VisitType, String gp_ContactNo,
                                String gp_Purpose, String gp_Reason) {

            this.gp_GatepassNumber = gp_GatepassNumber;
            this.gp_StudentName = gp_StudentName;
            this.gp_UserName = gp_UserName;
            this.gp_RequestStatus = gp_RequestStatus;
            this.gp_RequestTo = gp_RequestTo;
            this.gp_EnrollmentNo = gp_EnrollmentNo;
            this.gp_OutDate = gp_OutDate;
            this.gp_OutTime = gp_OutTime;
            this.gp_InDate = gp_InDate;
            this.gp_InTime = gp_InTime;
            this.gp_VisitPlace = gp_VisitPlace;
            this.gp_VisitType = gp_VisitType;
            this.gp_ContactNo = gp_ContactNo;
            this.gp_Purpose = gp_Purpose;
            this.gp_Reason = gp_Reason;
        }
}
