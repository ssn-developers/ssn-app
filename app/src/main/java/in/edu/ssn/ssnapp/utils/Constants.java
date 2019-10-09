package in.edu.ssn.ssnapp.utils;

// All the immutable constants must be included in this file

import in.edu.ssn.ssnapp.R;

public class Constants {

    //TODO: change to 'false' when releasing the app
    public static final Boolean debug_mode = true;

    public static final String PDF_URL = "pdf_url";
    public static final String CipherKey="SSN_APP";

    // sqlCipher database related function
    public static final String DATABASE_NAME="SSNDB";
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_PWD="TEST_PWD";

    //URLs
    public static final String calendar = "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/utils%2FCollege%20Calendar%202019%20-20.pdf?alt=media&token=c99c10cd-c124-405c-9a8d-ef6f39ee5e56";
    public static final String termsfeed = "https://www.termsfeed.com/privacy-policy/77e3f0a8a5b350afc54dc2b8c2af568b";

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
    public static String collection_club="club";
    public static String collection_feedback="feedback";

    public static String collection_exam_cell = "examcell";
    public static String collection_placement = "placement";
    public static String collection_post = "post";
    public static String collection_post_bus = "post_bus";
    public static String collection_post_club = "post_club";
    public static String collection_workshop = "workshop";

    // What's new stuff
    public static int versionCode = 9;
    public static String versionName = "1.18";
    public static String changelog = "\u2022 Dark mode added\n\u2022 Bug fixes\n\u2022 Performance improvements";
}