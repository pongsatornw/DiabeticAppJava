package com.example.yggdrasil.realdiabeticapp;

/**
 * Defines several constants used between {@link BluetoothChatService} and the UI.
 */

public class Constants {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_RESULT = 6;
    public static final int MESSAGE_SN = 7;
    public static final int MESSAGE_PROGRESS_MAX = 8;
    public static final int MESSAGE_PROGRESS = 9;
    public static final int MESSAGE_SYNC_COMPLETE = 10;
    public static final int MESSAGE_RECORD_SYNC_CONFIRM = 11;
    public static final int MESSAGE_RECORD_CONFIRM = 12;
    public static final int MESSAGE_CLOSE = 13;
    public static final int MESSAGE_RECORD_DELETE_ALL = 14;
    public static final int MESSAGE_RECORD_COUNT = 15;
    public static final int MESSAGE_READ_START = 16;
    public static final int MESSAGE_READ_CONTINUE = 17;
    public static final int MESSAGE_READ_END = 18;
    public static final int MESSAGE_ERROR = 99;
    public static final int STATUS_READ_DATA = 1;
    //public static final int
    //public static final int
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    /**
     *  -->Collection
            |-->Document
    */
    public static  final String Collect_Profile = "Profile_Collection";
        public static final String Document_User = "UserProfile_Document";
        public static final String Document_Caregiver = "CaregiverProfile_Document";

    private static final String Collect_Account = "Account_Collection";
        protected static final String Document_OfflineAccount = "OfflineAccount_Document";

    public static final String Collect_GValue = "GValue_Collection";
        public static final String Document_GValue = "Document_GValue";


    /**
     * Constants for FirebaseFirestore
     */

    public class Firestore{
        public static final String firstname = "First_Name";
        public static final String lastname = "Last_Name";
        public static final String birthdate = "Birthdate";
        public static final String gender = "Gender";
        public static final String tel = "Telephone_Number";
        public static final String email = "Email";
        public static final String date = "Date";
        public static final String time = "Time";
        public static final String value = "Value";
        public static final String measured = "Measured_Activity";
        public static final String status = "BloodGlucose_Status";
    }
}
