package in.edu.ssn.ssnapp.utils;


// All the immutable constants must be included in this file

import in.edu.ssn.ssnapp.R;

public class Constants {

    public static final String PDF_URL = "pdf_url";
    public static final String CipherKey="SSN_APP";

    // sqlCipher database related function
    public static final String DATABASE_NAME="SSNDB";
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_PWD="TEST_PWD";

    // FCM topics
    public static final String BUS_ALERTS="busalerts";

    //year
    //create composite query accordingly when modified
    public static final String fourth="2016";
    public static final String third="2017";
    public static final String second="2018";
    public static final String first="2019";

    //Share URL type
    public static final int post = 1;
    public static final int placement = 2;
    public static final int club = 3;
    public static final int post_club = 4;
    public static final int exam_cell = 5;
    public static final int workshop = 6;

    // collection name
    public static final String collection_club="debug_club";
    public static final String collection_exam_cell="debug_exam_cell";
    public static final String collection_placement="debug_placement";
    public static final String collection_post="debug_post";
    public static final String collection_post_bus="debug_post_bus";
    public static final String collection_post_club="debug_post_club";
    public static final String collection_workshop="debug_workshop";
    public static final String collection_feedback="debug_feedback";
}
