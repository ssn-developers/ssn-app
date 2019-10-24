package in.edu.ssn.ssnapp.utils;

// All the immutable constants must be included in this file

public class Constants {

    //TODO: change to 'false' when releasing the app
    public static final Boolean debug_mode = false;

    public static final String PDF_URL = "pdf_url";

    // sqlCipher database related function
    public static final String DATABASE_NAME="SSNDB";
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_PWD="TEST_PWD";

    //URLs
    public static final String calendar = "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/utils%2FCollege%20Calendar%202019%20-20.pdf?alt=media&token=c99c10cd-c124-405c-9a8d-ef6f39ee5e56";
    public static final String termsfeed = "https://www.termsfeed.com/privacy-policy/77e3f0a8a5b350afc54dc2b8c2af568b";

    // FCM topics
    public static final String BUS_ALERTS="busalerts";
    public static final String Event="event";

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
    public static final int event = 6;

    // collection name
    public static String collection_club="club";
    public static String collection_feedback="feedback";

    public static String collection_exam_cell = "examcell";
    public static String collection_placement = "placement";
    public static String collection_post = "post";
    public static String collection_post_bus = "post_bus";
    public static String collection_post_club = "post_club";
    public static String collection_event = "event";

    //TODO: modify when releasing the app
    //What's new stuff
    public static String changelog = "\u2022 Bug fixes\n\u2022 PG and Alumni login integrated\n\u2022 Helpline added\n\u2022 Placement notification enabled\n\u2022 UI/UX improvements\n\u2022 Performance improvements";
}